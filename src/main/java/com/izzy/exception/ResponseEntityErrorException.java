package com.izzy.exception;

public class ResponseEntityErrorException extends RuntimeException {
    public ResponseEntityErrorException(String message) {
        super(message);
    }
}
