package moodss.ia.ray;

import moodss.ia.ImmersiveAudio;
import moodss.ia.openal.EAXReverbController;
import moodss.ia.sfx.api.filter.Filter;
import moodss.ia.user.ImmersiveAudioConfig;
import moodss.ia.util.ReflectivityUtil;
import moodss.plummet.math.MathUtils;
import moodss.plummet.math.vec.Vector3;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;

public class PathtracedAudio extends DebugBiDirectionalPathtracer {

    /**
     * Reflectivity of each block from every bounced ray
     */
    protected final float[] bounceReflectivityRatio;

    protected float[] gains;

    public PathtracedAudio(ImmersiveAudioConfig.Raytracing raytracing) {
        super(raytracing);

        this.bounceReflectivityRatio = new float[raytracing.maxRayBounceCount];
    }

    protected void clearBounceReflectivity() {
        Arrays.fill(this.bounceReflectivityRatio, 0.0F);
    }

    public CompletableFuture<Vector3> pathtrace(Vector3 origin, Vector3 listener,
                                                BiFunction<Ray, Vector3, RayHitResult> traceFunc,
                                                float maxDistance, Executor executor) {
        this.clearBounceReflectivity();
        float[] bounceReflectivityRatio = this.bounceReflectivityRatio;

        this.gains = new float[ImmersiveAudio.AUXILIARY_EFFECT_MANAGER.getMaxAuxiliaries()];
        EAXReverbController reverbController = ImmersiveAudio.EAX_REVERB_CONTROLLER;

        return super.pathtrace(origin, listener, traceFunc, maxDistance, executor).thenApplyAsync(position -> {
            for(int i = 0; i < bounceReflectivityRatio.length; ++i) {
                bounceReflectivityRatio[i] /= (float)this.maxRayCount;
            }

            float sharedAirspace = (this.strengthManager.getCurrentEntryIdx() * 64.0F) * this.MAX_RAY_COUNT_NORM;

            ImmersiveAudio.DEVICE.run(context -> {
                for(int idx = 0; idx < this.gains.length; ++idx) {
                    float sharedAirspaceWeight = MathUtils.clamp(sharedAirspace / (idx * 20.0F - 10.0F), 0.0F, 1.0F);

                    float sendGain = this.gains[idx];
                    float sendCutoff = (float)(Math.exp(-3.0) * (double)(1.0F - sharedAirspaceWeight) + (double)sharedAirspaceWeight);

                    sendGain *= bounceReflectivityRatio[idx];
                    sendGain = MathUtils.clamp(sendGain, 0.0F, 1.0F);
                    sendGain = (float) (sendGain * Math.pow(sendCutoff, 0.1F));

                    Filter filter = reverbController.getFilter(idx);
                    context.setGain(filter, sendGain);
                    context.setGainHF(filter, sendCutoff);
                }
            });

            return position;
        }, executor);
    }

    @Override
    protected void onRayBounceHit(RayHitResult result, Ray ray, Vector3 endPosition, int unit) {
        super.onRayBounceHit(result, ray, endPosition, unit);
        this.onRayBounceHit0((BlockRayHitResult)result, unit);
    }

    protected void onRayBounceHit0(BlockRayHitResult result, int unit) {
        float blockReflectivity = ReflectivityUtil.getReflectivity0(result);
        this.bounceReflectivityRatio[unit] += blockReflectivity;
    }

    @Override
    protected void onRayBounceMiss(Ray ray, Vector3 endPosition) {
        super.onRayBounceMiss(ray, endPosition);
    }

    @Override
    protected void onRayBounceFinish(RayHitResult result, int unit, float overallRayLength) {
        super.onRayBounceFinish(result, unit, overallRayLength);
        this.onRayBounceFinish0((BlockRayHitResult)result, unit, overallRayLength);
    }

    //TODO: Remake gain
    protected void onRayBounceFinish0(BlockRayHitResult result, int unit, float overallRayLength) {
        float blockReflectivity = ReflectivityUtil.getReflectivity0(result);
        float energyTowardsPlayer = 0.25F * (blockReflectivity * 0.75F + 0.25F);
        float reflectionDelay = Math.max(overallRayLength, 0.0F) * 0.12F * blockReflectivity;

        for(int idx = 0; idx < this.gains.length; ++idx) {
            float cross = 1.0F - MathUtils.clamp(Math.abs(reflectionDelay - 0.0F), 0.0F, 1.0F);
            this.gains[idx] = cross * energyTowardsPlayer * 6.4F * this.MAX_RAY_COUNT_NORM;
        }
    }
}
