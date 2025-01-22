package com.svalero.protectoraAnimales.exception.runtime;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}