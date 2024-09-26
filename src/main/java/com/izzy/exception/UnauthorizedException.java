package com.izzy.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(String.format("Error: %s",message));
    }
}
