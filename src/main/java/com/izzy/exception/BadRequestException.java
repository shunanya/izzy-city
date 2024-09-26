package com.izzy.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(String.format("Error: %s",message));
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
