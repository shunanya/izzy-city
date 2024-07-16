package com.izzy.exception;

import com.izzy.payload.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class TokenRefreshException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 1L;

  private String message;
  private String token;

  private transient ApiResponse apiResponse;

  public TokenRefreshException(String token, String message) {
    super();
    this.token = token;
    this.message = message;
  }

  public ApiResponse getApiResponse() {
    return apiResponse;
  }

  private void setApiResponse() {
    apiResponse = new ApiResponse(Boolean.FALSE, String.format("Failed for [%s]: %s", token, message));
  }

}
