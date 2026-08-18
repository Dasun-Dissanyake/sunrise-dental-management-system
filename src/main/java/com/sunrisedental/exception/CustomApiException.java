package com.sunrisedental.exception;

/**
 * Custom application runtime exception for API business logic errors.
 */
public class CustomApiException extends RuntimeException {

    public CustomApiException(String message) {
        super(message);
    }

    public CustomApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
