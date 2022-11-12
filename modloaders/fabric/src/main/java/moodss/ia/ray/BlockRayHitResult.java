package moodss.ia.ray;

import net.minecraft.util.math.BlockPos;

public class BlockRayHitResult extends RayHitResult {

    /**
     * The {@link BlockPos} this ray will yield.
     */
    private final BlockPos pos;

    public static BlockRayHitResult create(Ray ray, BlockPos pos) {
        return new BlockRayHitResult(ray, pos, Type.BLOCK);
    }

    public BlockRayHitResult(Ray ray, BlockPos pos, Type type) {
        super(ray, type);
        this.pos = pos;
    }

    public BlockRayHitResult withRay(Ray ray) {
        return new BlockRayHitResult(ray, this.getPos(), this.type());
    }

    public BlockPos getPos() {
        return this.pos;
    }
}
