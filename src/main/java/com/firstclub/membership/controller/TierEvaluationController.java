package com.firstclub.membership.controller;

import com.firstclub.membership.service.TierEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/evaluations")
@RequiredArgsConstructor
public class TierEvaluationController {

    private final TierEvaluationService tierEvaluationService;

    @PostMapping("/users/{userId}/evaluate-tier")
    public ResponseEntity<Void> evaluateTier(@PathVariable Long userId) {
        tierEvaluationService.evaluateUserTier(userId);
        return ResponseEntity.ok().build();
    }
}
