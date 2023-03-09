package com.aenesgur.microservice.service.consumer;

import com.aenesgur.microservice.event.type.PaymentCreateEvent;
import com.aenesgur.microservice.model.entity.PaymentInbox;
import com.aenesgur.microservice.model.enums.PaymentInboxStatus;
import com.aenesgur.microservice.service.concrete.PaymentInboxService;
import com.aenesgur.microservice.service.concrete.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@AllArgsConstructor
public class CreateOrderConsumer {

    private final PaymentService paymentService;
    private final PaymentInboxService paymentInboxService;

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topic.order-create-topic}", groupId = "${kafka.consumer.group-id}")
    public void handleOrderCreate(String event,
                                  @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY)  @NonNull String idempotentKey,
                                  Acknowledgment acknowledgment) throws JsonProcessingException {

        //TODO: If exception happened, It should add the event DLQ!
        log.info("create order event consumed {}", event);
        if (paymentInboxService.findByIdempotentToken(idempotentKey).isPresent()){
            log.error("Payment inbox not created, {} already exist!", idempotentKey);
            acknowledgment.acknowledge();
            return;
        }

        PaymentCreateEvent paymentCreateEvent = objectMapper.readValue(event, PaymentCreateEvent.class);

        paymentInboxService.create(createPaymentInbox(event, idempotentKey));

        paymentService.createForPaymentCreateEvent(paymentCreateEvent);
        acknowledgment.acknowledge();
    }

    private PaymentInbox createPaymentInbox(String event, String eventIdempotentToken){
        return PaymentInbox.builder()
                .status(PaymentInboxStatus.CREATED)
                .payload(event)
                .occuredOn(LocalDateTime.now())
                .idempotentToken(eventIdempotentToken)
                .build();
    }
}
