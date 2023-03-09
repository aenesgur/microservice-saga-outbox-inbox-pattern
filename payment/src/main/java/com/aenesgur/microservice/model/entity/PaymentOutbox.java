package com.aenesgur.microservice.model.entity;

import com.aenesgur.microservice.event.enums.PaymentResultStatus;
import com.aenesgur.microservice.model.enums.PaymentOutboxStatus;
import com.aenesgur.microservice.model.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment_outbox")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentOutbox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime occuredOn;

    private LocalDateTime processedDate;

    @Column(nullable = false)
    private String payload;

    @Column(unique = true, nullable = false)
    private String idempotentToken;

    @Column(nullable = false)
    private PaymentOutboxStatus outboxStatus;

    @Column(nullable = false)
    private PaymentStatus paymentStatus;
}
