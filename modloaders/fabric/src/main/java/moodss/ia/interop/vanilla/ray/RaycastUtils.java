package moodss.ia.interop.vanilla.ray;

import moodss.ia.mixin.VoxelShapeExt;
import moodss.ia.mixins.core.VoxelShapeAccessor;
import moodss.ia.ray.Ray;
import moodss.ia.util.DirectionUtil;
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

public class RaycastUtils {

    protected static final float EPSILON = 1.0E-7F;

    @Nullable
    public static BlockRayHitResult raycastBlock(World world, Ray start, Vector3 end, BlockPos pos, VoxelShape shape, BlockState state) {
        var result = raycastVoxelShape(shape, start, end, pos);
        if (result != null) {
            var boundBackResult = raycastVoxelShape(state.getRaycastShape(world, pos), start, end, pos);
            if (boundBackResult != null) {
                var boundBackSqr = boundBackResult.ray().distanceToSquared(start);
                var reflectiveSqr = result.ray().distanceToSquared(start);

                if (boundBackSqr < reflectiveSqr) {
                    return result.withDirection(Ray.getDirection(boundBackResult.ray()), true);
                }
            }
        }

        return result;
    }

    public static BlockRayHitResult raycastVoxelShape(VoxelShape shape, Ray start, Vector3 end, BlockPos pos) {
        if (!shape.isEmpty()) {
            var mid = start.furthestPoint(end);
            if (mid.lengthSquared() > EPSILON) {
                var offset = Vector3.add(Ray.getOrigin(start), Vector3.modulate(mid, 0.001F));
                VoxelSet voxels = ((VoxelShapeAccessor)shape).voxels();

                float coordX = offset.getX() - pos.getX();
                float coordY = offset.getY() - pos.getY();
                float coordZ = offset.getZ() - pos.getZ();

                var coordXIdx = VoxelShapeExt.getCoordIdx(shape, Direction.Axis.X, coordX);
                var coordYIdx = VoxelShapeExt.getCoordIdx(shape, Direction.Axis.Y, coordY);
                var coordZIdx = VoxelShapeExt.getCoordIdx(shape, Direction.Axis.Z, coordZ);

                if(voxels.inBoundsAndContains(coordXIdx, coordYIdx, coordZIdx)) {
                    return BlockRayHitResult.create(new Ray(offset, DirectionUtil.get(DirectionUtil.getFacing(mid).getOpposite()), true), pos);
                }

                return RayHitResultHelper.raycastShape(shape, start, end, pos);
            }
        }

        return null;
    }

    public static <T> T raycast(Vector3 start, Vector3 end, Function<BlockPos, T> blockHitFactory, Function<BlockPos, T> missFactory) {
        var x1 = MathUtils.lerp(-EPSILON, end.getX(), start.getX());
        var y1 = MathUtils.lerp(-EPSILON, end.getY(), start.getY());
        var z1 = MathUtils.lerp(-EPSILON, end.getZ(), start.getZ());

        var x2 = MathUtils.lerp(-EPSILON, start.getX(), end.getX());
        var y2 = MathUtils.lerp(-EPSILON, start.getY(), end.getY());
        var z2 = MathUtils.lerp(-EPSILON, start.getZ(), end.getZ());

        var originX = MathHelper.floor(x2);
        var originY = MathHelper.floor(y2);
        var originZ = MathHelper.floor(z2);

        if (!start.equals(end)) {
            BlockPos.Mutable mutable = new BlockPos.Mutable(originX, originY, originZ);
            if (blockHitFactory.apply(mutable) == null) {
                return missFactory.apply(mutable);
            }

            float x = x1 - x2;
            float y = y1 - y2;
            float z = z1 - z2;

            var xBit = MathHelper.sign(x);
            var yBit = MathHelper.sign(y);
            var zBit = MathHelper.sign(z);

            var xAngle = xBit == 0 ? Float.MAX_VALUE : xBit / x;
            var yAngle = yBit == 0 ? Float.MAX_VALUE : yBit / y;
            var zAngle = zBit == 0 ? Float.MAX_VALUE : zBit / z;

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

        }

        return missFactory.apply(new BlockPos(originX, originY, originZ));
    }
}
