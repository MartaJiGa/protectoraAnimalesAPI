package com.svalero.protectoraAnimales.exception.adoption;

public class AnimalAlreadyAdoptedException  extends RuntimeException {

    public AnimalAlreadyAdoptedException(String message) {
        super(message);
    }
}
