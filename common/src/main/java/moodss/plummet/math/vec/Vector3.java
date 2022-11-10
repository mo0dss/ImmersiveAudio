package moodss.plummet.math.vec;

import moodss.plummet.math.MathUtils;

import java.util.StringJoiner;

public class Vector3 {
    /**
     * Returns a empty {@link #Vector3(float, float, float)}. (0,0,0)
     */
    public static final Vector3 ZERO = new Vector3(0.0F, 0.0F, 0.0F);

    /**
     * Returns a positive X {@link #Vector3(float, float, float)}. (1,0,0)
     */
    public static Vector3 POSITIVE_X = new Vector3(1.0F, 0.0F, 0.0F);

    /**
     * Returns a positive Y {@link #Vector3(float, float, float)}. (0,1,0)
     */
    public static Vector3 POSITIVE_Y = new Vector3(0.0F, 1.0F, 0.0F);

    /**
     * Returns a positive Z {@link #Vector3(float, float, float)}. (0,0,1)
     */
    public static Vector3 POSITIVE_Z = new Vector3(0.0F, 0.0F, 1.0F);

    /**
     * Returns a negative X {@link #Vector3(float, float, float)}. (-1,0,0)
     */
    public static Vector3 NEGATIVE_X = new Vector3(-1.0F, 0.0F, 0.0F);

    /**
     * Returns a positive Y {@link #Vector3(float, float, float)}. (0,-1,0)
     */
    public static Vector3 NEGATIVE_Y = new Vector3(0.0F, -1.0F, 0.0F);

    /**
     * Returns a negative Z {@link #Vector3(float, float, float)}. (0,0,-1)
     */
    public static Vector3 NEGATIVE_Z = new Vector3(0.0F, 0.0F, -1.0F);

    public static Vector3[] ALL = new Vector3[] {NEGATIVE_X, NEGATIVE_Y, NEGATIVE_Z, POSITIVE_X, POSITIVE_Y, POSITIVE_Z};

    private float x, y, z;

    /**
     * Default empty constructor
     */
    public Vector3() {
        //NO-OP
    }

    /**
     * @param x The initial value for the x-component of this vector.
     * @param y The initial value for the y-component of this vector.
     * @param z The initial value for the z-component of this vector.
     */
    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Converts the vector into a unit vector.
     */
    public void normalize()
    {
        float length = length();
        if (length == 0)
        {
            return;
        }

        float num = 1.0F / length;
        this.x *= num;
        this.y *= num;
        this.z *= num;
    }

    /**
     * Calculates the distance between two vectors.
     *
     * @param position The second vector to calculate the distance to.
     * @return The distance to the other vector.
     */
    public float distanceTo(Vector3 position)
    {
        return Vector3.subtract(position, this).length();
    }

    /**
     * Calculates the squared distance between two vectors.
     *
     * @param position The second vector to calculate the distance to.
     * @return The distance to the other vector.
     */
    public float distanceToSquared(Vector3 position)
    {
        return distanceSquared(position, this);
    }

    /**
     * Calculates the length of the vector.
     *
     * @return The length of the vector.
     */
    public float length()
    {
        return (float) Math.sqrt(MathUtils.fma(this.x, this.x, MathUtils.fma(this.y, this.y, this.z * this.z)));
    }

    /**
     * Calculates the squared length of the vector.
     *
     * @return The squared length of the vector.
     */
    public float lengthSquared()
    {
        return MathUtils.fma(this.x, this.x, MathUtils.fma(this.y, this.y, this.z * this.z));
    }

    /**
     * @return The x-component of this vector.
     */
    public float getX() {
        return this.x;
    }

    /**
     * @return The y-component of this vector.
     */
    public float getY() {
        return this.y;
    }

    /**
     * @return The z-component of this vector.
     */
    public float getZ() {
        return this.z;
    }

    /**
     * @param o Object to make the comparison with.
     * @return <c>true</c> if the current instance is equal to the specified object; <c>false</c> otherwise.
     */
    @Override
    public boolean equals(Object o)
    {
        if (o == null || getClass() != o.getClass()) return false;

        Vector3 vector3 = (Vector3) o;

        if (Float.compare(vector3.x, this.x) != 0) return false;
        if (Float.compare(vector3.y, this.y) != 0) return false;
        return Float.compare(vector3.z, this.z) == 0;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Vector3.class.getSimpleName() + "[", "]")
                .add("x=" + x)
                .add("y=" + y)
                .add("z=" + z)
                .toString();
    }

    /**
     * Returns the hash code for this instance.
     *
     * @return A 32-bit signed integer hash code.
     */
    @Override
    public int hashCode()
    {
        int result = (this.x != +0.0f ? Float.floatToIntBits(this.x) : 0);
        result = 31 * result + (this.y != +0.0f ? Float.floatToIntBits(this.y) : 0);
        result = 31 * result + (this.z != +0.0f ? Float.floatToIntBits(this.z) : 0);
        return result;
    }

    /**
     * Converts the vector into a unit vector.
     *
     * @param vector The vector to normalize.
     * @return The normalized vector.
     */
    public static Vector3 normalize(Vector3 vector)
    {
        vector.normalize();
        return vector;
    }

    /**
     * Subtracts two vectors.
     *
     * @param left The first vector to subtract.
     * @param right The second vector to subtract.
     * @return The difference of the two vectors.
     */
    public static Vector3 subtract(Vector3 left, Vector3 right)
    {
        return new Vector3(left.getX() - right.getX(), left.getY() - right.getY(), left.getZ() - right.getZ());
    }

    public static Vector3 subtract(Vector3 left, float rightX, float rightY, float rightZ)
    {
        return new Vector3(left.getX() - rightX, left.getY() - rightY, left.getZ() - rightZ);
    }

    public static Vector3 subtract(float rightX, float leftY, float leftZ, Vector3 right)
    {
        return new Vector3(rightX - right.getX(), leftY - right.getY(), leftZ - right.getZ());
    }

    /**
     * Adds two vectors.
     *
     * @param left The first vector to add.
     * @param right The second vector to add.
     * @return The sum of the two vectors.
     */
    public static Vector3 add(Vector3 left, Vector3 right)
    {
        return new Vector3(left.getX() + right.getX(), left.getY() + right.getY(), left.getZ() + right.getZ());
    }

    public static Vector3 add(Vector3 left, float rightX, float rightY, float rightZ)
    {
        return new Vector3(left.getX() + rightX, left.getY() + rightY, left.getZ() + rightZ);
    }

    public static Vector3 add(Vector3 left, float amount)
    {
        return new Vector3(left.getX() + amount, left.getY() + amount, left.getZ() + amount);
    }

    /**
     * Calculates the dot product of two vectors.
     *
     * @param left First source vector.
     * @param right Second source vector.
     * @return The dot product of the two vectors.
     */
    public static float dot(Vector3 left, Vector3 right)
    {
        return dot(left, right.getX(), right.getY(), right.getZ());
    }

    public static float dot(Vector3 left, float rightX, float rightY, float rightZ)
    {
        return MathUtils.fma(left.getX(), rightX, MathUtils.fma(left.getY(), rightY, left.getZ() * rightZ));
    }

    /**
     * Calculates the squared distance between two vectors.
     *
     * @param position1 The first vector to calculate the squared distance to the second vector.
     * @param position2 The second vector to calculate the squared distance to the first vector.
     * @return The squared distance between the two vectors.
     */
    public static float distanceSquared(Vector3 position1, Vector3 position2)
    {
        return Vector3.subtract(position1, position2).lengthSquared();
    }

    /**
     * Modulates a vector by another.
     *
     * @param left The first vector to modulate.
     * @param right The second vector to modulate.
     * @return The multiplied vector.
     */
    public static Vector3 modulate(Vector3 left, Vector3 right)
    {
        return new Vector3(left.getX() * right.getX(), left.getY() * right.getY(), left.getZ() * right.getZ());
    }

    public static Vector3 modulate(Vector3 left, float amount)
    {
        return new Vector3(left.getX() * amount, left.getY() * amount, left.getZ() * amount);
    }

    /**
     * Returns the reflection of a vector off a surface that has the specified normal.
     *
     * Reflect only gives the direction of a reflection off a surface, it does not determine
     * whether the original vector was close enough to the surface to hit it.
     *
     * @param vector The vector to project onto the plane.
     * @param normal Normal of the surface.
     * @return The reflected vector.
     */
    public static Vector3 reflect(Vector3 vector, Vector3 normal)
    {
        Vector3 result = ZERO;
        float dot = MathUtils.fma(vector.getX(), normal.getX(), MathUtils.fma(vector.getY(), normal.getY(), vector.getZ() * normal.getZ()));

        return new Vector3(
                vector.getX() - ((2.0f * dot) * normal.getX()),
                vector.getY() - ((2.0f * dot) * normal.getY()),
                vector.getZ() - ((2.0f * dot) * normal.getZ())
        );
    }

    public static Vector3 getFacing(Vector3 facing) {
        Vector3 direction = Vector3.NEGATIVE_Z;

        float prevOccurrence = Float.MIN_VALUE;
        for(Vector3 dir : ALL) {
            float dot = Vector3.dot(facing, dir.getX(), dir.getY(), dir.getZ());
            if(dot > prevOccurrence) {
                prevOccurrence = dot;
                direction = dir;
            }
        }

        return direction;
    }
}
