package moodss.ia.interop.vanilla.ray;

import moodss.ia.ray.Ray;
import moodss.ia.ray.RayHitResult;
import moodss.plummet.math.vec.Vector3;
import net.minecraft.util.math.BlockPos;

public class BlockRayHitResult extends RayHitResult {

    /**
     * The {@link BlockPos} this ray will yield.
     */
    private final BlockPos pos;

    public static BlockRayHitResult create(Ray ray, BlockPos pos) {
        return new BlockRayHitResult(ray, pos, Type.BLOCK);
    }

    public static BlockRayHitResult createMissed(Ray ray, BlockPos pos) {
        return new BlockRayHitResult(ray, pos, Type.MISS);
    }

    public BlockRayHitResult(Ray ray, BlockPos pos, Type type) {
        super(ray, type);
        this.pos = pos;
    }

    public BlockRayHitResult withDirection(Vector3 direction, boolean isNormalised) {
        return new BlockRayHitResult(new Ray(Ray.getOrigin(this.ray()), direction, isNormalised), this.getPos(), this.type());
    }

    public BlockPos getPos() {
        return this.pos;
    }
}
