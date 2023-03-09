package com.aenesgur.microservice.repository;

import com.aenesgur.microservice.model.entity.PaymentInbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentInboxRepository extends JpaRepository<PaymentInbox, Long> {

    Optional<PaymentInbox> findByIdempotentToken(String idempotentToken);

    void deleteByIdempotentToken(String idempotentToken);
}
