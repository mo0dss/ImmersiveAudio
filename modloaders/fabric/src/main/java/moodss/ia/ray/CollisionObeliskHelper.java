package moodss.ia.ray;

import moodss.plummet.math.vec.Vector3;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

public class CollisionObeliskHelper {

    public static BlockCollisionObelisk raycast(Iterable<Box> boxes, Vector3 from, Vector3 to, BlockPos pos) {
        float[] traceDistanceResult = new float[]{1F};
        Vector3 direction = Vector3.ZERO;
        float x = to.getX() - from.getX();
        float y = to.getY() - from.getY();
        float z = to.getZ() - from.getZ();

        for(var it = boxes.iterator(); it.hasNext(); ) {
            var box = it.next();

            direction = traceCollisionSide(box.offset(pos), from, traceDistanceResult, direction, x, y, z);
        }

        if(!direction.equals(Vector3.ZERO)) {
            float distance = traceDistanceResult[0];

            return BlockCollisionObelisk.create(Vector3.add(from, distance * x, distance * y, distance * z), direction, pos);
        }

        return null;
    }

    protected static final float EXPONENT = 1.0E-7F;

    protected static Vector3 traceCollisionSide(Box box, Vector3 intersection, float[] traceDistanceResult, Vector3 direction, float x, float y, float z) {
        if(x > EXPONENT) {
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
        }
        else if(x < -EXPONENT) {
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
    private static Vector3 traceCollisionSide(float[] traceDistanceResult, Vector3 direction,
                                              float x, float y, float z,
                                              double begin, double minX, double maxX, double minZ, double maxZ,
                                              Vector3 resultDirection, float startX, float startY, float startZ) {
        float d = (float) ((begin - startX) / x);
        float e = startY + d * y;
        float f = startZ + d * z;
        if (0.0 < d && d < traceDistanceResult[0] && minX - EXPONENT < e && e < maxX + EXPONENT && minZ - EXPONENT < f && f < maxZ + EXPONENT) {
            traceDistanceResult[0] = d;
            return resultDirection;
        } else {
            return direction;
        }
    }
}
