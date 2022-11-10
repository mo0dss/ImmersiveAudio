package moodss.ia.util;

import moodss.plummet.math.vec.Vector3;
import net.minecraft.util.math.Direction;

public class DirectionUtil {

    public static Direction getFacing(Vector3 facing) {
        Direction direction = Direction.NORTH;

        float prevOccurrence = Float.MIN_VALUE;
        for(Direction dir : Direction.values()) {
            float dot = Vector3.dot(facing, dir.getOffsetX(), dir.getOffsetY(), dir.getOffsetZ());
            if(dot > prevOccurrence) {
                prevOccurrence = dot;
                direction = dir;
            }
        }

        return direction;
    }

    public static Vector3 get(Direction direction) {
        return switch(direction) {
            case DOWN -> Vector3.NEGATIVE_Y;
            case UP -> Vector3.POSITIVE_Y;
            case NORTH -> Vector3.NEGATIVE_Z;
            case SOUTH -> Vector3.POSITIVE_Z;
            case WEST -> Vector3.NEGATIVE_X;
            case EAST -> Vector3.POSITIVE_X;
        };
    }
}
