package com.izzy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(String.format("Error: %s",message));
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

}
