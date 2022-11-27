package moodss.ia.client;

import moodss.ia.interop.vanilla.ray.BlockRayHitResult;
import moodss.ia.interop.vanilla.ray.VanillaPathtracer;
import moodss.ia.openal.EAXReverbController;
import moodss.ia.ray.RayHitResult;
import moodss.ia.sfx.api.effects.filter.Filter;
import moodss.ia.user.ImmersiveAudioConfig;
import moodss.ia.util.CameraUtil;
import moodss.plummet.math.MathUtils;
import moodss.plummet.math.vec.Vector3;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;

public class ReverbPathtracer extends VanillaPathtracer {

    private final EAXReverbController controller;
    private final float[] sendGains;
    private final int rayStretchedResolutionSample;

    private int bouncedSum, missedSum;

    public ReverbPathtracer(ImmersiveAudioConfig config, EAXReverbController controller) {
        super(config);
        this.controller = controller;

        int resolution = config.audioResolution + 1;

        this.sendGains = new float[resolution];
        this.rayStretchedResolutionSample = resolution / config.raytracing.maxRayBounceCount;
    }

    @Override
    protected void preTrace() {
        super.preTrace();
        Arrays.fill(this.sendGains, 1F);

        this.bouncedSum = 0;
        this.missedSum = 0;
    }

    @Override
    protected Vector3 postTrace(Vector3 origin, Vector3 position) {
        var cameraData = CameraUtil.getCameraData();
        var controller = this.controller;
        var submerged = cameraData.submersion().fluidType().isWater();

        var sendGains = this.sendGains;
        var sendGainCount = this.sendGains.length;

        var rayStretchedResolutionSample = this.rayStretchedResolutionSample;
        var bouncedSum = this.bouncedSum;
        var missedSum = this.missedSum;

        ImmersiveAudioClientMod.DEVICE.run(context -> {
            var listenerDistance = cameraData.position().distanceTo(origin);

            var directGain = MathHelper.clamp(attemptClamp(
                    MathUtils.pow(3F, listenerDistance)
                            / (MathUtils.pow(listenerDistance, 2.0F * missedSum)
                            * MathHelper.lerp(this.strengthManager.getOcclusion(), bouncedSum, 1F))
            ), 0F, 1F);

            var directCutoff = MathUtils.pow(directGain, 1F / Float.MIN_NORMAL);

            if(submerged) {
                directCutoff *= 0.4F;
            }

            Filter directFilter = controller.getDirectFilter();
            context.setGain(directFilter, directGain);
            context.setGainHF(directFilter, directCutoff);

            for(int idx = 0; idx < controller.getEffectCount(); idx++) {
                var sendGain = MathHelper.clamp(attemptClamp(
                        sendGains[MathUtils.average(sendGains, idx, sendGainCount)] * (bouncedSum * rayStretchedResolutionSample)
                ), 0F, 1F);

                var sendCutoff = MathUtils.pow(sendGain, 1F / Float.MIN_NORMAL);

                if(submerged) {
                    sendCutoff *= 0.4F;
                }

                Filter filter = controller.getFilter(idx);
                context.setGain(filter, sendGain);
                context.setGainHF(filter, sendCutoff);
            }
        });

        return super.postTrace(origin, position);
    }

    protected float attemptClamp(float value) {
        if(value < 0.0) {
            float norm = 1F / value;
            value *= norm;
        }

        return value;
    }

    @Override
    protected void onRayBounceFinish(RayHitResult result,
                                     int unit, int missedSum, int bouncedSum,
                                     float rayDistance, float overallRayDistance, float listenerDistance) {
        super.onRayBounceFinish(result, unit, missedSum, bouncedSum, rayDistance, overallRayDistance, listenerDistance);

        //Record bounced and missed sum for post tracing
        this.bouncedSum = bouncedSum;
        this.missedSum = missedSum;

        this.modifySendGain((BlockRayHitResult) result, missedSum, overallRayDistance, rayDistance);
    }

    protected void modifySendGain(BlockRayHitResult result, int missedSum, float overallRayDistance, float rayDistance) {
        var reflectivity = getBlockReflectivity(result.getPos());

        var playerGainEnergy = MathHelper.clamp(
                this.totalReflectivity * reflectivity
                        * MathUtils.pow(1F, overallRayDistance + rayDistance)
                        / MathUtils.pow(overallRayDistance + rayDistance, 2.0F * missedSum),
                0F, 1F);

        var bounceGainEnergy = MathHelper.clamp(
                this.totalReflectivity
                        * MathUtils.pow(1F, overallRayDistance)
                        / MathUtils.pow(overallRayDistance, 2.0F * missedSum),
                Float.MIN_VALUE, 1F);

        float bounceTime = overallRayDistance / this.config.world.speedOfSound;
        var resolution = this.config.audioResolution;

        this.sendGains[MathUtils.clamp(
                MathUtils.floor(MathUtils.logBase(Math.max(MathUtils.pow(bounceGainEnergy, 4.142F / bounceTime), Float.MIN_VALUE), MathUtils.exp(-9.21F)) * resolution),
                0, resolution)] += playerGainEnergy;
    }
}
