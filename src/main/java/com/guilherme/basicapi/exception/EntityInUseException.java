package com.guilherme.basicapi.exception;

public class EntityInUseException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public EntityInUseException(String message) {
        super(message);
    }
}