package com.guilherme.basicapi.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Collections;

@Getter
@Setter
public class ApiException extends RuntimeException {

    private ErrorResponse errorResponse;

    public ApiException(String message) {
        super(message);
        errorResponse = new ErrorResponse();
        errorResponse.setDetails("");
        errorResponse.setMessage(Collections.singletonList(message));
        errorResponse.setCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST);
    }

    public ApiException(String message, Exception e) {
        super(message);
        e.printStackTrace();
        errorResponse = new ErrorResponse();
        errorResponse.setDetails(e.getMessage());
        errorResponse.setMessage(Collections.singletonList(message));
        errorResponse.setCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST);
    }

    public ApiException(String message, String details) {
        super(message);
        errorResponse = new ErrorResponse();
        errorResponse.setDetails(details);
        errorResponse.setMessage(Collections.singletonList(message));
        errorResponse.setCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST);
    }

}