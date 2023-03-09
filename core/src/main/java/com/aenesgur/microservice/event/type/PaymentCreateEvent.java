package com.aenesgur.microservice.event.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateEvent {

    private String cardNo;
    private String orderRef;
    private BigDecimal totalPrice;
    private String idempotentToken;
}
