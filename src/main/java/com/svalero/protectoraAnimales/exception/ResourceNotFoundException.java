package com.svalero.protectoraAnimales.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException() {
        super("Recurso no encontrado.");
    }

    public ResourceNotFoundException(long id) {
        super("El recurso con ID " + id + " no ha sido encontrado.");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}