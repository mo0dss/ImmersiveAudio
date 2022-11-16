package moodss.ia.sfx.api;

public class AudioException extends RuntimeException {

    private final int errorCode;

    public AudioException(int errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public AudioException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public AudioException(String message, Throwable cause, int errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return this.errorCode;
    }
}
