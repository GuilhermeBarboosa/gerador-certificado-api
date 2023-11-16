package com.guilherme.basicapi.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
public class ErrorResponse {

    private List<String> message;
    private String details;
    private String developerDetails;
    private HttpStatus status;
    private int code;

} 