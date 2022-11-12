package moodss.ia.ray;

import moodss.plummet.math.vec.Vector3;

import java.util.*;
import java.util.function.Consumer;

public class PathtracedDebug {

    private Ray[] rays;

    private int nextRayIdx;

    public PathtracedDebug(int maxRayCount, int maxRayBounceCount, int additionalRayCount, int additionalRayBounceCount, boolean enabled) {
        if(enabled) {
            this.rays = new Ray[(additionalRayCount * maxRayCount) * (additionalRayBounceCount * maxRayBounceCount)];
        }
    }

    public void addRay(Vector3 start, Vector3 end, int color) {
        if(this.rays != null) {
            this.rays[this.nextRayIdx++] = new Ray(start, end, color);
        }
    }

    public void clear() {
        if(this.rays == null) {
            return;
        }

        synchronized (this.rays) {
            Arrays.fill(this.rays, null);
            this.nextRayIdx = 0;
        }
    }

    public void forEachRay(Consumer<Ray> consumer) {
        if(this.rays == null) {
            return;
        }

        synchronized (this.rays) {
            for(Ray ray : this.rays) {
                consumer.accept(ray);
            }
        }
    }

    public record Ray(Vector3 start, Vector3 to, int color)
    {}
}
