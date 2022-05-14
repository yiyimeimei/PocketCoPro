package com.memoryleak.pocketcopro.util;

public class RestException extends RuntimeException {
    private final int status;

    public static final int NULL_RESPONSE = -1;
    public static final String NULL_RESPONSE_MESSAGE = "NULL_RESPONSE";

    public RestException(int status) {
        this.status = status;
    }

    public RestException(String message, int status) {
        super(message);
        this.status = status;
    }

    public RestException(String message, Throwable cause, int status) {
        super(message, cause);
        this.status = status;
    }

    public RestException(Throwable cause, int status) {
        super(cause);
        this.status = status;
    }

    public RestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int status) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.status = status;
    }

    public int status() {
        return status;
    }

    @Override
    public String toString() {
        return "RestException{" +
                "status=" + status + ", " +
                "cause=" + super.toString() +
                '}';
    }
}
