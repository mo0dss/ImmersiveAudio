package moodss.ia.ray;

import moodss.ia.ImmersiveAudioMod;
import moodss.ia.client.ImmersiveAudioClientMod;
import moodss.ia.client.camera.StoredCameraData;
import moodss.ia.fluid.FluidView;
import moodss.ia.openal.EAXReverbController;
import moodss.ia.ray.path.DebugBiDirectionalPathtracer;
import moodss.ia.ray.trace.Raytracer;
import moodss.ia.sfx.api.effects.filter.Filter;
import moodss.ia.user.ImmersiveAudioConfig;
import moodss.ia.util.SupportedSoundTypeUtil;
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
    protected float directGain;

    private final ImmersiveAudioConfig config;

    public PathtracedAudio(ImmersiveAudioConfig config) {
        super(config.raytracing);

        this.reflectivity = new float[config.audioResolution + 1];
        this.gains = new float[config.audioResolution + 1];
        this.config = config;
    }

    protected void clear() {
        Arrays.fill(this.gains, 0F);
        this.directGain = 1F;
    }

    public CompletableFuture<Vector3> computePathtrace(Vector3 origin,
                                                       StoredCameraData<FluidView> cameraData,
                                                       Raytracer tracer,
                                                       float maxDistance, Executor executor) {
        this.clear();
        float[] gains = this.gains;

        int maxAuxiliary = ImmersiveAudioMod.instance().auxiliaryEffectManager().getMaxAuxiliaries();
        int maxRayBounceCount = this.maxRayBounceCount;
        int maxRayCount = this.maxRayCount;
        EAXReverbController reverbController = ImmersiveAudioMod.instance().eaxReverbController();

        return super.computePathtrace(origin, cameraData.position(), tracer, maxDistance, executor).thenApplyAsync(position -> {
            ImmersiveAudioClientMod.DEVICE.run(context -> {
                for(int idx = 0; idx < maxAuxiliary; idx++) {
                    float sendGain = MathHelper.clamp(gains[MathUtils.average(gains, gains.length)] * gains.length / maxRayBounceCount, 0F, 1.0F);
                    float directGain = this.directGain;

                    if(sendGain < 0.0) {
                        float norm = 1F / sendGain;
                        sendGain *= norm;
                    }

                    if(directGain < 0.0) {
                        float norm = 1F / directGain;
                        directGain *= norm;
                    }

                    float directCutoff = MathUtils.pow(directGain, 1F / 0.75F);
                    float sendCutoff = MathUtils.pow(sendGain, 1F / 0.75F);

                    if(cameraData.submersion().fluidType().isWater()) {
                        sendCutoff *= 0.4F;
                        directCutoff *= 0.4F;
                    }

                    Filter directFilter = reverbController.getDirectFilter();
                    context.setGain(directFilter, directGain);
                    context.setGainHF(directFilter, directCutoff);

                    Filter filter = reverbController.getFilter(idx);
                    context.setGain(filter, sendGain);
                    context.setGainHF(filter, sendCutoff);

                    //Panning
                    float midPointX = origin.getX() - cameraData.position().getX();
                    float midPointY = origin.getY() - cameraData.position().getY();
                    float midPointZ = origin.getZ() - cameraData.position().getZ();

                    float angle = (float) Math.acos(cameraData.orientation().getZ());

                    //If the Listener is facing to the left of straight ahead then the angle should be negated
                    if(cameraData.orientation().getX() < 0) {
                        angle = -angle;
                    }

                    float angleCos = cos(-angle);
                    float angleSin = sin(-angle);

                    float panX = MathUtils.fma(midPointX, angleCos, midPointZ * angleSin);
                    float panY = 0F;
                    float panZ = MathUtils.fma(midPointX, -angleSin, midPointZ * angleCos);
                }
            });

            return position;
        }, executor);
    }

    @Override
    protected float sin(float value) {
        return MathHelper.sin(value);
    }

    @Override
    protected float cos(float value) {
        return MathHelper.cos(value);
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
    protected void onRayBounceFinish(RayHitResult result, int unit, int missedSum, int bouncedSum, float rayDistance, float overallRayDistance, float listenerDistance) {
        super.onRayBounceFinish(result, unit, missedSum, bouncedSum, rayDistance, overallRayDistance, listenerDistance);

        this.onRayBounceFinish0((BlockRayHitResult) result, unit, missedSum, bouncedSum, rayDistance, overallRayDistance, listenerDistance);
    }

    //TODO: move
    protected float totalReflectivity;

    protected void onRayBounceFinish0(BlockRayHitResult result, int unit, int missedSum, int bouncedSum, float rayDistance, float overallRayDistance, float listenerDistance) {
        var blockReflectivity = getBlockReflectivity(result.getPos());
        var totalReflectivity = this.totalReflectivity += blockReflectivity;

        this.modifyDirectGain(listenerDistance, missedSum, bouncedSum);

        this.modifyGain(rayDistance, overallRayDistance, listenerDistance, blockReflectivity, totalReflectivity, missedSum, bouncedSum);
    }

    protected void modifyDirectGain(float listenerDistance, int missedSum, int bouncedSum) {
        float directGain = MathUtils.pow(1F, listenerDistance);

        directGain *= MathUtils.pow(1F, listenerDistance)
                / MathUtils.pow(listenerDistance, 2.0 * missedSum)
                * MathHelper.lerp(this.strengthManager.getOcclusion(), bouncedSum, 1d);

        this.directGain = directGain;
    }

    protected void modifyGain(float rayDistance, float overallRayDistance, float listenerDistance, float reflectivity, float overallReflectivity, int missedSum, int bouncedSum) {
        var playerGainEnergy = MathHelper.clamp(
                overallReflectivity * reflectivity
                        * MathUtils.pow(1F, overallRayDistance + rayDistance)
                        / MathUtils.pow(overallRayDistance + rayDistance, 2.0F * missedSum),
                0F, 1F);

        var bounceGainEnergy = MathHelper.clamp(
                overallReflectivity
                        * MathUtils.pow(1F, overallRayDistance)
                        / MathUtils.pow(overallRayDistance, 2.0F * missedSum),
                Float.MIN_VALUE, 1F);

        float bounceTime = overallRayDistance / this.config.world.speedOfSound;
        int resolution = this.config.audioResolution;

        this.gains[MathUtils.clamp(
                MathUtils.floor(MathUtils.logBase(Math.max(MathUtils.pow(bounceGainEnergy, 4.142F / bounceTime), Float.MIN_VALUE), MathUtils.exp(-9.21F)) * resolution),
                0, resolution)] += playerGainEnergy;
    }

    protected float getBlockReflectivity(BlockPos pos) {
        ImmersiveAudioConfig config = ImmersiveAudioMod.instance().config();
        ClientWorld world = MinecraftClient.getInstance().world;
        Validate.isTrue(world != null, "World is null");
        BlockState state = world.getBlockState(pos);

        var blockReflectivity = config.reflectivity.blockReflectivity.getOrDefault(
                Registry.BLOCK.getKey(state.getBlock()),
                -1F
        );

        if(blockReflectivity == -1) {
            blockReflectivity = config.reflectivity.soundTypeReflectivity.getOrDefault(
                    SupportedSoundTypeUtil.from(state.getSoundGroup()),
                    config.reflectivity.defaultReflectivity
            );
        }

        return blockReflectivity;
    }

    protected float getBlockOcclusion(BlockPos pos) {
        ImmersiveAudioConfig config = ImmersiveAudioMod.instance().config();
        ClientWorld world = MinecraftClient.getInstance().world;
        Validate.isTrue(world != null, "World is null");
        BlockState state = world.getBlockState(pos);

        var blockOcclusion = config.occlusion.blockOcclusion.getOrDefault(
                Registry.BLOCK.getKey(state.getBlock()),
                -1F
        );

        if(blockOcclusion == -1) {
            blockOcclusion = config.occlusion.soundTypeOcclusion.getOrDefault(
                    SupportedSoundTypeUtil.from(state.getSoundGroup()),
                    config.occlusion.defaultOcclusion
            );
        }

        return blockOcclusion;
    }

    protected float getBlockExclusion(BlockPos pos) {
        ImmersiveAudioConfig config = ImmersiveAudioMod.instance().config();
        ClientWorld world = MinecraftClient.getInstance().world;
        Validate.isTrue(world != null, "World is null");
        BlockState state = world.getBlockState(pos);

        var blockExclusion = config.exclusion.blockExclusion.getOrDefault(
                Registry.BLOCK.getKey(state.getBlock()),
                -1F
        );

        if(blockExclusion == -1) {
            blockExclusion = config.exclusion.soundTypeExclusion.getOrDefault(
                    SupportedSoundTypeUtil.from(state.getSoundGroup()),
                    config.exclusion.defaultExclusion
            );
        }

        return blockExclusion;
    }
}
