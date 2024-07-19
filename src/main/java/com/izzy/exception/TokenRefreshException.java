package com.izzy.exception;

public class TokenRefreshException extends RuntimeException {
    public TokenRefreshException(String token, String message) {
        super(String.format("Error: Failed for [%s]: %s", token, message));
    }
}
