package com.izzy.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.http.HttpStatus;

import java.io.Serializable;


@JsonPropertyOrder({
    "status",
    "message"
})
public class ApiResponse implements Serializable {

    @JsonProperty("status")
    private int status;

    @JsonProperty("message")
    private String message;

    public ApiResponse(int statusCode, String message) {
        this.status = statusCode;
        this.message = message;
    }

    public ApiResponse(HttpStatus status, String message) {
        this.status = status.value();
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public HttpStatus getHttpStatus() {
        HttpStatus httpStatus = HttpStatus.resolve(status);
        return httpStatus != null ? httpStatus : HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public String getMessage() {
        return message;
    }
}
