package moodss.ia.interop.vanilla.ray;

import moodss.ia.ImmersiveAudioMod;
import moodss.ia.client.RayDebug;
import moodss.ia.ray.Ray;
import moodss.ia.ray.RayHitResult;
import moodss.ia.ray.path.BiDirectionalPathtracer;
import moodss.ia.ray.trace.Raytracer;
import moodss.ia.user.ImmersiveAudioConfig;
import moodss.ia.util.SupportedSoundTypeUtil;
import moodss.plummet.math.vec.Vector3;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.Validate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class VanillaPathtracer extends BiDirectionalPathtracer {

    protected float totalReflectivity;

    protected final ImmersiveAudioConfig config;

    public VanillaPathtracer(ImmersiveAudioConfig config) {
        super(config.raytracing.maxRayCount, config.raytracing.maxRayBounceCount, config.raytracing.additionalRayCount, config.raytracing.additionalRayBounceCount);

        this.config = config;
    }

    protected void preTrace() {
        this.totalReflectivity = 0F;
    }

    protected Vector3 postTrace(Vector3 origin, Vector3 position) {
        return position;
    }

    @Override
    public CompletableFuture<Vector3> computePathtrace(Vector3 origin, Vector3 listener, Raytracer traceFunc, float maxDistance, Executor executor) {
        this.preTrace();
        return super.computePathtrace(origin, listener, traceFunc, maxDistance, executor).thenApplyAsync((pos) -> this.postTrace(origin, pos));
    }

    @Override
    protected void onRayBounceStart(RayHitResult result, Ray ray) {
        super.onRayBounceStart(result, ray);
        this.onRayBounceStart0((BlockRayHitResult) result, ray);

        RayDebug.recordRay(Ray.getOrigin(ray), Ray.getOrigin(result.ray()), 0xFF55FF55);
    }

    @Override
    protected void onRayBounceHit(RayHitResult result, Ray ray, Vector3 endPosition, int unit) {
        super.onRayBounceHit(result, ray, endPosition, unit);

        RayDebug.recordRay(Ray.getOrigin(result.ray()), endPosition, 0xFF0000FF);
    }

    @Override
    protected void onRayBounceMiss(RayHitResult result, Ray ray, Vector3 endPosition) {
        super.onRayBounceMiss(result, ray, endPosition);

        RayDebug.recordRay(Ray.getOrigin(ray), endPosition, 0xFFFF5555);
    }

    @Override
    protected void onRayBounceFinish(RayHitResult result,
                                     int unit, int missedSum, int bouncedSum,
                                     float rayDistance, float overallRayDistance, float listenerDistance) {
        super.onRayBounceFinish(result, unit, missedSum, bouncedSum, rayDistance, overallRayDistance, listenerDistance);

        this.totalReflectivity += getBlockReflectivity(((BlockRayHitResult) result).getPos());
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
    protected float sin(float value) {
        return MathHelper.sin(value);
    }

    @Override
    protected float cos(float value) {
        return MathHelper.cos(value);
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
