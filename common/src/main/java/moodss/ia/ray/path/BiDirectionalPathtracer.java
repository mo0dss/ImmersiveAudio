package moodss.ia.ray.path;

import moodss.ia.ray.Ray;
import moodss.ia.ray.RayHitResult;
import moodss.ia.ray.trace.Raytracer;
import moodss.plummet.math.MathUtils;
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

    @Override
    protected Ray createRay(Vector3 origin, int rayUnit) {
        float rayAngle = rayUnit * this.MAX_RAY_COUNT_NORM;

        float longitude = MathUtils.PHI * rayUnit;
        float latitude = (float) Math.asin(rayAngle * 2.0F - 1.0F);

        return new Ray(
                origin,
                new Vector3(
                        (float)(Math.cos(latitude) * Math.cos(longitude)),
                        (float)(Math.cos(latitude) * Math.sin(longitude)),
                        (float)Math.sin(latitude)
                ),
                true
        );
    }

    protected void onRay(Ray ray, Vector3 endPosition, Vector3 listener, float maxDistance, Raytracer tracer) {
        RayHitResult result = tracer.create(ray, endPosition);

        this.onRayBounceStart(result, ray);
        if (result.type() == RayHitResult.Type.BLOCK) {
            float rayDistance = ray.distanceTo(result.ray());
            float listenerDistance = ray.distanceTo(listener);

            Ray prevTracedRay = result.ray();
            Ray prevRay = ray;

            Vector3 firstSharedAirspaceVector = getClosestSharedAirspace(prevTracedRay, listener, tracer);
            if (!firstSharedAirspaceVector.equals(Vector3.ZERO)) {
                this.strengthManager.addEntry(firstSharedAirspaceVector, rayDistance);
            }

            int missedSum = 0;
            int bouncedSum = 0;
            for(int additionalRayBounce = 0; additionalRayBounce < this.additionalRayBounces; additionalRayBounce++) {

                for(int bounceUnit = 0; bounceUnit < this.maxRayBounceCount; bounceUnit++) {
                    Ray bounceRay = prevRay.reflect(prevTracedRay);

                    Vector3 bounceEndPosition = bounceRay.pointAt(maxDistance);
                    RayHitResult bounceResult = tracer.create(bounceRay, bounceEndPosition, Ray.getOrigin(result.ray()));
                    if (bounceResult.type() == RayHitResult.Type.MISS) {
                        rayDistance += prevTracedRay.distanceTo(listener);
                        missedSum++;

                        this.onRayBounceMiss(bounceResult, prevTracedRay, bounceEndPosition);
                    } else {
                        rayDistance += prevTracedRay.distanceTo(bounceResult.ray());
                        bouncedSum++;

                        prevTracedRay = bounceResult.ray();
                        prevRay = bounceRay;

                        this.onRayBounceHit(bounceResult, prevTracedRay, Ray.getOrigin(bounceRay), bounceUnit);

                        Vector3 sharedAirspaceVector = getClosestSharedAirspace(prevTracedRay, listener, tracer);
                        if (sharedAirspaceVector != null) {
                            this.strengthManager.addEntry(sharedAirspaceVector, rayDistance);
                        }
                    }

                    this.onRayBounceFinish(result, bounceUnit, missedSum, bouncedSum, rayDistance, listenerDistance);
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

    protected void onRayBounceEnd(RayHitResult result, Ray ray) {
        //NO-OP
    }

    protected void onRayBounceMiss(RayHitResult result, Ray ray, Vector3 endPosition) {
        //NO-OP
    }

    protected void onRayBounceHit(RayHitResult result, Ray ray, Vector3 endPosition, int unit) {
        //NO-OP
    }

    protected void onRayBounceFinish(RayHitResult result, int unit, int missedSum, int bouncedSum, float rayDistance, float listenerDistance) {
        //NO-OP
    }
}
