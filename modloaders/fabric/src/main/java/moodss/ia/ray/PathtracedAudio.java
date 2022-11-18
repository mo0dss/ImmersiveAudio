package moodss.ia.ray;

import moodss.ia.ImmersiveAudioMod;
import moodss.ia.client.ImmersiveAudioClientMod;
import moodss.ia.openal.EAXReverbController;
import moodss.ia.ray.path.DebugBiDirectionalPathtracer;
import moodss.ia.ray.trace.Raytracer;
import moodss.ia.sfx.api.effects.filter.Filter;
import moodss.ia.user.ImmersiveAudioConfig;
import moodss.plummet.math.MathUtils;
import moodss.plummet.math.vec.Vector3;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.Validate;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class PathtracedAudio extends DebugBiDirectionalPathtracer {

    protected float[] reflectivity;
    protected float[] gains;
    protected float[] directGain;

    private final ImmersiveAudioConfig config;

    public PathtracedAudio(ImmersiveAudioConfig config) {
        super(config.raytracing);

        this.reflectivity = new float[config.audioResolution + 1];
        this.gains = new float[config.audioResolution + 1];
        this.directGain = new float[config.audioResolution + 1];
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
        float[] directGains = this.directGain;

        int maxAuxiliary = ImmersiveAudioMod.instance().auxiliaryEffectManager().getMaxAuxiliaries();
        int maxRayBounceCount = this.config.raytracing.maxRayBounceCount;
        EAXReverbController reverbController = ImmersiveAudioMod.instance().eaxReverbController();

        return super.pathtrace(origin, listener, tracer, maxDistance, executor).thenApplyAsync(position -> {
            ImmersiveAudioClientMod.DEVICE.run(context -> {
                for(int idx = 0; idx < maxAuxiliary; idx++) {
                    float sendGain = MathHelper.clamp(gains[MathUtils.average(gains, gains.length)] * gains.length / maxRayBounceCount, 0F, 1.0F);
                    float directGain = MathHelper.clamp(directGains[MathUtils.average(directGains, directGains.length)] * directGains.length / maxRayBounceCount, 0F, 1.0F);

                    float directCutoff = (float) Math.pow(directGain, 1F / 0.75F);
                    float sendCutoff = (float) Math.pow(sendGain, 1F / 0.75F);

                    if(sendGain < 0.0) {
                        float norm = 1F / sendGain;
                        sendGain *= norm;
                    }

                    if(directGain < 0.0) {
                        float norm = 1F / directGain;
                        directGain *= norm;
                    }

                    Filter directFilter = reverbController.getDirectFilter();
                    context.setGain(directFilter, directGain);
                    context.setGainHF(directFilter, directCutoff);

                    Filter filter = reverbController.getFilter(idx);
                    context.setGain(filter, sendGain);
                    context.setGainHF(filter, sendCutoff);
                }
            });

            return position;
        }, executor);
    }

    @Override
    protected void onRayBounceStart(RayHitResult result, Ray ray) {
        super.onRayBounceStart(result, ray);
        this.onRayBounceStart0((BlockRayHitResult) result, ray);
    }

    protected void onRayBounceStart0(BlockRayHitResult result, Ray ray) {
        ClientWorld world = MinecraftClient.getInstance().world;

        if(world != null) {
            if(result.type() == RayHitResult.Type.MISS) {
                this.strengthManager.decrementExclusion(
                        getBlockExclusion(result.getPos())
                );
            } else {
                this.strengthManager.incrementOcclusion(
                        getBlockOcclusion(result.getPos())
                );
            }
        }
    }

    @Override
    protected void onRayBounceFinish(RayHitResult result, int unit, int missedSum, int bouncedSum, float rayDistance, float listenerDistance) {
        super.onRayBounceFinish(result, unit, missedSum, bouncedSum, rayDistance, listenerDistance);
        this.onRayBounceFinish0((BlockRayHitResult) result, unit, missedSum, bouncedSum, rayDistance, listenerDistance);
    }

    protected void onRayBounceFinish0(BlockRayHitResult result, int unit, int missedSum, int bouncedSum, float rayDistance, float listenerDistance) {
        var blockReflectivity = getBlockReflectivity(result.getPos());

        var overallRayDistance = (rayDistance * blockReflectivity);
        var overallDistance = overallRayDistance + listenerDistance;

        this.modifyDirectGain(listenerDistance, missedSum, bouncedSum);
        this.modifyGain(overallRayDistance, overallDistance, missedSum, bouncedSum);
    }

    protected void modifyDirectGain(float listenerDistance, int missedSum, int bouncedSum) {
        var playerGainEnergy = MathHelper.clamp(
                listenerDistance
                        * (float) Math.pow(1F, listenerDistance)
                        / (float) Math.pow(listenerDistance, 2.0F * missedSum),
                0F, 1F);

        var directGainEnergy = MathHelper.clamp(
                listenerDistance
                        * (float) Math.pow(1F, listenerDistance)
                        / (float) Math.pow(listenerDistance, 2.0F * missedSum),
                Float.MIN_VALUE, 1F);

        float time = listenerDistance / this.config.world.speedOfSound;
        int resolution = this.config.audioResolution;

        this.directGain[MathUtils.clamp(
                MathUtils.floor(MathUtils.logBase(Math.max((float) Math.pow(directGainEnergy, 4.142F / time), Float.MIN_VALUE), (float) Math.exp(-9.21F)) * resolution),
                0,
                resolution)] += playerGainEnergy;
    }

    protected void modifyGain(float overallRayDistance, float overallDistance, int missedSum, int bouncedSum) {
        var playerGainEnergy = MathHelper.clamp(
                overallRayDistance
                        * (float) Math.pow(1F, overallDistance)
                        / (float) Math.pow(overallDistance, 2.0F * missedSum),
                bouncedSum, 1F);

        var bounceGainEnergy = MathHelper.clamp(
                overallRayDistance
                        * (float) Math.pow(1F, overallDistance)
                        / (float) Math.pow(overallDistance, 2.0F * missedSum),
                Float.MIN_VALUE, 1F);

        float bounceTime = overallRayDistance / this.config.world.speedOfSound;
        int resolution = this.config.audioResolution;

        this.gains[MathUtils.clamp(
                MathUtils.floor(MathUtils.logBase(Math.max((float) Math.pow(bounceGainEnergy, 4.142F / bounceTime), Float.MIN_VALUE), (float) Math.exp(-9.21F)) * resolution),
                0,
                resolution)] += playerGainEnergy;
    }

    protected float getBlockReflectivity(BlockPos pos) {
        ImmersiveAudioConfig config = ImmersiveAudioMod.instance().config();
        ClientWorld world = MinecraftClient.getInstance().world;
        Validate.isTrue(world != null, "World is null");
        BlockState state = world.getBlockState(pos);

        var blockReflectivity = config.reflectivity.blockReflectivity.getFloat(
                Registry.BLOCK.getKey(state.getBlock())
        );

        if(blockReflectivity == -1) {
            blockReflectivity = config.reflectivity.soundTypeReflectivity.getFloat(
                    state.getSoundGroup()
            );
        }

        return blockReflectivity;
    }

    protected float getBlockOcclusion(BlockPos pos) {
        ImmersiveAudioConfig config = ImmersiveAudioMod.instance().config();
        ClientWorld world = MinecraftClient.getInstance().world;
        Validate.isTrue(world != null, "World is null");
        BlockState state = world.getBlockState(pos);

        var blockOcclusion = config.occlusion.blockOcclusion.getFloat(
                Registry.BLOCK.getKey(state.getBlock())
        );

        if(blockOcclusion == -1) {
            blockOcclusion = config.occlusion.soundTypeOcclusion.getFloat(
                    state.getSoundGroup()
            );
        }

        return blockOcclusion;
    }

    protected float getBlockExclusion(BlockPos pos) {
        ImmersiveAudioConfig config = ImmersiveAudioMod.instance().config();
        ClientWorld world = MinecraftClient.getInstance().world;
        Validate.isTrue(world != null, "World is null");
        BlockState state = world.getBlockState(pos);

        var blockExclusion = config.exclusion.blockExclusion.getFloat(
                Registry.BLOCK.getKey(state.getBlock())
        );

        if(blockExclusion == -1) {
            blockExclusion = config.exclusion.soundTypeExclusion.getFloat(
                    state.getSoundGroup()
            );
        }

        return blockExclusion;
    }
}
