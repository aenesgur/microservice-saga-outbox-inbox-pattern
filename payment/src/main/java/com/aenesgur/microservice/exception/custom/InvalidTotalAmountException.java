package com.aenesgur.microservice.exception.custom;

public class InvalidTotalAmountException extends RuntimeException{
    public InvalidTotalAmountException(String message) {
        super(message);
    }
}
