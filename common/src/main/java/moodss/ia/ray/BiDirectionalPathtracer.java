package moodss.ia.ray;

import moodss.ia.user.ImmersiveAudioConfig;
import moodss.plummet.math.MathUtils;
import moodss.plummet.math.vec.Vector3;
import org.apache.commons.lang3.Validate;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;

public class BiDirectionalPathtracer {

    private final int maxRayCount;
    private final int maxRayBounceCount;

    private final StrengthManager strengthManager;
    private final PathtracedDebug debug;

    private final float MAX_RAY_COUNT_NORM;

    public BiDirectionalPathtracer(ImmersiveAudioConfig.Raytracing raytracing) {
        this.maxRayCount = raytracing.maxRayCount;
        this.maxRayBounceCount = raytracing.maxRayBounceCount;

        this.strengthManager = new StrengthManager(this.maxRayCount * this.maxRayBounceCount);

        this.debug = new PathtracedDebug(this.maxRayCount, this.maxRayBounceCount);

        MAX_RAY_COUNT_NORM = 1F / this.maxRayCount;
    }

    public <T> CompletableFuture<Vector3> computeOrigin(Vector3 origin, Vector3 listener,
                                                     BiFunction<Vector3, Vector3, CollisionObelisk> traceFunc,
                                                     Function<T, Float> reflectivityFunc,
                                                     float[] bounceReflectivityRatio,
                                                     float[] gain,
                                                     float maxDistance,
                                                     Executor executor) {
        StrengthManager strengthManager = this.strengthManager;
        strengthManager.setMaxStrength(maxDistance);

        this.strengthManager.clear();
        this.debug.clear();

        return CompletableFuture.supplyAsync(() -> {
            Vector3 directSharedAirspaceVector = getFurthestSharedAirspace(new Ray(origin, Vector3.ZERO, true), listener, traceFunc);
            if (!directSharedAirspaceVector.equals(Vector3.ZERO)) {
                strengthManager.setNominalEntry(directSharedAirspaceVector);
            }

            //Begin raytracing
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
                    float rayLength = ray.distanceTo(Ray.getOrigin(record.getRay()));

                    this.debug.addRay(origin, Ray.getOrigin(record.getRay()), 0xFF55FF55);

                    Vector3 firstSharedAirspaceVector = getClosestSharedAirspace(record, listener, traceFunc);
                    if (!firstSharedAirspaceVector.equals(Vector3.ZERO)) {
                        strengthManager.addEntry(firstSharedAirspaceVector, rayLength);
                    }

                    Ray lastTracedRay = record.getRay();
                    Ray lastRay = ray;

                    for(int bounceUnit = 0; bounceUnit < this.maxRayBounceCount; bounceUnit++) {
                        Ray bounceRay = new Ray(
                                Ray.getOrigin(lastTracedRay),
                                Vector3.reflect(Ray.getDirection(lastRay), Ray.getDirection(lastTracedRay)),
                                true //Prevent any normalization
                        );

                        Vector3 bounceEndPosition = bounceRay.pointAt(maxDistance);
                        CollisionObelisk bounceRecord = traceFunc.apply(Ray.getOrigin(bounceRay), bounceEndPosition);

                        float blockReflectivity = reflectivityFunc.apply((T) record);
                        float energyTowardsPlayer = 0.25F * (blockReflectivity * 0.75F + 0.25F);


                        if (bounceRecord.getType() == CollisionObelisk.Type.MISS) {
                            rayLength += lastTracedRay.distanceTo(listener);

                            this.debug.addRay(Ray.getOrigin(lastTracedRay), bounceEndPosition, 0xFFFF5555);
                        } else {
                            this.debug.addRay(Ray.getOrigin(lastTracedRay), Ray.getOrigin(bounceRecord.getRay()), 0xFF0000FF);

                            rayLength += lastTracedRay.distanceTo(Ray.getOrigin(bounceRecord.getRay()));

                            bounceReflectivityRatio[bounceUnit] += blockReflectivity;

                            lastTracedRay = record.getRay();
                            lastRay = bounceRay;

                            Vector3 sharedAirspaceVector = getClosestSharedAirspace(bounceRecord, listener, traceFunc);
                            if (sharedAirspaceVector != null) {
                                strengthManager.addEntry(sharedAirspaceVector, rayLength);
                            }
                        }
                        float reflectionDelay = (float) Math.max(rayLength, 0D) * 0.12F * blockReflectivity;

                        for(int idx = 0; idx < gain.length; idx++) {
                            float cross = 1F - MathUtils.clamp(Math.abs(reflectionDelay - 0F), 0F, 1F);
                            gain[idx] = cross * energyTowardsPlayer * 6.4F * MAX_RAY_COUNT_NORM;
                        }

                        if (bounceRecord.getType() == CollisionObelisk.Type.MISS) {
                            break;
                        }
                    }
                }
            }
            //Finish raytracing

            return strengthManager.computePosition(origin, listener);
        }, executor);
    }

    public PathtracedDebug getDebug() {
        return debug;
    }

    public StrengthManager getStrengthManager() {
        return this.strengthManager;
    }

    public int getMaxRayCount() {
        return this.maxRayCount;
    }

    private static Vector3 getClosestSharedAirspace(CollisionObelisk record, Vector3 listener, BiFunction<Vector3, Vector3, CollisionObelisk> traceFunc) {
        return getClosestSharedAirspace(record.getRay(), listener, traceFunc);
    }

    private static Vector3 getFurthestSharedAirspace(Ray ray, Vector3 listener, BiFunction<Vector3, Vector3, CollisionObelisk> traceFunc) {
        CollisionObelisk record = traceFunc.apply(Ray.getOrigin(ray), listener);
        if (record.getType() != CollisionObelisk.Type.MISS) {
            return ray.furthestPoint(listener);
        }
        return Vector3.ZERO;
    }

    private static Vector3 getClosestSharedAirspace(Ray ray, Vector3 listener, BiFunction<Vector3, Vector3, CollisionObelisk> traceFunc) {
        CollisionObelisk record = traceFunc.apply(Ray.getOrigin(ray), listener);
        if (record.getType() != CollisionObelisk.Type.MISS) {
            return ray.closestPoint(listener);
        }
        return Vector3.ZERO;
    }

    public static class StrengthManager {
        private final BiDirectionalEntry[] entries;
        private float maxStrength = 16F;

        private int nextEntryIdx;

        private BiDirectionalEntry nominalEntry;

        public StrengthManager(int maxEntries) {
            Validate.isTrue(maxEntries != -1, "Max entry size must be one or higher.");
            this.entries = new BiDirectionalEntry[maxEntries];
        }

        public void clear() {
            Arrays.fill(this.entries, null);
            this.nextEntryIdx = 0;
        }

        public int getCurrentEntryIdx() {
            return this.nextEntryIdx;
        }

        public void setNominalEntry(Vector3 nominalDirection) {
            this.nominalEntry = new BiDirectionalEntry(nominalDirection, nominalDirection.length());
        }

        public void setMaxStrength(float maxStrength) {
            this.maxStrength = maxStrength;
        }

        public void addEntry(Vector3 direction, float distance) {
            float strength = distance + direction.length();
            if (strength <= 0F || strength > this.maxStrength) {
                return;
            }

            this.entries[this.nextEntryIdx++] = new BiDirectionalEntry(direction, strength);
        }

        public Vector3 computePosition(Vector3 origin, Vector3 listener) {
            if(isHomogenousArray(this.entries)) {
                return origin;
            }

            Vector3 output = Vector3.ZERO;

            if(nominalEntry != null) {
                Vector3 nominalDirection = nominalEntry.direction();
                if(!nominalDirection.equals(Vector3.ZERO)) {
                    output = Vector3.normalize(nominalDirection);
                }
            }

            for(int idx = 0; idx < this.entries.length; idx++) {
                BiDirectionalEntry entry = this.entries[idx];

                output = entry.modifyForStrength(output);
            }

            return Vector3.add(Vector3.modulate(Vector3.normalize(output), origin.distanceTo(listener)), listener);
        }

        public record BiDirectionalEntry(Vector3 direction, float strength) {

            public Vector3 modifyForStrength(Vector3 input) {
                float strength = this.strength;
                if(strength <= 0F) {
                    return input; //TODO check
                }

                //TODO: Shouldn't this by 3?
                float dot = 1F / (strength * strength);

                return Vector3.add(input, Vector3.modulate(Vector3.normalize(this.direction), dot));
            }
        }

        private static boolean isHomogenousArray(BiDirectionalEntry[] arr) {
            BiDirectionalEntry val = arr[0];

            for (int i = 1; i < arr.length; i++) {
                if (arr[i] != val) {
                    return false;
                }
            }

            return true;
        }
    }
}
