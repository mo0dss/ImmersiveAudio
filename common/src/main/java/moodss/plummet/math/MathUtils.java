package moodss.plummet.math;

import org.apache.commons.math3.util.FastMath;

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

    public static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        return Math.min(value, max);
    }

    public static int average(float[] values, int preferred, int size) {
        float sum = 0F;
        float weightedSum = 0F;

        for(int idx = preferred; idx < size; idx++) {
            sum += values[idx];
            weightedSum += idx * values[idx];
        }

        return clamp(Math.round(weightedSum / sum), 0, size);
    }

    public static float max(float value, float max) {
        return Math.min(value, max);
    }

    public static float lerp(float delta, float start, float end) {
        return fma((end - start), delta, start);
    }

    public static float logBase(float x, float b) {
        return (float) (FastMath.log(x) / FastMath.log(b));
    }

    public static int floor(float value) {
        int flatValue = (int) value;

        if(flatValue < value) {
            return flatValue - 1;
        }

        return flatValue;
    }

    public static float pow(float value, float exponent) {
        return (float) FastMath.pow(value, exponent);
    }

    public static double pow(double value, double exponent) {
        return FastMath.pow(value, exponent);
    }

    public static int pow(int value, int exponent) {
        return (int) FastMath.pow(value, exponent);
    }

    public static float exp(float value) {
        return (float) FastMath.exp(value);
    }

    public static double exp(double value) {
        return FastMath.exp(value);
    }

    public static int exp(int value) {
        return (int) FastMath.exp(value);
    }
}
