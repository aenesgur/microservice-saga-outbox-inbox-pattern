package com.aenesgur.microservice.model.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateDto {
    private BigDecimal totalPrice;
    private String cardNo;
    private String orderNote;
    private Long userId;
}
