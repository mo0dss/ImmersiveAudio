package moodss.ia.ray;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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
        }
    }

    public void forEachRay(Consumer<Ray> consumer) {
        synchronized (this.rays) {
            for(Ray ray : this.rays) {
                consumer.accept(ray);
            }
        }
    }

    public Ray[] getRays() {
        return this.rays;
    }

    public static record Ray(Vector3 start, Vector3 to, int color) {

    }
}
