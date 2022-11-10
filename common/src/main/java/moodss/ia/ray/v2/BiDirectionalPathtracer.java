package moodss.ia.ray.v2;

import moodss.ia.ray.Ray;
import moodss.ia.user.ImmersiveAudioConfig;
import moodss.plummet.math.MathUtils;
import moodss.plummet.math.vec.Vector3;
import org.apache.commons.lang3.Validate;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class BiDirectionalPathtracer {

    private final int maxRayCount;
    private final int maxRayBounceCount;

    private final StrengthManager strengthManager;

    private final float MAX_RAY_COUNT_NORM;

    public BiDirectionalPathtracer(ImmersiveAudioConfig.Raytracing raytracing) {
        this.maxRayCount = raytracing.maxRayCount;
        this.maxRayBounceCount = raytracing.maxRayBounceCount;

        this.strengthManager = new StrengthManager(maxRayCount * maxRayBounceCount);

        MAX_RAY_COUNT_NORM = 1F / this.maxRayCount;
    }

    public <T> CompletableFuture<Vector3> computePathtrace(Vector3 origin,
                                                     Vector3 listener,
                                                     BiFunction<Ray, Vector3, Boolean> onRayConsumer,

                                                     float maxDistance,
                                                     Executor executor) {
        return CompletableFuture.supplyAsync(() -> {

            float phi = MathUtils.PHI;
            for(int rayUnit = 0; rayUnit < this.maxRayCount; rayUnit++) {
                float rayAngle = rayUnit * MAX_RAY_COUNT_NORM;

                float longitude = phi * rayUnit;
                float latitude = (float) Math.asin(rayAngle * 2F - 1F);

                Ray ray = new Ray(origin,
                        new Vector3(
                                (float) (Math.cos(latitude) * Math.cos(longitude)),
                                (float) (Math.cos(latitude) * Math.sin(longitude)),
                                (float) Math.sin(latitude)),
                        true);

                Vector3 endPosition = ray.pointAt(maxDistance);

                //If true rays can bounce
                if(onRayConsumer.apply(ray, endPosition)) {

                    for(int rayBounceUnit = 0; rayBounceUnit < this.maxRayBounceCount; rayBounceUnit++) {

                    }
                }
            }

            return this.strengthManager.computePosition(origin, listener);
        }, executor);
    }

    private static class StrengthManager {
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
