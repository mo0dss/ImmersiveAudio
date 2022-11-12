package moodss.ia.ray;

import moodss.plummet.math.vec.Vector3;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

public class RayHitResultHelper {
    protected static final float EXPONENT = 1.0E-7F;

    public RayHitResultHelper() {
    }

    public static BlockRayHitResult raycast(Iterable<Box> boxes, Ray ray, Vector3 to, BlockPos pos) {
        float[] traceDistanceResult = new float[]{1.0F};

        Vector3 direction = Vector3.ZERO;
        Vector3 from = Ray.getOrigin(ray);

        float x = to.getX() - from.getX();
        float y = to.getY() - from.getY();
        float z = to.getZ() - from.getZ();

        for(Box box : boxes) {
            direction = traceCollisionSide(box.offset(pos), from, traceDistanceResult, direction, x, y, z);
        }

        if (!direction.equals(Vector3.ZERO)) {
            float distance = traceDistanceResult[0];
            return BlockRayHitResult.create(new Ray(Vector3.add(from, distance * x, distance * y, distance * z), direction, true), pos);
        }

        return null;
    }

    protected static Vector3 traceCollisionSide(Box box, Vector3 intersection, float[] traceDistanceResult, Vector3 direction, float x, float y, float z) {
        if (x > EXPONENT) {
            direction = traceCollisionSide(
                    traceDistanceResult,
                    direction,
                    x,
                    y,
                    z,
                    box.minX,
                    box.minY,
                    box.maxY,
                    box.minZ,
                    box.maxZ,
                    Vector3.NEGATIVE_X,
                    intersection.getX(),
                    intersection.getY(),
                    intersection.getZ()
            );
        } else if (x < -EXPONENT) {
            direction = traceCollisionSide(
                    traceDistanceResult,
                    direction,
                    x,
                    y,
                    z,
                    box.maxX,
                    box.minY,
                    box.maxY,
                    box.minZ,
                    box.maxZ,
                    Vector3.POSITIVE_X,
                    intersection.getX(),
                    intersection.getY(),
                    intersection.getZ()
            );
        }

        if (y > EXPONENT) {
            direction = traceCollisionSide(
                    traceDistanceResult,
                    direction,
                    y,
                    z,
                    x,
                    box.minY,
                    box.minZ,
                    box.maxZ,
                    box.minX,
                    box.maxX,
                    Vector3.NEGATIVE_Y,
                    intersection.getY(),
                    intersection.getZ(),
                    intersection.getX()
            );
        } else if (y < -EXPONENT) {
            direction = traceCollisionSide(
                    traceDistanceResult,
                    direction,
                    y,
                    z,
                    x,
                    box.maxY,
                    box.minZ,
                    box.maxZ,
                    box.minX,
                    box.maxX,
                    Vector3.POSITIVE_Y,
                    intersection.getY(),
                    intersection.getZ(),
                    intersection.getX()
            );
        }

        if (z > EXPONENT) {
            direction = traceCollisionSide(
                    traceDistanceResult,
                    direction,
                    z,
                    x,
                    y,
                    box.minZ,
                    box.minX,
                    box.maxX,
                    box.minY,
                    box.maxY,
                    Vector3.NEGATIVE_Z,
                    intersection.getZ(),
                    intersection.getX(),
                    intersection.getY()
            );
        } else if (z < -EXPONENT) {
            direction = traceCollisionSide(
                    traceDistanceResult,
                    direction,
                    z,
                    x,
                    y,
                    box.maxZ,
                    box.minX,
                    box.maxX,
                    box.minY,
                    box.maxY,
                    Vector3.POSITIVE_Z,
                    intersection.getZ(),
                    intersection.getX(),
                    intersection.getY()
            );
        }

        return direction;
    }

    @Nullable
    private static Vector3 traceCollisionSide(
            float[] traceDistanceResult,
            Vector3 direction,
            float x,
            float y,
            float z,
            double begin,
            double minX,
            double maxX,
            double minZ,
            double maxZ,
            Vector3 resultDirection,
            float startX,
            float startY,
            float startZ
    ) {
        float d = (float)((begin - (double)startX) / (double)x);
        float e = startY + d * y;
        float f = startZ + d * z;
        if (0.0 < (double)d
                && d < traceDistanceResult[0]
                && minX - EXPONENT < (double)e
                && (double)e < maxX + EXPONENT
                && minZ - EXPONENT < (double)f
                && (double)f < maxZ + EXPONENT) {
            traceDistanceResult[0] = d;
            return resultDirection;
        }

        return direction;
    }
}
