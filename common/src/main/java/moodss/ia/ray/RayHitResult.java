package moodss.ia.ray;

import java.util.Objects;

public class RayHitResult {

    /**
     * The ray this hit will yield.
     */
    private final Ray ray;

    /**
     * The type of result
     */
    private final RayHitResult.Type type;

    public RayHitResult(Ray ray, RayHitResult.Type type) {
        this.ray = ray;
        this.type = type;
    }

    public Ray ray() {
        return this.ray;
    }

    public Type type() {
        return this.type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RayHitResult that = (RayHitResult) o;

        if (!Objects.equals(this.ray, that.ray)) return false;
        return this.type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.ray, this.type);
    }

    public enum Type {
        MISS,

        BLOCK,

        ENTITY
    }
}
