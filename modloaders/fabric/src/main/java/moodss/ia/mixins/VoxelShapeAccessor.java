package moodss.ia.mixins;

import net.minecraft.util.shape.VoxelSet;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(VoxelShape.class)
public interface VoxelShapeAccessor {

    @Accessor("voxels")
    VoxelSet voxels();
}
