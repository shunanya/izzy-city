package com.izzy.exception;

import com.izzy.payload.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RestControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllExceptions(Exception ex) {
        ApiResponse response;
        if (ex instanceof AuthorizationDeniedException) {
            response = new ApiResponse(HttpStatus.FORBIDDEN, ex.getMessage());
        } else if (ex instanceof AuthenticationException) {
            response = new ApiResponse(HttpStatus.FORBIDDEN, ex.getMessage());
        } else if (ex instanceof ResourceAccessException) {
            response = new ApiResponse(HttpStatus.NOT_ACCEPTABLE, ex.getMessage());
        } else if (ex instanceof UnauthorizedException) {
            response = new ApiResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
        } else if (ex instanceof ResourceNotFoundException) {
            response = new ApiResponse(HttpStatus.NOT_FOUND, ex.getMessage());
        } else if (ex instanceof AccessDeniedException) {
            response = new ApiResponse(HttpStatus.FORBIDDEN, ex.getMessage());
        } else if (ex instanceof BadRequestException) {
            response = new ApiResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        } else if (ex instanceof JsonProcessingException) {
            response = new ApiResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
        } else if (ex instanceof NoHandlerFoundException) {
            response = new ApiResponse(HttpStatus.NOT_FOUND, "404 Error: Resource Not Found");
        } else {
            response = new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @ExceptionHandler(CustomException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResponse> handleCustomException(CustomException ex) {
        ApiResponse apiResponse = new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }
/*
      @ExceptionHandler(AuthenticationException.class)
       @ResponseStatus(code = HttpStatus.FORBIDDEN)
       public ResponseEntity<ApiResponse> handleAuthenticationException(AuthenticationException ex) {
           ApiResponse apiResponse = new ApiResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage());
           return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
       }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ApiResponse> handleUnauthorizedException(UnauthorizedException ex) {
        ApiResponse apiResponse = new ApiResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse> handleBadRequestException(BadRequestException ex) {
        ApiResponse apiResponse = new ApiResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ApiResponse apiResponse = new ApiResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    public ResponseEntity<ApiResponse> resolveException(AccessDeniedException ex) {
        ApiResponse apiResponse = new ApiResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
    }

    @ExceptionHandler({JsonProcessingException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse> handleJsonProcessingException(JsonProcessingException ex) {
        ApiResponse apiResponse = new ApiResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @ExceptionHandler({UnrecognizedPropertyException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse> handleUnrecognizedPropertyException(UnrecognizedPropertyException ex) {
        ApiResponse apiResponse = new ApiResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }


    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<ExceptionResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        String message = "Request method '" + ex.getMethod() + "' not supported. List of all supported methods - "
            + ex.getSupportedHttpMethods();
        List<String> messages = new ArrayList<>(1);
        messages.add(message);

        return new ResponseEntity<>(new ExceptionResponse(messages, HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(),
            HttpStatus.METHOD_NOT_ALLOWED.value()), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String message = "Please provide Request Body in valid JSON format";
        List<String> messages = new ArrayList<>(1);
        messages.add(message);
        return new ResponseEntity<>(new ExceptionResponse(messages, HttpStatus.BAD_REQUEST.getReasonPhrase(),
            HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }
*/
}

