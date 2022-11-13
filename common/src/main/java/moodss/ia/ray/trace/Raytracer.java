package moodss.ia.ray.trace;

import moodss.ia.ray.Ray;
import moodss.ia.ray.RayHitResult;
import moodss.plummet.math.vec.Vector3;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface Raytracer {

    RayHitResult create(Ray ray, Vector3 end, @Nullable Vector3 ignore);

    default RayHitResult create(Ray ray, Vector3 end) {
        return create(ray, end, null);
    }
}
