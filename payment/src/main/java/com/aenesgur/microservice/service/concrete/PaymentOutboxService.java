package com.aenesgur.microservice.service.concrete;

import com.aenesgur.microservice.model.entity.PaymentOutbox;
import com.aenesgur.microservice.repository.PaymentOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentOutboxService {

    private final PaymentOutboxRepository paymentOutboxRepository;

    public void create(PaymentOutbox paymentOutbox) {
        paymentOutboxRepository.save(paymentOutbox);
    }
}
