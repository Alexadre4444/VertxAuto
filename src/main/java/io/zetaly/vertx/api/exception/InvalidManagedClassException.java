package io.zetaly.vertx.api.exception;

public class InvalidManagedClassException extends RuntimeException {
    public InvalidManagedClassException() {
    }

    public InvalidManagedClassException(String message) {
        super(message);
    }

    public InvalidManagedClassException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidManagedClassException(Throwable cause) {
        super(cause);
    }

    public InvalidManagedClassException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
