package com.guilherme.basicapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundRequestException extends RuntimeException {

    public NotFoundRequestException(String message) {
        super(message);
    }
}
