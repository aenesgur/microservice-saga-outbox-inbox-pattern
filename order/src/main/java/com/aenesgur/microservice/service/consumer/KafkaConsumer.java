package com.aenesgur.microservice.service.consumer;


import com.aenesgur.microservice.event.type.PaymentResultEvent;
import com.aenesgur.microservice.model.entity.OrderInbox;
import com.aenesgur.microservice.model.enums.OrderInboxStatus;
import com.aenesgur.microservice.service.OrderInboxService;
import com.aenesgur.microservice.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;

@Service
@Slf4j
@AllArgsConstructor
public class KafkaConsumer {

    private final ObjectMapper objectMapper;

    private final OrderInboxService orderInboxService;

    private final OrderService orderService;

    private final PlatformTransactionManager transactionManager;


    @KafkaListener(topics = "${queue.kafka.topic.payment-complete-topic}", groupId = "${queue.kafka.group-id}")
    public void handlePaymentComplete(String event,
                                  @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY)  @NonNull String idempotentKey) throws JsonProcessingException {


        log.info("payment complete event consumed {}", event);

        if (orderInboxService.findByToken(idempotentKey).isPresent()){
            log.error("Payment inbox not created, {} already exist!", idempotentKey);
            return;
        }

        PaymentResultEvent paymentResultEvent = objectMapper.readValue(event, PaymentResultEvent.class);
        orderInboxService.create(createOrderInbox(event, idempotentKey));

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                orderService.updateOrderForPaymentResultEvent(paymentResultEvent);
                orderInboxService.deleteByToken(idempotentKey);
            }
        });

    }

    @KafkaListener(topics = "${queue.kafka.topic.payment-fail-topic}", groupId = "${queue.kafka.group-id}")
    public void handlePaymentFail(String event,
                                  @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY)  @NonNull String idempotentKey) throws JsonProcessingException {


        log.info("payment fail event consumed {}", event);
        PaymentResultEvent paymentResultEvent = objectMapper.readValue(event, PaymentResultEvent.class);

        orderInboxService.create(createOrderInbox(event, idempotentKey));

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                orderService.updateOrderForPaymentResultEvent(paymentResultEvent);
                orderInboxService.deleteByToken(idempotentKey);
            }
        });

    }

    private OrderInbox createOrderInbox(String event, String eventIdempotentToken){
        return OrderInbox.builder()
                .status(OrderInboxStatus.CREATED)
                .payload(event)
                .occuredOn(LocalDateTime.now())
                .idempotentToken(eventIdempotentToken)
                .build();
    }

}
