package com.firstclub.membership.controller;

import com.firstclub.membership.dto.CheckoutRequestDto;
import com.firstclub.membership.dto.CheckoutResponseDto;
import com.firstclub.membership.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping("/apply-benefits")
    public ResponseEntity<CheckoutResponseDto> applyBenefits(@RequestBody CheckoutRequestDto request) {
        return ResponseEntity.ok(checkoutService.applyMembershipBenefits(request));
    }
}
