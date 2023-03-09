package com.aenesgur.microservice.service;

import com.aenesgur.microservice.model.entity.OrderOutbox;
import com.aenesgur.microservice.repository.OrderOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderOutboxService {

    private final OrderOutboxRepository orderOutboxRepository;

    public void save(OrderOutbox orderOutbox){
        orderOutboxRepository.save(orderOutbox);
    }


}
