package com.izzy.payload.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serial;
import java.io.Serializable;


@JsonPropertyOrder({
        "status",
        "message"
})
public class ApiResponse implements Serializable {

    @Serial
    @JsonIgnore
    private static final long serialVersionUID = 7702134516418120340L;

    @JsonProperty("status")
    private int status;

    @JsonProperty("message")
    private String message;


    public ApiResponse(int statusCode, String message) {
        this.status = statusCode;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
