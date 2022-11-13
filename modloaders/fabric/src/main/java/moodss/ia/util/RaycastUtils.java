package moodss.ia.util;

import moodss.ia.mixin.VoxelShapeExt;
import moodss.ia.mixins.VoxelShapeAccessor;
import moodss.ia.ray.BlockRayHitResult;
import moodss.ia.ray.Ray;
import moodss.ia.ray.RayHitResultHelper;
import moodss.plummet.math.MathUtils;
import moodss.plummet.math.vec.Vector3;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

public class RaycastUtils {

    protected static final float EPSILON = 1.0E-7F;

    @Nullable
    public static BlockRayHitResult raycastBlock(World world, Ray start, Vector3 end, BlockPos pos, VoxelShape shape, BlockState state) {
        BlockRayHitResult result = raycastVoxelShape(shape, start, end, pos);
        if (result != null) {
            BlockRayHitResult boundBackResult = raycastVoxelShape(state.getRaycastShape(world, pos), start, end, pos);
            if (boundBackResult != null) {
                float boundBackSqr = boundBackResult.ray().distanceToSquared(start);
                float reflectiveSqr = result.ray().distanceToSquared(start);

                if (boundBackSqr < reflectiveSqr) {
                    return result.withDirection(Ray.getDirection(boundBackResult.ray()), true);
                }
            }
        }

        return result;
    }

    public static BlockRayHitResult raycastVoxelShape(VoxelShape shape, Ray start, Vector3 end, BlockPos pos) {
        if (shape.isEmpty()) {
            return null;
        }

        Vector3 mid = start.furthestPoint(end);
        if (mid.lengthSquared() > EPSILON) {

            Vector3 offset = Vector3.add(Ray.getOrigin(start), Vector3.modulate(mid, 0.001F));
            VoxelSet voxels = ((VoxelShapeAccessor)shape).voxels();

            return voxels
                    .inBoundsAndContains(
                            VoxelShapeExt.getCoordIdx(shape, Direction.Axis.X, offset.getX() - pos.getX()),
                            VoxelShapeExt.getCoordIdx(shape, Direction.Axis.Y, offset.getY() - pos.getY()),
                            VoxelShapeExt.getCoordIdx(shape, Direction.Axis.Z, offset.getZ() - pos.getZ())
                    ) ? BlockRayHitResult.create(new Ray(offset, DirectionUtil.get(DirectionUtil.getFacing(mid).getOpposite()), true), pos)
                    : RayHitResultHelper.raycast(shape.getBoundingBoxes(), start, end, pos);
        }

        return null;
    }

    public static <T> T raycast(Vector3 start, Vector3 end, Function<BlockPos, T> blockHitFactory, Supplier<T> missFactory) {
        if (start.equals(end)) {
            return missFactory.get();
        }

        float x1 = MathUtils.lerp(-EPSILON, end.getX(), start.getX());
        float y1 = MathUtils.lerp(-EPSILON, end.getY(), start.getY());
        float z1 = MathUtils.lerp(-EPSILON, end.getZ(), start.getZ());

        float x2 = MathUtils.lerp(-EPSILON, start.getX(), end.getX());
        float y2 = MathUtils.lerp(-EPSILON, start.getY(), end.getY());
        float z2 = MathUtils.lerp(-EPSILON, start.getZ(), end.getZ());

        int originX = MathHelper.floor(x2);
        int originY = MathHelper.floor(y2);
        int originZ = MathHelper.floor(z2);

        BlockPos.Mutable mutable = new BlockPos.Mutable(originX, originY, originZ);
        if (blockHitFactory.apply(mutable) == null) {
            return missFactory.get();
        }

        float x = x1 - x2;
        float y = y1 - y2;
        float z = z1 - z2;

        int xBit = MathHelper.sign(x);
        int yBit = MathHelper.sign(y);
        int zBit = MathHelper.sign(z);

        float xAngle = xBit == 0 ? Float.MAX_VALUE : xBit / x;
        float yAngle = yBit == 0 ? Float.MAX_VALUE : yBit / y;
        float zAngle = zBit == 0 ? Float.MAX_VALUE : zBit / z;

        float directionX = xAngle * (xBit > 0 ? 1.0F - MathHelper.fractionalPart(x2) : MathHelper.fractionalPart(x2));
        float directionY = yAngle * (yBit > 0 ? 1.0F - MathHelper.fractionalPart(y2) : MathHelper.fractionalPart(y2));
        float directionZ = zAngle * (zBit > 0 ? 1.0F - MathHelper.fractionalPart(z2) : MathHelper.fractionalPart(z2));

        while(directionX <= 1.0F || directionY <= 1.0F || directionZ <= 1.0F) {
            if (directionX < directionY) {
                if (directionX < directionZ) {
                    originX += xBit;
                    directionX += xAngle;
                } else {
                    originZ += zBit;
                    directionX += zAngle;
                }
            } else if (directionY < directionZ) {
                originY += yBit;
                directionY += yAngle;
            } else {
                originZ += zBit;
                directionZ += zAngle;
            }

            T result = blockHitFactory.apply(mutable.set(originX, originY, originZ));
            if (result != null) {
                return result;
            }
        }

        return missFactory.get();
    }
}
