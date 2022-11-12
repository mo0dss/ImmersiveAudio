package moodss.ia.ray;

import moodss.plummet.math.vec.Vector3;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;

public class BiDirectionalPathtracer extends DirectionalPathtracer {
    private final int maxRayBounceCount;

    public BiDirectionalPathtracer(int maxRayCount, int maxRayBounceCount) {
        super(maxRayCount, new StrengthManager(maxRayBounceCount * maxRayBounceCount));
        this.maxRayBounceCount = maxRayBounceCount;
    }

    public CompletableFuture<Vector3> computePathtrace(
            Vector3 origin, Vector3 listener, BiFunction<Ray, Vector3, RayHitResult> traceFunc, float maxDistance, Executor executor
    ) {
        Vector3 directSharedAirspaceVector = getFurthestSharedAirspace(new Ray(origin), listener, traceFunc);
        if (!directSharedAirspaceVector.equals(Vector3.ZERO)) {
            this.strengthManager.setNominalEntry(directSharedAirspaceVector);
        }

        return super.computePathtrace(origin, listener, traceFunc, maxDistance, executor);
    }

    protected void onRay(Ray ray, Vector3 endPosition, Vector3 listener, float maxDistance, BiFunction<Ray, Vector3, RayHitResult> traceFunc) {
        RayHitResult result = traceFunc.apply(ray, endPosition);
        if (result.type() == RayHitResult.Type.BLOCK) {
            float rayLength = ray.distanceTo(result.ray());

            this.onRayBounceStart(result, ray);

            Ray prevTracedRay = result.ray();
            Ray prevRay = ray;

            Vector3 firstSharedAirspaceVector = getClosestSharedAirspace(prevTracedRay, listener, traceFunc);
            if (!firstSharedAirspaceVector.equals(Vector3.ZERO)) {
                this.strengthManager.addEntry(firstSharedAirspaceVector, rayLength);
            }

            for(int bounceUnit = 0; bounceUnit < this.maxRayBounceCount; bounceUnit++) {
                Ray bounceRay = prevRay.reflect(prevTracedRay);

                Vector3 bounceEndPosition = bounceRay.pointAt(maxDistance);
                RayHitResult bounceResult = traceFunc.apply(bounceRay, bounceEndPosition);
                if (bounceResult.type() == RayHitResult.Type.MISS) {
                    rayLength += prevTracedRay.distanceTo(listener);

                    this.onRayBounceMiss(prevTracedRay, bounceEndPosition);
                } else {
                    rayLength += prevTracedRay.distanceTo(bounceResult.ray());

                    prevTracedRay = bounceResult.ray();
                    prevRay = bounceRay;

                    this.onRayBounceHit(bounceResult, prevTracedRay, Ray.getOrigin(bounceRay), bounceUnit);

                    Vector3 sharedAirspaceVector = getClosestSharedAirspace(prevTracedRay, listener, traceFunc);
                    if (sharedAirspaceVector != null) {
                        this.strengthManager.addEntry(sharedAirspaceVector, rayLength);
                    }
                }

                this.onRayBounceFinish(result, bounceUnit, rayLength);
                if (bounceResult.type() == RayHitResult.Type.MISS) {
                    break;
                }
            }
        }
    }

    protected void onRayBounceStart(RayHitResult result, Ray ray) {
        //NO-OP
    }

    protected void onRayBounceMiss(Ray ray, Vector3 endPosition) {
        //NO-OP
    }

    protected void onRayBounceHit(RayHitResult result, Ray ray, Vector3 endPosition, int unit) {
        //NO-OP
    }

    protected void onRayBounceFinish(RayHitResult result, int unit, float overallRayLength) {
        //NO-OP
    }
}
