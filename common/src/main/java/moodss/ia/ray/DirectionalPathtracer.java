package moodss.ia.ray;

import moodss.plummet.math.MathUtils;
import moodss.plummet.math.vec.Vector3;
import org.apache.commons.lang3.Validate;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;

public class DirectionalPathtracer {

    /**
     * Maximum amount of rays allowed
     */
    protected final int maxRayCount;

    /**
     * Additional amount of rays allowed
     */
    protected final int additionalRays;

    /**
     * Strength manager for each ray
     */
    protected final StrengthManager strengthManager;

    protected final float MAX_RAY_COUNT_NORM;

    public DirectionalPathtracer(int maxRayCount, int additionalRays, StrengthManager strengthManager) {
        this.maxRayCount = maxRayCount;
        this.strengthManager = strengthManager;

        this.additionalRays = additionalRays;

        MAX_RAY_COUNT_NORM = 1.0F / this.maxRayCount;
    }

    public CompletableFuture<Vector3> computePathtrace(Vector3 origin, Vector3 listener,
                                                           BiFunction<Ray, Vector3, RayHitResult> traceFunc,
                                                           float maxDistance,
                                                           Executor executor) {
        this.strengthManager.clear();
        this.strengthManager.setMaxStrength(maxDistance);

        return CompletableFuture.supplyAsync(
                () -> {
                    float phi = MathUtils.PHI;

                    for(int additionalRayUnit = 0; additionalRayUnit < this.additionalRays; additionalRayUnit++) {
                        for(int rayUnit = 0; rayUnit < this.maxRayCount; rayUnit++) {
                            float rayAngle = rayUnit * this.MAX_RAY_COUNT_NORM;

                            float longitude = phi * rayUnit;
                            float latitude = (float) Math.asin(rayAngle * 2.0F - 1.0F);

                            Ray ray = new Ray(
                                    origin,
                                    new Vector3(
                                            (float)(Math.cos(latitude) * Math.cos(longitude)),
                                            (float)(Math.cos(latitude) * Math.sin(longitude)),
                                            (float)Math.sin(latitude)
                                    ),
                                    true
                            );

                            Vector3 endPosition = ray.pointAt(maxDistance);

                            this.onRay(ray, endPosition, listener, maxDistance, traceFunc);
                        }
                    }

                    return this.strengthManager.computePosition(origin, listener);
                },
                executor
        );
    }

    protected void onRay(Ray ray, Vector3 endPosition, Vector3 listener, float maxDistance, BiFunction<Ray, Vector3, RayHitResult> traceFunc) {
        //NO-OP
    }

    protected static Vector3 getClosestSharedAirspace(Ray ray, Vector3 listener, BiFunction<Ray, Vector3, RayHitResult> traceFunc) {
        RayHitResult result = traceFunc.apply(ray, listener);
        if(result.type() != RayHitResult.Type.MISS) {
            return ray.closestPoint(listener);
        }

        return Vector3.ZERO;
    }

    protected static Vector3 getFurthestSharedAirspace(Ray ray, Vector3 listener, BiFunction<Ray, Vector3, RayHitResult> traceFunc) {
        RayHitResult result = traceFunc.apply(ray, listener);
        if(result.type() != RayHitResult.Type.MISS) {
            return ray.furthestPoint(listener);
        }

        return Vector3.ZERO;
    }

    public static class StrengthManager {
        private final BiDirectionalEntry[] entries;
        private float maxStrength = 16.0F;
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
            if (!(strength <= 0.0F) && !(strength > this.maxStrength)) {
                this.entries[this.nextEntryIdx++] = new BiDirectionalEntry(direction, strength);
            }
        }

        public Vector3 computePosition(Vector3 origin, Vector3 listener) {
            if (isHomogenousArray(this.entries)) {
                return origin;
            }

            Vector3 output = Vector3.ZERO;
            if (this.nominalEntry != null) {
                Vector3 nominalDirection = this.nominalEntry.direction();
                if (!nominalDirection.equals(Vector3.ZERO)) {
                    output = Vector3.normalize(nominalDirection);
                }
            }

            //noinspection ForLoopReplaceableByForEach
            for(int idx = 0; idx < this.entries.length; ++idx) {
                BiDirectionalEntry entry = this.entries[idx];
                output = entry.modifyForStrength(output);
            }

            return Vector3.add(Vector3.modulate(Vector3.normalize(output), origin.distanceTo(listener)), listener);
        }

        private static boolean isHomogenousArray(BiDirectionalEntry[] arr) {
            BiDirectionalEntry val = arr[0];

            for(int i = 1; i < arr.length; ++i) {
                if (arr[i] != val) {
                    return false;
                }
            }

            return true;
        }

        public record BiDirectionalEntry(Vector3 direction, float strength) {

            public Vector3 modifyForStrength(Vector3 input) {
                float strength = this.strength;
                if (strength <= 0.0F) {
                    return input;
                }

                //TODO: Shouldnt this be 3?
                float dot = 1.0F / (strength * strength);
                return Vector3.add(input, Vector3.modulate(Vector3.normalize(this.direction), dot));
            }
        }
    }
}
