package com.aenesgur.microservice.controller;

import com.aenesgur.microservice.model.dto.OrderCreateDto;
import com.aenesgur.microservice.model.dto.OrderResponseDto;
import com.aenesgur.microservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/{ref}")
    public ResponseEntity<OrderResponseDto> getOrderByRef(@PathVariable String ref){
        return new ResponseEntity<>(orderService.getOrderByRef(ref), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderCreateDto orderCreateDto) {
        return new ResponseEntity<>(orderService.save(orderCreateDto), HttpStatus.CREATED);
    }
}
