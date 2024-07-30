package com.izzy.exception;

public class UnrecognizedPropertyException  extends RuntimeException {
    public UnrecognizedPropertyException(String message) {
        super(message);
    }

    public UnrecognizedPropertyException(String propertyName, Object property) {
        super(String.format("Error: not recognized property '%s' = %s", propertyName, property));
    }
}
