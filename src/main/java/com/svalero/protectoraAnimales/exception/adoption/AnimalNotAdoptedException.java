package com.svalero.protectoraAnimales.exception.adoption;

public class AnimalNotAdoptedException  extends RuntimeException {
    public AnimalNotAdoptedException(String message) {
        super(message);
    }
}
