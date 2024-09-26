package com.izzy.exception;

public class JsonProcessingException extends RuntimeException {
    public JsonProcessingException(String message) {
        super(String.format("Error: %s",message));
    }
}
