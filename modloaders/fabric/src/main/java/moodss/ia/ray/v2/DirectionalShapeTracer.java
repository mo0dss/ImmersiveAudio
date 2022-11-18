package moodss.ia.ray.v2;

import moodss.plummet.math.MathUtils;
import moodss.plummet.math.vec.Vector3;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class DirectionalShapeTracer {

    protected static final float EXPONENT = 1.0E-7F;

    public static CompletableFuture<Vector3> computeSideCollision(Vector3 origin, Vector3 emitter, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {

            return null;
        }, executor);
    }

    protected static Vector3 computeSideCollision(Vector3 origin, Vector3 direction, float startX, float startY, float startZ,
                                                  Vector3 emitter, float endY, float endZ, Vector3 result, float overallDistance) {
        float dot = (startX - emitter.getX()) / origin.getX();

        float y = MathUtils.fma(dot, origin.getY(), emitter.getY());
        float z = MathUtils.fma(dot, origin.getZ(), emitter.getZ());

        if(dot < overallDistance) {
            if(startY - EXPONENT < y && y < endY + EXPONENT) {
                if(startZ - EXPONENT < z && z < endZ + EXPONENT) {
                    overallDistance = dot;
                    return result;
                }
            }
        }
        return direction;
    }
}
