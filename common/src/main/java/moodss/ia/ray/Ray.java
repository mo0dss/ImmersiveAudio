package moodss.ia.ray;

import moodss.plummet.math.vec.Vector3;

//Highly based from UE5
public class Ray {

    /**
     * Ray origin point
     */
    private final Vector3 origin;

    /**
     * Ray direction vector (always normalized)
     */
    private final Vector3 direction;

    /** Default constructor initializes ray to Zero origin and Z-axis direction */
    public Ray()
    {
        this(Vector3.ZERO, Vector3.POSITIVE_Z, true);
    }

    /**
     * Initialize Ray with origin and no direction
     *
     * @param origin Ray Origin Point
     */
    public Ray(Vector3 origin) {
        this(origin, Vector3.ZERO, true);
    }

    /**
     * Initialize Ray with origin and direction
     *
     * @param origin Ray Origin Point
     * @param direction Ray Direction Vector
     * @param isNormalised Direction will be normalized unless this is passed as true (default false)
     */
    public Ray(Vector3 origin, Vector3 direction, boolean isNormalised) {
        this.origin = origin;
        this.direction = direction;

        if(!isNormalised) {
            this.direction.normalize();
        }
    }

    /**
     * Calculate position on ray at given distance/parameter
     *
     * @param distance Scalar distance along Ray
     * @return Point on Ray
     */
    public Vector3 pointAt(float distance) {
        Vector3 offset = Vector3.add(this.origin, distance);
        return Vector3.modulate(offset, this.direction);
    }

    /**
     * Calculates the dot product (distance from origin to closest point) for query point.
     *
     * @param point query point
     * @return The distance along this ray from origin to the closest point
     */
    public float dot(Vector3 point) {
        return Vector3.dot(Vector3.subtract(point, this.origin), this.direction);
    }

    /**
     * Calculates the reflected ray for query ray
     *
     * @param ray query Ray
     * @return The reflected ray
     */
    public Ray reflect(Ray ray) {
        return new Ray(ray.origin, Vector3.reflect(this.direction, ray.direction), true);
    }

    /**
     * Find minimum squared distance from query point to ray
     *
     * @param point query Point
     * @return squared distance to Ray
     */
    public float distanceToSquared(Vector3 point) {
        float dot = this.dot(point);
        if(dot < 0) {
            return Vector3.distanceSquared(this.origin, point);
        }

        Vector3 projectionPoint = Vector3.modulate(Vector3.add(this.origin, dot), this.direction);
        return projectionPoint.distanceToSquared(point);
    }

    /**
     * Find minimum squared distance from query ray to ray
     *
     * @param ray query Ray
     * @return squared distance to Ray
     */
    public float distanceToSquared(Ray ray) {
        return this.distanceToSquared(ray.origin);
    }

    /**
     * Find minimum distance from query point to ray
     *
     * @param point query Point
     * @return squared distance to Ray
     */
    public float distanceTo(Vector3 point) {
        float dot = this.dot(point);
        if(dot < 0) {
            return Vector3.distance(this.origin, point);
        }

        Vector3 projectionPoint = Vector3.modulate(Vector3.add(this.origin, dot), this.direction);
        return projectionPoint.distanceTo(point);
    }

    /**
     * Find minimum distance from query ray to ray
     *
     * @param ray query Ray
     * @return squared distance to Ray
     */
    public float distanceTo(Ray ray) {
        return this.distanceTo(ray.origin);
    }

    /**
     * Find closest point on ray to query point
     * @param point query point
     * @return closest point on Ray
     */
    public Vector3 closestPoint(Vector3 point) {
        float dot = this.dot(point);
        if(dot < 0) {
            return this.origin;
        }

        return Vector3.modulate(Vector3.add(this.origin, dot), this.direction);
    }

    /**
     * Find furthest point on ray to query point
     * @param point query point
     * @return furthest point on Ray
     */
    public Vector3 furthestPoint(Vector3 point) {
        float dot = this.dot(point);
        if(dot < 0) {
            return this.origin;
        }

        return Vector3.modulate(Vector3.subtract(this.origin, dot), this.direction);
    }

    public static Vector3 getOrigin(Ray ray) {
        return ray.origin;
    }

    public static Vector3 getDirection(Ray ray) {
        return ray.direction;
    }
}
