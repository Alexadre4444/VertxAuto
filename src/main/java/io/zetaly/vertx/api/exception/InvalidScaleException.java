package io.zetaly.vertx.api.exception;

public class InvalidScaleException extends RuntimeException {
    public InvalidScaleException() {
    }

    public InvalidScaleException(String message) {
        super(message);
    }

    public InvalidScaleException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidScaleException(Throwable cause) {
        super(cause);
    }

    public InvalidScaleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
