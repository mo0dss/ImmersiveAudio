package moodss.ia.ray;

import moodss.ia.ray.v2.BiDirectionalPathStrengthManager;
import moodss.ia.user.ImmersiveAudioConfig;
import moodss.plummet.math.MathUtils;
import moodss.plummet.math.vec.Vector3;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;

public class RaytracedAudio {

    private final int maxRayCount;
    private final int maxRayBounceCount;

    private final BiDirectionalPathStrengthManager pathtracer;
    private final PathtracedDebug debug;

    public RaytracedAudio(ImmersiveAudioConfig.Raytracing raytracing) {
        this.maxRayCount = raytracing.maxRayCount;
        this.maxRayBounceCount = raytracing.maxRayBounceCount;

        this.pathtracer = new BiDirectionalPathStrengthManager(maxRayCount * maxRayBounceCount);

        this.debug = new PathtracedDebug(maxRayCount);
    }

    public <T> CompletableFuture<Vector3> computeOrigin(Vector3 origin, Vector3 listener,
                                                     BiFunction<Vector3, Vector3, CollisionObelisk> traceFunc,
                                                     Function<T, Float> reflectivityFunc,
                                                     float[] bounceReflectivityRatio,
                                                     float[] gain,
                                                     float maxDistance,
                                                     Executor executor) {
        BiDirectionalPathStrengthManager pathtracer = this.pathtracer;
        pathtracer.setMaxStrength(maxDistance);

        this.pathtracer.clear();
        this.debug.clear();

        return CompletableFuture.supplyAsync(() -> {
            Vector3 directSharedAirspaceVector = getSharedAirspace(origin, listener, traceFunc);
            if (!directSharedAirspaceVector.equals(Vector3.ZERO)) {
                pathtracer.setNominalEntry(directSharedAirspaceVector);
            }

            //Begin raytracing
            float rayNorm = 1F / this.maxRayCount;
            float phi = MathUtils.PHI;
            for(int unit = 0; unit < this.maxRayCount; unit++) {
                float rayUnit = (float) unit / this.maxRayCount;

                float longitude = phi * (float) unit * 1F;
                float latitude = (float) Math.asin(rayUnit * 2F - 1F);

                Ray ray = new Ray(origin,
                        new Vector3(
                                (float) (Math.cos(latitude) * Math.cos(longitude)),
                                (float) (Math.cos(latitude) * Math.sin(longitude)),
                                (float) Math.sin(latitude)),
                        true);

                Vector3 endPosition = ray.pointAt(maxDistance);
                CollisionObelisk record = traceFunc.apply(origin, endPosition);

                if(record.getType() == CollisionObelisk.Type.BLOCK) {
                    float rayLength = origin.distanceTo(record.getOrigin());

                    this.debug.addRay(origin, record.getOrigin(), 0xFF55FF55);

                    Vector3 firstSharedAirspaceVector = getSharedAirspace(record, listener, traceFunc);
                    if (!firstSharedAirspaceVector.equals(Vector3.ZERO)) {
                        pathtracer.addEntry(firstSharedAirspaceVector, rayLength);
                    }

                    Vector3 lastOrigin = record.getOrigin();
                    Vector3 lastDirection = record.getDirection();
                    Ray lastRay = ray;

                    for(int bounceUnit = 0; bounceUnit < this.maxRayBounceCount; bounceUnit++) {
                        Ray bounceRay = new Ray(
                                lastOrigin,
                                Vector3.reflect(Ray.getDirection(lastRay), lastDirection),
                                true //Prevent any normalization
                        );

                        Vector3 bounceEndPosition = bounceRay.pointAt(maxDistance);
                        CollisionObelisk bounceRecord = traceFunc.apply(Ray.getOrigin(bounceRay), bounceEndPosition);

                        float blockReflectivity = reflectivityFunc.apply((T) record);
                        float energyTowardsPlayer = 0.25F * (blockReflectivity * 0.75F + 0.25F);

                        if (bounceRecord.getType() == CollisionObelisk.Type.MISS) {
                            rayLength += lastOrigin.distanceTo(listener);

                            this.debug.addRay(lastOrigin, bounceEndPosition, 0xFFFF5555);
                        } else {
                            this.debug.addRay(lastOrigin, bounceRecord.getOrigin(), 0xFFFF5555);

                            rayLength += lastOrigin.distanceTo(bounceRecord.getOrigin());

                            bounceReflectivityRatio[bounceUnit] += blockReflectivity;

                            lastOrigin = bounceRecord.getOrigin();
                            lastDirection = bounceRecord.getDirection();
                            lastRay = bounceRay;

                            Vector3 sharedAirspaceVector = getSharedAirspace(bounceRecord, listener, traceFunc);
                            if (sharedAirspaceVector != null) {
                                pathtracer.addEntry(sharedAirspaceVector, rayLength);
                            }
                        }
                        float reflectionDelay = (float) Math.max(rayLength, 0D) * 0.12F * blockReflectivity;

                        for(int idx = 0; idx < gain.length; idx++) {
                            float cross = 1F - MathUtils.clamp(Math.abs(reflectionDelay - 0F), 0F, 1F);
                            gain[idx] = cross * energyTowardsPlayer * 6.4F * rayNorm;
                        }

                        if (bounceRecord.getType() == CollisionObelisk.Type.MISS) {
                            break;
                        }
                    }
                }
            }
            //Finish raytracing

            return pathtracer.computePosition(origin, listener);
        }, executor);
    }

    public PathtracedDebug getDebug() {
        return debug;
    }

    public BiDirectionalPathStrengthManager getPathtracer() {
        return this.pathtracer;
    }

    public int getMaxRayCount() {
        return this.maxRayCount;
    }

    private static Vector3 getSharedAirspace(CollisionObelisk record, Vector3 listener, BiFunction<Vector3, Vector3, CollisionObelisk> traceFunc) {
        Vector3 origin = record.getOrigin();
        Vector3 direction = record.getDirection();

        return getSharedAirspace(Vector3.modulate(Vector3.add(direction, 0.001F), origin), listener, traceFunc);
    }

    private static Vector3 getSharedAirspace(Vector3 origin, Vector3 listener, BiFunction<Vector3, Vector3, CollisionObelisk> traceFunc) {
        CollisionObelisk record = traceFunc.apply(origin, listener);
        if (record.getType() != CollisionObelisk.Type.MISS) {
            return Vector3.subtract(origin, listener);
        }
        return Vector3.ZERO;
    }
}
