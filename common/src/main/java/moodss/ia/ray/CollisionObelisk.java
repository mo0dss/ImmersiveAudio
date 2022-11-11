package moodss.ia.ray;

public class CollisionObelisk {

    private final Ray ray;
    private final Type type;

    public CollisionObelisk(Ray ray, Type type) {
        this.ray = ray;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public Ray getRay() {
        return ray;
    }

    public enum Type {
        MISS,
        BLOCK,
        ENTITY
    }
}
