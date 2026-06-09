package com.firstclub.membership.service;

import com.firstclub.membership.entity.OrderHistory;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.repository.OrderHistoryRepository;
import com.firstclub.membership.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderHistoryRepository orderHistoryRepository;
    private final UserRepository userRepository;
    private final TierEvaluationService tierEvaluationService;

    @Transactional
    public void placeOrder(Long userId, BigDecimal amount) {
        User user = userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        OrderHistory order = OrderHistory.builder()
                .user(user)
                .amount(amount)
                .orderDate(LocalDateTime.now())
                .build();

        orderHistoryRepository.save(order);
        log.info("Placed order for user {} with amount {}", userId, amount);

        // Auto-trigger tier evaluation immediately when an order is completed
        tierEvaluationService.evaluateUserTier(userId);
    }
}
