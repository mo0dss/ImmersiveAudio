package moodss.ia.ray.path;

import moodss.ia.ray.Ray;
import moodss.ia.ray.RayHitResult;
import moodss.ia.ray.trace.Raytracer;
import moodss.plummet.math.vec.Vector3;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class BiDirectionalPathtracer extends DirectionalPathtracer {
    private final int maxRayBounceCount;

    private final int additionalRayBounces;

    public BiDirectionalPathtracer(int maxRayCount, int maxRayBounceCount, int additionalRays, int additionalRayBounces) {
        super(maxRayCount, additionalRays, new StrengthManager((additionalRays * maxRayCount) * (additionalRayBounces * maxRayBounceCount)));
        this.maxRayBounceCount = maxRayBounceCount;

        this.additionalRayBounces = additionalRayBounces;
    }

    public CompletableFuture<Vector3> computePathtrace(Vector3 origin,
                                                       Vector3 listener,
                                                       Raytracer tracer,
                                                       float maxDistance,
                                                       Executor executor) {
        Vector3 directSharedAirspaceVector = getFurthestSharedAirspace(new Ray(origin), listener, tracer);
        if (!directSharedAirspaceVector.equals(Vector3.ZERO)) {
            this.strengthManager.setNominalEntry(directSharedAirspaceVector);
        }

        return super.computePathtrace(origin, listener, tracer, maxDistance, executor);
    }

    protected void onRay(Ray ray, Vector3 endPosition, Vector3 listener, float maxDistance, Raytracer tracer) {
        RayHitResult result = tracer.create(ray, endPosition);
        if (result.type() == RayHitResult.Type.BLOCK) {
            float rayLength = ray.distanceTo(result.ray());

            this.onRayBounceStart(result, ray);

            Ray prevTracedRay = result.ray();
            Ray prevRay = ray;

            Vector3 firstSharedAirspaceVector = getClosestSharedAirspace(prevTracedRay, listener, tracer);
            if (!firstSharedAirspaceVector.equals(Vector3.ZERO)) {
                this.strengthManager.addEntry(firstSharedAirspaceVector, rayLength);
            }

            int missedSum = 0;
            for(int additionalRayBounce = 0; additionalRayBounce < this.additionalRayBounces; additionalRayBounce++) {

                for(int bounceUnit = 0; bounceUnit < this.maxRayBounceCount; bounceUnit++) {
                    Ray bounceRay = prevRay.reflect(prevTracedRay);

                    Vector3 bounceEndPosition = bounceRay.pointAt(maxDistance);
                    RayHitResult bounceResult = tracer.create(bounceRay, bounceEndPosition, Ray.getOrigin(result.ray()));
                    if (bounceResult.type() == RayHitResult.Type.MISS) {
                        rayLength += prevTracedRay.distanceTo(listener);
                        missedSum++;

                        this.onRayBounceMiss(prevTracedRay, bounceEndPosition);
                    } else {
                        rayLength += prevTracedRay.distanceTo(bounceResult.ray());

                        prevTracedRay = bounceResult.ray();
                        prevRay = bounceRay;

                        this.onRayBounceHit(bounceResult, prevTracedRay, Ray.getOrigin(bounceRay), bounceUnit);

                        Vector3 sharedAirspaceVector = getClosestSharedAirspace(prevTracedRay, listener, tracer);
                        if (sharedAirspaceVector != null) {
                            this.strengthManager.addEntry(sharedAirspaceVector, rayLength);
                        }
                    }

                    this.onRayBounceFinish(result, bounceUnit, missedSum, rayLength);
                    if (bounceResult.type() == RayHitResult.Type.MISS) {
                        break;
                    }
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

    protected void onRayBounceFinish(RayHitResult result, int unit, int missedSum, float overallRayLength) {
        //NO-OP
    }
}
