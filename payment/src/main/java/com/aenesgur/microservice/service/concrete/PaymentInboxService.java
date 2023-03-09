package com.aenesgur.microservice.service.concrete;

import com.aenesgur.microservice.model.entity.PaymentInbox;
import com.aenesgur.microservice.repository.PaymentInboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentInboxService {

    private final PaymentInboxRepository paymentInboxRepository;

    public void create(PaymentInbox paymentInbox) {
        paymentInboxRepository.save(paymentInbox);
    }

    public void deleteByToken(String idempotentToken){
        paymentInboxRepository.deleteByIdempotentToken(idempotentToken);
    }

    public Optional<PaymentInbox> findByIdempotentToken(String idempotentToken){
        return paymentInboxRepository.findByIdempotentToken(idempotentToken);
    }
}
