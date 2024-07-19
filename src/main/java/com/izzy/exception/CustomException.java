package com.izzy.exception;

public class CustomException extends RuntimeException {

    public CustomException(int status, String message) {
        super(String.format("Error: %s with status %s", message, status));
    }

    public CustomException(int status, String message, Throwable exception) {
        super(exception);
    }
}