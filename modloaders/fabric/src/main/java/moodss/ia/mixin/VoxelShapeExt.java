package moodss.ia.mixin;

import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;

public interface VoxelShapeExt {

    static int getCoordIdx(VoxelShape shape, Direction.Axis axis, float coord) {
        return ((VoxelShapeExt) shape).getCoordIdx(axis, coord);
    }

    int getCoordIdx(Direction.Axis axis, float coord);
}
