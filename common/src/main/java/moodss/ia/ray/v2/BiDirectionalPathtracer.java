package moodss.ia.ray.v2;

import moodss.ia.ray.Ray;
import moodss.plummet.math.vec.Vector3;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class BiDirectionalPathtracer extends DirectionalPathtracer {

    public BiDirectionalPathtracer(int maxRayCount, DirectionalPathtracer.StrengthManager strengthManager) {
        super(maxRayCount, strengthManager);
    }


    public <T> CompletableFuture<Vector3> computePathtrace(Vector3 origin,
                                                           Vector3 listener,
                                                           BiFunction<Ray, Vector3, RayHitResult> traceFunc,
                                                           float maxDistance,
                                                           Executor executor) {
        CompletableFuture<Vector3> directionalPathtrace = super.computePathtrace(origin, listener, new BiConsumer<Ray, Vector3>() {
            @Override
            public void accept(Ray ray, Vector3 endPosition) {
                RayHitResult result = traceFunc.apply(ray, endPosition);
                if(result.type() == RayHitResult.Type.BLOCK) {
                    float rayLength = ray.distanceToSquared(Ray.getOrigin(result.ray()));

                    //this.debug.addRay(origin, record.getOrigin(), 0xFF55FF55);
                }
            }
        }, maxDistance, executor);

        return directionalPathtrace;
    }
    private static Vector3 getSharedAirspace(Ray ray, Vector3 listener, BiFunction<Vector3, Vector3, RayHitResult> traceFunc) {
        RayHitResult result = traceFunc.apply(Ray.getOrigin(ray), listener);
        if (result.type() != RayHitResult.Type.MISS) {
            return ray.closestPoint(listener);
        }
        return Vector3.ZERO;
    }

}
