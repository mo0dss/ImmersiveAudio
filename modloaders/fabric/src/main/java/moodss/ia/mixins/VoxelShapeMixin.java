package moodss.ia.mixins;

import moodss.ia.mixin.VoxelShapeExt;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(VoxelShape.class)
public abstract class VoxelShapeMixin implements VoxelShapeExt {
    @Shadow
    @Final
    protected VoxelSet voxels;

    @Shadow
    protected abstract double getPointPosition(Direction.Axis axis, int index);

    @Override
    public int getCoordIdx(Direction.Axis axis, float coord) {
        return MathHelper.binarySearch(0, this.voxels.getSize(axis) + 1, i -> coord < this.getPointPosition(axis, i)) - 1;
    }
}
