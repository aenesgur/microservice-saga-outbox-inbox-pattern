package com.aenesgur.microservice.model.entity;

import com.aenesgur.microservice.model.enums.OrderInboxStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_inbox")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderInbox {
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
    private OrderInboxStatus status;
}
