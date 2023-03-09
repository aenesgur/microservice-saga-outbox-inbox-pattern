package com.aenesgur.microservice.exception.custom;

public class WrongCardNumberException extends RuntimeException{
    public WrongCardNumberException(String message) {
        super(message);
    }
}
