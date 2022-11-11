package moodss.ia.ray;

import moodss.plummet.math.vec.Vector3;
import net.minecraft.util.math.BlockPos;

public class BlockCollisionObelisk extends CollisionObelisk {
    private final BlockPos pos;

    public static BlockCollisionObelisk create(Ray ray, BlockPos pos) {
        return new BlockCollisionObelisk(ray, pos, CollisionObelisk.Type.BLOCK);
    }

    public static BlockCollisionObelisk createMissed(Ray ray, BlockPos pos) {
        return new BlockCollisionObelisk(ray, pos, CollisionObelisk.Type.MISS);
    }

    public BlockCollisionObelisk(Ray ray, BlockPos pos, CollisionObelisk.Type type) {
        super(ray, type);
        this.pos = pos;
    }

    public BlockCollisionObelisk withRay(Ray ray) {
        return new BlockCollisionObelisk(ray, this.pos, this.getType());
    }

    public BlockPos getPos() {
        return pos;
    }
}
