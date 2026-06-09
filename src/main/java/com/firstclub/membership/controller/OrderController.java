package com.firstclub.membership.controller;

import com.firstclub.membership.dto.OrderRequestDto;
import com.firstclub.membership.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Void> placeOrder(@RequestBody OrderRequestDto request) {
        orderService.placeOrder(request.getUserId(), request.getAmount());
        return ResponseEntity.ok().build();
    }
}

