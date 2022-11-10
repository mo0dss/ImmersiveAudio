package moodss.ia.ray;

import moodss.plummet.math.vec.Vector3;

public class CollisionObelisk {

    private final Vector3 origin;
    private final Vector3 direction;
    private final Type type;

    public CollisionObelisk(Vector3 origin, Vector3 direction, Type type) {
        this.origin = origin;
        this.direction = direction;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public Vector3 getDirection() {
        return direction;
    }

    public Vector3 getOrigin() {
        return origin;
    }

    public enum Type {
        MISS,
        BLOCK,
        ENTITY
    }
}
