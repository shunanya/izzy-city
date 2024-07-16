package com.izzy.exception;

import com.izzy.payload.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.io.Serial;

public class ResponseEntityErrorException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -3156815846745801694L;

    private transient ResponseEntity<ApiResponse> apiResponse;

    public ResponseEntityErrorException(ResponseEntity<ApiResponse> apiResponse) {
        this.apiResponse = apiResponse;
    }

    public ResponseEntity<ApiResponse> getApiResponse() {
        return apiResponse;
    }
}
