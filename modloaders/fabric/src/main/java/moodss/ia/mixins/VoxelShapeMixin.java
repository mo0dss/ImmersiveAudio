package moodss.ia.mixins;

import moodss.ia.mixin.VoxelShapeExt;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(VoxelShape.class)
public abstract class VoxelShapeMixin implements VoxelShapeExt {

    @Shadow
    protected abstract int getCoordIndex(Direction.Axis axis, double coord);

    @Override
    public int getCoordIdx(Direction.Axis axis, float coord) {
        return this.getCoordIndex(axis, coord);
    }
}
