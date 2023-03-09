package com.aenesgur.microservice.repository;

import com.aenesgur.microservice.model.entity.OrderInbox;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderInboxRepository extends org.springframework.data.jpa.repository.JpaRepository<OrderInbox, Long> {

    Optional<OrderInbox> findByIdempotentToken(String idempotentToken);

    void deleteByIdempotentToken(String idempotentToken);
}
