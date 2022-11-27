package moodss.ia.interop.vanilla.ray;

import moodss.ia.ray.Ray;
import moodss.plummet.math.MathUtils;
import moodss.plummet.math.vec.Vector3;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class RayHitResultHelper {
    protected static final float EXPONENT = 1.0E-7F;

    public RayHitResultHelper() {
    }

    public static BlockRayHitResult raycastShape(VoxelShape shape, Ray ray, Vector3 end, BlockPos offset) {
        var result = new float[] { 1F };

        var direction = Vector3.ZERO;
        var origin = Ray.getOrigin(ray);

        float emitterX = end.getX() - origin.getX();
        float emitterY = end.getY() - origin.getY();
        float emitterZ = end.getZ() - origin.getZ();

        for(var it = shape.getBoundingBoxes().iterator(); it.hasNext(); ) {
            var box = it.next();

            direction = traceCollisionSide(box.offset(offset), origin, result, direction, emitterX, emitterY, emitterZ);
        }

        if (!direction.equals(Vector3.ZERO)) {
            float distance = result[0];
            return BlockRayHitResult.create(
                    new Ray(
                            Vector3.add(origin, distance * emitterX, distance * emitterY, distance * emitterZ),
                            direction,
                            true),
                    offset);
        }

        return null;
    }

    public static BlockRayHitResult raycast(Iterable<Box> boxes, Ray ray, Vector3 to, BlockPos offset) {
        float[] result = new float[]{1.0F};

        Vector3 direction = Vector3.ZERO;
        Vector3 origin = Ray.getOrigin(ray);

        float emitterX = to.getX() - origin.getX();
        float emitterY = to.getY() - origin.getY();
        float emitterZ = to.getZ() - origin.getZ();

        for(Box box : boxes) {
            direction = traceCollisionSide(box.offset(offset), origin, result, direction, emitterX, emitterY, emitterZ);
        }

        if (!direction.equals(Vector3.ZERO)) {
            float distance = result[0];
            return BlockRayHitResult.create(
                    new Ray(
                            Vector3.add(origin, distance * emitterX, distance * emitterY, distance * emitterZ),
                            direction,
                            true),
                    offset);
        }

        return null;
    }

    protected static Vector3 traceCollisionSide(Box box, Vector3 intersection, float[] traceDistanceResult, Vector3 direction, float x, float y, float z) {
        if (x > EXPONENT) {
            direction = traceCollisionSide(traceDistanceResult, direction,
                    x, y, z,
                    box.minX, box.minY,
                    box.maxY,
                    box.minZ, box.maxZ,
                    Vector3.NEGATIVE_X,
                    intersection.getX(), intersection.getY(), intersection.getZ()
            );
        } else if (x < -EXPONENT) {
            direction = traceCollisionSide(traceDistanceResult, direction,
                    x, y, z,
                    box.maxX, box.minY,
                    box.maxY,
                    box.minZ, box.maxZ,
                    Vector3.POSITIVE_X,
                    intersection.getX(), intersection.getY(), intersection.getZ()
            );
        }

        if (y > EXPONENT) {
            direction = traceCollisionSide(traceDistanceResult, direction,
                    y, z, x,
                    box.minY, box.minZ,
                    box.maxZ,
                    box.minX, box.maxX,
                    Vector3.NEGATIVE_Y,
                    intersection.getY(), intersection.getZ(), intersection.getX()
            );
        } else if (y < -EXPONENT) {
            direction = traceCollisionSide(traceDistanceResult, direction,
                    y, z, x,
                    box.maxY, box.minZ,
                    box.maxZ,
                    box.minX, box.maxX,
                    Vector3.POSITIVE_Y,
                    intersection.getY(), intersection.getZ(), intersection.getX()
            );
        }

        if (z > EXPONENT) {
            direction = traceCollisionSide(traceDistanceResult, direction,
                    z, x, y,
                    box.minZ, box.minX,
                    box.maxX,
                    box.minY, box.maxY,
                    Vector3.NEGATIVE_Z,
                    intersection.getZ(), intersection.getX(), intersection.getY()
            );
        } else if (z < -EXPONENT) {
            direction = traceCollisionSide(traceDistanceResult, direction,
                    z, x, y,
                    box.maxZ, box.minX,
                    box.maxX,
                    box.minY, box.maxY,
                    Vector3.POSITIVE_Z,
                    intersection.getZ(), intersection.getX(), intersection.getY()
            );
        }

        return direction;
    }

    @Nullable
    private static Vector3 traceCollisionSide(float[] traceDistanceResult,
                                              Vector3 direction,
                                              float emitterX, float emitterY, float emitterZ,
                                              double start,
                                              double minX, double maxX,
                                              double minZ, double maxZ,
                                              Vector3 resultDirection,
                                              float originX, float originY, float originZ) {
        float dot = (float) (start - originX) / emitterX;
        float y = MathUtils.fma(dot, emitterY, originY);
        float z = MathUtils.fma(dot, emitterZ, originZ);

        if (dot < traceDistanceResult[0]) {
            if(minX - EXPONENT < y && y < maxX + EXPONENT) {
                if(minZ - EXPONENT < z && z < maxZ + EXPONENT) {
                    traceDistanceResult[0] = dot;
                    return resultDirection;
                }
            }
        }

        return direction;
    }

    protected static Vector3 computeSideCollision(Vector3 origin, Vector3 direction, float startX, float startY, float startZ,
                                                  Vector3 emitter, float endY, float endZ, Vector3 result, float[] overallDistance) {
        float dot = (startX - emitter.getX()) / origin.getX();

        float y = MathUtils.fma(dot, origin.getY(), emitter.getY());
        float z = MathUtils.fma(dot, origin.getZ(), emitter.getZ());

        if(dot < overallDistance[0]) {
            if(startY - EXPONENT < y && y < endY + EXPONENT) {
                if(startZ - EXPONENT < z && z < endZ + EXPONENT) {
                    overallDistance[0] = dot;
                    return result;
                }
            }
        }
        return direction;
    }
}
