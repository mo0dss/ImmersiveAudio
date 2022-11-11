package moodss.ia.ray.v2;

import moodss.ia.ray.Ray;
import moodss.plummet.math.vec.Vector3;

public record RayHitResult(Ray ray, Vector3 endPosition, Type type) {

    public enum Type {
        MISS,

        BLOCK,

        ENTITY
    }
}
