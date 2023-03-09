package com.aenesgur.microservice.repository;

import com.aenesgur.microservice.model.entity.PaymentOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentOutboxRepository extends JpaRepository<PaymentOutbox, Long> {
}
