package com.izzy.exception;

public class UnrecognizedPropertyException  extends RuntimeException {
    public UnrecognizedPropertyException(String message) {
        super(message);
    }
}
