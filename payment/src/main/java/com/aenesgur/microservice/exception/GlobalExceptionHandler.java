package com.aenesgur.microservice.exception;

import com.aenesgur.microservice.event.Producer;
import com.aenesgur.microservice.exception.custom.InvalidTotalAmountException;
import com.aenesgur.microservice.exception.custom.WrongCardNumberException;
import com.aenesgur.microservice.model.entity.PaymentOutbox;
import com.aenesgur.microservice.model.enums.PaymentOutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final Producer producer;

    @ExceptionHandler({WrongCardNumberException.class, InvalidTotalAmountException.class})
    public void handlePayment(Exception ex) {
        log.error(ex.getMessage(), ex);

    }

    @ExceptionHandler({Exception.class})
    public void handleGeneralException(Exception ex) {
        log.error(ex.getMessage(), ex);
    }
}
