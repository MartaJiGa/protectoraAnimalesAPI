package com.svalero.protectoraAnimales.exception;

public class ResourceNotFoundException extends Exception {

    public ResourceNotFoundException(){
        super();
    }

    public ResourceNotFoundException(long id){
        super("El recurso de ID " + id + " no ha sido encontrado.");
    }
}
