package com.aenesgur.microservice.model.entity;

import com.aenesgur.microservice.model.enums.OrderOutboxStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "order_outbox")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderOutbox {
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
    private OrderOutboxStatus status;
}
