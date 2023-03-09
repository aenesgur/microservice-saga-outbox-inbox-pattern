package com.aenesgur.microservice.service;

import com.aenesgur.microservice.model.entity.OrderInbox;
import com.aenesgur.microservice.repository.OrderInboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderInboxService {

    private final OrderInboxRepository orderInboxRepository;

    public void create(OrderInbox orderInbox){
        orderInboxRepository.save(orderInbox);

    }

    public void deleteByToken(String idempotentToken){
        orderInboxRepository.deleteByIdempotentToken(idempotentToken);
    }

    public Optional<OrderInbox> findByToken(String idempotentToken){
        return orderInboxRepository.findByIdempotentToken(idempotentToken);
    }
}
