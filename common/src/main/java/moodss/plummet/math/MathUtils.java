package moodss.plummet.math;

import java.util.function.Supplier;

public class MathUtils {

    public static final float PHI = 1.618033988F;

    protected static final boolean USE_FMA = ((Supplier<Boolean>) () -> {
        try {
            Math.class.getDeclaredMethod("fma", float.class, float.class, float.class);
            return true;
        } catch (NoSuchMethodException ignored) {
            return false;
        }
    }).get();

    public static float fma(float a, float b, float c) {
        if(USE_FMA) {
            return Math.fma(a, b, c);
        }

        return (a * b) + c;
    }

    public static double fma(double a, double b, double c) {
        if(USE_FMA) {
            return Math.fma(a, b, c);
        }

        return (a * b) + c;
    }

    public static float fmn(float a, float b, float c) {
        return fma(a, b, -c);
    }

    public static double fmn(double a, double b, double c) {
        return fma(a, b, -c);
    }

    public static float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        }
        return Math.min(value, max);
    }

    public static float max(float value, float max) {
        return Math.min(value, max);
    }

    public static float lerp(float delta, float start, float end) {
        return fma((end - start), delta, start);
    }
}
