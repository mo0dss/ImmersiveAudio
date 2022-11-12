package moodss.ia.ray;

import moodss.plummet.math.vec.Vector3;

import java.util.*;
import java.util.function.Consumer;

public class PathtracedDebug {

    private final Ray[] rays;
    private int nextRayIdx;

    public PathtracedDebug(int maxRayCount, int maxRayBounceCount) {
        this.rays = new Ray[maxRayCount * maxRayBounceCount];
    }

    public void addRay(Vector3 start, Vector3 end, int color) {
        this.rays[this.nextRayIdx++] = new Ray(start, end, color);
    }

    public void clear() {
        synchronized (this.rays) {
            Arrays.fill(this.rays, null);
            this.nextRayIdx = 0;
        }
    }

    public void forEachRay(Consumer<Ray> consumer) {
        synchronized (this.rays) {
            for(Ray ray : this.rays) {
                consumer.accept(ray);
            }
        }
    }

    public record Ray(Vector3 start, Vector3 to, int color)
    {}
}
