package com.aenesgur.microservice.service.concrete;

import com.aenesgur.microservice.event.enums.PaymentResultStatus;
import com.aenesgur.microservice.event.type.PaymentCreateEvent;
import com.aenesgur.microservice.event.type.PaymentResultEvent;
import com.aenesgur.microservice.model.entity.Payment;
import com.aenesgur.microservice.model.entity.PaymentOutbox;
import com.aenesgur.microservice.model.enums.PaymentOutboxStatus;
import com.aenesgur.microservice.model.enums.PaymentStatus;
import com.aenesgur.microservice.repository.PaymentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final PaymentInboxService paymentInboxService;

    private final PaymentOutboxService paymentOutboxService;

    private final ObjectMapper objectMapper;

    @Transactional
    public void createForPaymentCreateEvent(PaymentCreateEvent paymentCreateEvent) {
        Payment payment = createPaymentEntity(paymentCreateEvent, PaymentStatus.CREATED);
        paymentRepository.save(payment);

        if(validate(paymentCreateEvent)){
            payment.setStatus(PaymentStatus.PAID);
            paymentRepository.save(payment);

            paymentInboxService.deleteByToken(paymentCreateEvent.getIdempotentToken());
            log.info("Payment created and status PAID: {}", payment.getRef());
            paymentOutboxService.create(createPaymentOutbox(payment));
        }
    }

    private boolean validate(PaymentCreateEvent paymentCreateEvent){
        if (paymentCreateEvent.getCardNo().startsWith("1") || paymentCreateEvent.getCardNo().startsWith("2")){
            paymentInboxService.deleteByToken(paymentCreateEvent.getIdempotentToken());
            Payment payment = createPaymentEntity(paymentCreateEvent, PaymentStatus.FAILED);
            payment.setMessage("Card number is wrong!");
            paymentRepository.save(payment);
            PaymentOutbox paymentOutbox = createPaymentOutbox(payment);
            paymentOutboxService.create(paymentOutbox);

            return false;
        }
        else if (paymentCreateEvent.getTotalPrice().compareTo(BigDecimal.ZERO) == 0 || paymentCreateEvent.getTotalPrice().compareTo(BigDecimal.ONE) == 0){
            paymentInboxService.deleteByToken(paymentCreateEvent.getIdempotentToken());
            Payment payment = createPaymentEntity(paymentCreateEvent, PaymentStatus.FAILED);
            payment.setMessage("Total amount is wrong!");
            paymentRepository.save(payment);
            PaymentOutbox paymentOutbox = createPaymentOutbox(payment);
            paymentOutboxService.create(paymentOutbox);

            return false;
        }
        return true;
    }

    private Payment createPaymentEntity(PaymentCreateEvent paymentCreateEvent, PaymentStatus paymentStatus){
        return Payment.builder()
                .orderRef(paymentCreateEvent.getOrderRef())
                .cardNo(paymentCreateEvent.getCardNo())
                .status(paymentStatus)
                .totalPrice(paymentCreateEvent.getTotalPrice())
                .ref(UUID.randomUUID().toString())
                .build();
    }

    private PaymentOutbox createPaymentOutbox(Payment payment){
        String payload = null;
        String idempotentToken = UUID.randomUUID().toString();
        try{
            payload = objectMapper.writeValueAsString(toPaymentResultEvent(payment, idempotentToken, payment.getMessage()));

        }catch (JsonProcessingException ex){
            log.error("Object could not convert to String. Object: {}", payment.toString());
            throw new RuntimeException(ex);
        }
        return PaymentOutbox.builder()
                .outboxStatus(PaymentOutboxStatus.CREATED)
                .paymentStatus(payment.getStatus())
                .occuredOn(LocalDateTime.now())
                .idempotentToken(idempotentToken)
                .payload(payload)
                .build();
    }

    private PaymentResultEvent toPaymentResultEvent(Payment payment, String idempotentToken, String message){
        return PaymentResultEvent.builder()
                .orderRef(payment.getOrderRef())
                .cardNo(payment.getCardNo())
                .totalPrice(payment.getTotalPrice())
                .idempotentToken(idempotentToken)
                .status(payment.getStatus().name().equals(PaymentStatus.PAID) ? PaymentResultStatus.PAID : PaymentResultStatus.FAILED)
                .message(message)
                .build();
    }
}
