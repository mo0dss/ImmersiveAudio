package moodss.ia.sfx.api;

import moodss.ia.sfx.api.types.ErrorCondition;

public class AudioException extends RuntimeException {

    private final ErrorCondition condition;

    public AudioException(ErrorCondition condition) {
        super();
        this.condition = condition;
    }

    public AudioException(String message, ErrorCondition condition) {
        super(message);
        this.condition = condition;
    }

    public AudioException(String message, Throwable cause, ErrorCondition condition) {
        super(message, cause);
        this.condition = condition;
    }

    public ErrorCondition getCondition() {
        return this.condition;
    }
}
