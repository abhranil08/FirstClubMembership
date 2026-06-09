package com.firstclub.membership.service;

import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.entity.OrderHistory;
import com.firstclub.membership.entity.UserSubscription;
import com.firstclub.membership.enums.SubscriptionStatus;
import com.firstclub.membership.repository.MembershipTierRepository;
import com.firstclub.membership.repository.OrderHistoryRepository;
import com.firstclub.membership.repository.UserRepository;
import com.firstclub.membership.repository.UserSubscriptionRepository;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TierEvaluationService {

    private final OrderHistoryRepository orderHistoryRepository;
    private final UserSubscriptionRepository subscriptionRepository;
    private final MembershipTierRepository tierRepository;
    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;
    private final List<com.firstclub.membership.service.tier.TierEligibilityRule> rules;

    @Transactional
    public void evaluateUserTier(Long userId) {
        Optional<UserSubscription> optionalSubscription = subscriptionService.getActiveSubscription(userId);
        if (optionalSubscription.isEmpty()) {
            log.info("No active subscription for user {}. Skipping tier evaluation.", userId);
            return;
        }

        // Lock the user record to serialize concurrent operations
        User user = userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserSubscription currentSubscription = optionalSubscription.get();
        MembershipTier currentTier = currentSubscription.getTier();

        // 2. Calculate orders in the last 30 days
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<OrderHistory> recentOrders = orderHistoryRepository.findByUserIdAndOrderDateBetween(userId, thirtyDaysAgo, LocalDateTime.now());
        
        int orderCount = recentOrders.size();
        BigDecimal totalOrderValue = recentOrders.stream()
                .map(OrderHistory::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Find eligible tier based on highest criteria met
        List<MembershipTier> allTiers = tierRepository.findAll();
        // Sort tiers descending by min order value requirements (assuming higher tiers have higher reqs)
        allTiers.sort(Comparator.comparing(MembershipTier::getMinOrderValueInMonth).reversed());

        MembershipTier eligibleTier = null;
        for (MembershipTier tier : allTiers) {
            boolean matchesAll = rules.stream().allMatch(rule -> rule.isEligible(user, tier, orderCount, totalOrderValue));
            if (matchesAll) {
                eligibleTier = tier;
                break;
            }
        }

        if (eligibleTier == null) {
            // Fallback to lowest tier if no criteria met, or just don't downgrade automatically depending on business rules.
            // For now, if no criteria met, we do nothing.
            return;
        }

        // 4. Upgrade/Downgrade if eligible tier is different from current
        if (!eligibleTier.getId().equals(currentTier.getId())) {
            log.info("User {} is eligible for tier {}. Upgrading/Downgrading from tier {}.", userId, eligibleTier.getName(), currentTier.getName());
            subscriptionService.upgradeOrDowngrade(currentSubscription.getId(), null, eligibleTier.getId());
        }
    }

}

