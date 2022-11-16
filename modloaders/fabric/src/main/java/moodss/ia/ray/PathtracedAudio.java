package moodss.ia.ray;

import moodss.ia.ImmersiveAudioMod;
import moodss.ia.client.ImmersiveAudioClientMod;
import moodss.ia.openal.EAXReverbController;
import moodss.ia.ray.path.DebugBiDirectionalPathtracer;
import moodss.ia.ray.trace.Raytracer;
import moodss.ia.sfx.api.filter.Filter;
import moodss.ia.user.ImmersiveAudioConfig;
import moodss.plummet.math.MathUtils;
import moodss.plummet.math.vec.Vector3;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class PathtracedAudio extends DebugBiDirectionalPathtracer {

    protected float[] gains;

    private final ImmersiveAudioConfig config;

    public PathtracedAudio(ImmersiveAudioConfig config) {
        super(config.raytracing);

        this.gains = new float[config.audioResolution + 1];
        this.config = config;
    }

    protected void clear() {
        Arrays.fill(this.gains, 0F);
    }

    public CompletableFuture<Vector3> pathtrace(Vector3 origin,
                                                Vector3 listener,
                                                Raytracer tracer,
                                                float maxDistance,
                                                Executor executor) {
        this.clear();
        float[] gains = this.gains;

        int maxAuxiliary = ImmersiveAudioMod.instance().auxiliaryEffectManager().getMaxAuxiliaries();
        int maxRayBounceCount = this.config.raytracing.maxRayBounceCount;
        EAXReverbController reverbController = ImmersiveAudioMod.instance().eaxReverbController();

        return super.pathtrace(origin, listener, tracer, maxDistance, executor).thenApplyAsync(position -> {
            ImmersiveAudioClientMod.DEVICE.run(context -> {
                for(int idx = 0; idx < maxAuxiliary; idx++) {
                    float sendGain = MathHelper.clamp(gains[MathUtils.average(this.gains, this.gains.length)] * this.gains.length / maxRayBounceCount, 0F, 1.0F);

                    float sendCutoff = (float) Math.pow(sendGain, 0.1F);

                    if(sendGain < 0.0) {
                        float norm = 1F / sendGain;
                        sendGain *= norm;
                    }

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
    }

    @Override
    protected void onRayBounceMiss(Ray ray, Vector3 endPosition) {
        super.onRayBounceMiss(ray, endPosition);
    }

    @Override
    protected void onRayBounceFinish(RayHitResult result, int unit, int missedSum, float overallRayLength) {
        super.onRayBounceFinish(result, unit, missedSum, overallRayLength);
        this.onRayBounceFinish0((BlockRayHitResult) result, unit, missedSum, overallRayLength);
    }

    //TODO: Remake gain
    protected void onRayBounceFinish0(BlockRayHitResult result, int unit, int missedSum, float overallRayLength) {
        float playerEnergy = MathHelper.clamp(
                overallRayLength
                        * (float) Math.pow(1F, overallRayLength)
                        / (float) Math.pow(overallRayLength, 2.0F * missedSum),
                0F, 1F);

        float bounceEnergy = MathHelper.clamp(
                overallRayLength
                        * (float) Math.pow(1F, overallRayLength)
                        / (float) Math.pow(overallRayLength, 2.0F * missedSum),
                Float.MIN_VALUE, 1F);

        float bounceTime = overallRayLength / this.config.world.speedOfSound;
        int resolution = this.config.audioResolution;

        this.gains[MathUtils.clamp(
                MathUtils.floor(MathUtils.logBase(Math.max((float) Math.pow(bounceEnergy, 4.142F / bounceTime), Float.MIN_VALUE), (float) Math.exp(-9.21F)) * resolution),
                0,
                resolution)] += playerEnergy;
    }
}
