package com.izzy.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("Error: %s not found with %s: '%s'", resourceName, fieldName, fieldValue));
    }
}
