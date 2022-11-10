package moodss.ia.ray;

import moodss.plummet.math.vec.Vector3;
import net.minecraft.util.math.BlockPos;

public class BlockCollisionObelisk extends CollisionObelisk {
    private final BlockPos pos;

    public static BlockCollisionObelisk create(Vector3 origin, Vector3 direction, BlockPos pos) {
        return new BlockCollisionObelisk(origin, direction, pos, CollisionObelisk.Type.BLOCK);
    }

    public static BlockCollisionObelisk createMissed(Vector3 origin, Vector3 direction, BlockPos pos) {
        return new BlockCollisionObelisk(origin, direction, pos, CollisionObelisk.Type.MISS);
    }

    public BlockCollisionObelisk(Vector3 origin, Vector3 direction, BlockPos pos, CollisionObelisk.Type type) {
        super(origin, direction, type);
        this.pos = pos;
    }

    public BlockCollisionObelisk withSide(Vector3 side) {
        return new BlockCollisionObelisk(this.getOrigin(), side, this.pos, this.getType());
    }

    public BlockPos getPos() {
        return pos;
    }
}
