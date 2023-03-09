package com.aenesgur.microservice.service;

import com.aenesgur.microservice.event.enums.PaymentResultStatus;
import com.aenesgur.microservice.event.type.PaymentCreateEvent;
import com.aenesgur.microservice.event.type.PaymentResultEvent;
import com.aenesgur.microservice.exception.custom.OrderNotFoundException;
import com.aenesgur.microservice.model.dto.OrderCreateDto;
import com.aenesgur.microservice.model.dto.OrderResponseDto;
import com.aenesgur.microservice.model.entity.Order;
import com.aenesgur.microservice.model.entity.OrderOutbox;
import com.aenesgur.microservice.model.enums.OrderOutboxStatus;
import com.aenesgur.microservice.model.enums.OrderStatus;
import com.aenesgur.microservice.repository.OrderOutboxRepository;
import com.aenesgur.microservice.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderOutboxService orderOutboxService;
    private final ObjectMapper objectMapper;

    public OrderResponseDto getOrderByRef(String ref){
        Order order = orderRepository.findByRef(ref)
                .orElseThrow(() -> new OrderNotFoundException("order not found for this ref: ".concat(String.valueOf(ref))));
        return toOrderResponseDto(order);
    }
    @Transactional
    public OrderResponseDto save(OrderCreateDto orderCreateDto){
        Order order = orderRepository.save(toOrderEntity(orderCreateDto));
        orderOutboxService.save(toOutboxEntity(order));
        return toOrderResponseDto(order);
    }

    public void updateOrderForPaymentResultEvent(PaymentResultEvent paymentResultEvent){
        Order order = orderRepository.findByRef(paymentResultEvent.getOrderRef()).orElseThrow(() -> new OrderNotFoundException("order not found for this ref: ".concat(String.valueOf(paymentResultEvent.getOrderRef()))));
        order.setStatus(paymentResultEvent.getStatus().name().equals(PaymentResultStatus.PAID.name()) ? OrderStatus.PAID : OrderStatus.CANCELLED);
        order.setMessage(paymentResultEvent.getMessage());
    }

    private OrderOutbox toOutboxEntity(Order order){
        String payload = null;
        String idempotentToken = UUID.randomUUID().toString();
        try{
            payload = objectMapper.writeValueAsString(toPaymentCreateEvent(order, idempotentToken));

            PaymentCreateEvent paymentCreateEvent = objectMapper.readValue(payload, PaymentCreateEvent.class);
            String aaa = "";
        }catch (JsonProcessingException ex){
            log.error("Object could not convert to String. Object: {}", order.toString());
            throw new RuntimeException(ex);
        }

        return OrderOutbox.builder()
                .status(OrderOutboxStatus.CREATED)
                .occuredOn(LocalDateTime.now())
                .idempotentToken(idempotentToken)
                .payload(payload)
                .build();
    }

    private PaymentCreateEvent toPaymentCreateEvent(Order order, String idempotentToken){
        return PaymentCreateEvent.builder()
                .orderRef(order.getRef())
                .cardNo(order.getCardNo())
                .totalPrice(order.getTotalPrice())
                .idempotentToken(idempotentToken)
                .build();
    }
    private Order toOrderEntity(OrderCreateDto orderCreateDto){
        return Order.builder()
                .orderNote(orderCreateDto.getOrderNote())
                .cardNo(orderCreateDto.getCardNo())
                .userId(orderCreateDto.getUserId())
                .totalPrice(orderCreateDto.getTotalPrice())
                .status(OrderStatus.CREATED)
                .ref(UUID.randomUUID().toString())
                .build();
    }

    private OrderResponseDto toOrderResponseDto(Order order){
        return OrderResponseDto.builder()
                .ref(order.getRef())
                .message(order.getMessage())
                .status(order.getStatus())
                .orderNote(order.getOrderNote())
                .totalPrice(order.getTotalPrice())
                .build();
    }
}
