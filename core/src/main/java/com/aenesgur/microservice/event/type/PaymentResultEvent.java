package com.aenesgur.microservice.event.type;

import com.aenesgur.microservice.event.enums.PaymentResultStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResultEvent {
    private String cardNo;
    private String orderRef;
    private BigDecimal totalPrice;
    private String idempotentToken;
    private PaymentResultStatus status;
    private String message;
}
