package moodss.ia.sfx.openal;

import moodss.ia.sfx.api.types.ErrorCondition;
import org.lwjgl.openal.AL10;

public class AlEnum extends AlcEnum {

    public static ErrorCondition from(int glParam) {
        return switch (glParam) {
            case AL10.AL_NO_ERROR -> ErrorCondition.NONE;
            case AL10.AL_INVALID_NAME -> ErrorCondition.INVALID_NAME;
            case AL10.AL_INVALID_VALUE -> ErrorCondition.INVALID_VALUE;
            case AL10.AL_BITS -> ErrorCondition.INVALID_OPERATION;
            case AL10.AL_OUT_OF_MEMORY -> ErrorCondition.OUT_OF_MEMORY;
            default -> null;
        };
    }
}
