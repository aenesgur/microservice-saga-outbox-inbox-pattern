package com.aenesgur.microservice.model.dto;

import com.aenesgur.microservice.model.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
@Data
public class OrderResponseDto {
    private String ref;
    private BigDecimal totalPrice;
    private String orderNote;
    private OrderStatus status;
    private String message;
}
