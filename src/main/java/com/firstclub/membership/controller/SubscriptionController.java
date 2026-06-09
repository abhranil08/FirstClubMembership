package com.firstclub.membership.controller;

import com.firstclub.membership.dto.SubscriptionRequestDto;
import com.firstclub.membership.dto.SubscriptionResponseDto;
import com.firstclub.membership.dto.UserPerksDto;
import com.firstclub.membership.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<SubscriptionResponseDto> subscribe(@RequestBody SubscriptionRequestDto request) {
        return ResponseEntity.ok(subscriptionService.subscribe(request));
    }

    @PutMapping("/{subscriptionId}")
    public ResponseEntity<SubscriptionResponseDto> upgradeOrDowngrade(
            @PathVariable Long subscriptionId,
            @RequestParam(required = false) Long planId,
            @RequestParam(required = false) Long tierId) {
        return ResponseEntity.ok(subscriptionService.upgradeOrDowngrade(subscriptionId, planId, tierId));
    }

    @PostMapping("/{subscriptionId}/cancel")
    public ResponseEntity<Void> cancelSubscription(@PathVariable Long subscriptionId) {
        subscriptionService.cancelSubscription(subscriptionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<SubscriptionResponseDto> getCurrentSubscription(@PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.getCurrentSubscription(userId));
    }

    @GetMapping("/users/{userId}/perks")
    public ResponseEntity<UserPerksDto> getUserPerks(@PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.getUserPerks(userId));
    }
}

