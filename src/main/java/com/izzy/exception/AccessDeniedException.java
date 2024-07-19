package com.izzy.exception;
public class AccessDeniedException extends RuntimeException {
     public AccessDeniedException(String message) {
        super(String.format("Error: "+message));
    }

    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
