package moodss.ia.ray;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import moodss.plummet.math.vec.Vector3;

import java.util.*;
import java.util.function.Consumer;

public class PathtracedDebug {

    private final List<Ray> rays;

    public PathtracedDebug(int maxRayCount) {
        this.rays = new ObjectArrayList<>();
    }

    public void addRay(Vector3 start, Vector3 end, int color) {
        this.rays.add(new Ray(start, end, color));
    }

    public void clear() {
        synchronized (this.rays) {
            this.rays.clear();
        }
    }

    public void forEachRay(Consumer<Ray> consumer) {
        synchronized (this.rays) {
            this.rays.forEach(consumer);
        }
    }

    public List<Ray> getRaySet() {
        return this.rays;
    }

    public static record Ray(Vector3 start, Vector3 to, int color) {

    }
}
