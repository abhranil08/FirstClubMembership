package com.firstclub.membership.service;

import com.firstclub.membership.dto.*;
import com.firstclub.membership.entity.*;
import com.firstclub.membership.enums.PlanDuration;
import com.firstclub.membership.enums.SubscriptionStatus;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.exception.ValidationException;
import com.firstclub.membership.repository.MembershipPlanRepository;
import com.firstclub.membership.repository.MembershipTierRepository;
import com.firstclub.membership.repository.UserRepository;
import com.firstclub.membership.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final UserSubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final MembershipPlanRepository planRepository;
    private final MembershipTierRepository tierRepository;

    @Transactional
    public SubscriptionResponseDto subscribe(SubscriptionRequestDto request) {
        User user = userRepository.findByIdForUpdate(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if user already has an active subscription
        subscriptionRepository.findByUserIdAndStatus(user.getId(), SubscriptionStatus.ACTIVE)
                .ifPresent(sub -> {
                    throw new ValidationException("User already has an active subscription");
                });

        MembershipPlan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        MembershipTier tier = tierRepository.findById(request.getTierId())
                .orElseThrow(() -> new ResourceNotFoundException("Tier not found"));

        if (tier.getEligibleCohort() != null && !tier.getEligibleCohort().equalsIgnoreCase(user.getCohortName())) {
            throw new ValidationException("User does not belong to the eligible cohort for this tier");
        }

        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = calculateEndDate(startDate, plan.getDuration());

        UserSubscription subscription = UserSubscription.builder()
                .user(user)
                .plan(plan)
                .tier(tier)
                .startDate(startDate)
                .endDate(endDate)
                .status(SubscriptionStatus.ACTIVE)
                .build();

        subscription = subscriptionRepository.save(subscription);
        return mapToDto(subscription);
    }

    @Transactional
    public SubscriptionResponseDto upgradeOrDowngrade(Long subscriptionId, Long newPlanId, Long newTierId) {
        UserSubscription currentSubscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        if (currentSubscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new ValidationException("Can only modify active subscriptions");
        }

        User user = userRepository.findByIdForUpdate(currentSubscription.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Handle Plan Change
        if (newPlanId != null && !newPlanId.equals(currentSubscription.getPlan().getId())) {
            MembershipPlan newPlan = planRepository.findById(newPlanId)
                    .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));
            currentSubscription.setPlan(newPlan);
            // Recalculate end date based on new plan (simplified logic - from now)
            currentSubscription.setEndDate(calculateEndDate(LocalDateTime.now(), newPlan.getDuration()));
        }

        // Handle Tier Change
        if (newTierId != null && !newTierId.equals(currentSubscription.getTier().getId())) {
            MembershipTier newTier = tierRepository.findById(newTierId)
                    .orElseThrow(() -> new ResourceNotFoundException("Tier not found"));
            if (newTier.getEligibleCohort() != null && !newTier.getEligibleCohort().equalsIgnoreCase(user.getCohortName())) {
                throw new ValidationException("User does not belong to the eligible cohort for this tier");
            }
            currentSubscription.setTier(newTier);
        }

        currentSubscription = subscriptionRepository.save(currentSubscription);
        return mapToDto(currentSubscription);
    }

    @Transactional
    public void cancelSubscription(Long subscriptionId) {
        UserSubscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
            throw new ValidationException("Subscription is already cancelled");
        }

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscriptionRepository.save(subscription);
    }

    public SubscriptionResponseDto getCurrentSubscription(Long userId) {
        UserSubscription subscription = subscriptionRepository.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("No active subscription found for user"));
        return mapToDto(subscription);
    }

    public UserPerksDto getUserPerks(Long userId) {
        // Retrieve the user's active subscription if any
        UserSubscription subscription = subscriptionRepository.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE)
                .orElse(null);

        if (subscription == null) {
            return UserPerksDto.builder()
                    .tierName("NONE")
                    .hasPrioritySupport(false)
                    .hasEarlyAccess(false)
                    .allBenefits(java.util.Collections.emptyList())
                    .build();
        }

        MembershipTier tier = subscription.getTier();

        boolean hasPriority = false;
        String priorityLevel = null;
        boolean hasEarly = false;
        String earlyHours = null;
        List<String> allBenefitsList = new java.util.ArrayList<>();

        if (tier.getTierBenefits() != null) {
            for (TierBenefit tb : tier.getTierBenefits()) {
                String name = tb.getBenefit().getName();
                String val = tb.getConfigValue();

                String benefitSummary = name + (val != null ? " (" + val + ")" : "");
                allBenefitsList.add(benefitSummary);

                if ("Priority Support".equalsIgnoreCase(name)) {
                    hasPriority = true;
                    priorityLevel = val;
                } else if ("Early Access".equalsIgnoreCase(name)) {
                    hasEarly = true;
                    earlyHours = val;
                }
            }
        }

        return UserPerksDto.builder()
                .tierName(tier.getName())
                .hasPrioritySupport(hasPriority)
                .prioritySupportLevel(priorityLevel)
                .hasEarlyAccess(hasEarly)
                .earlyAccessHours(earlyHours)
                .allBenefits(allBenefitsList)
                .build();
    }


    private LocalDateTime calculateEndDate(LocalDateTime startDate, PlanDuration duration) {
        switch (duration) {
            case MONTHLY: return startDate.plusMonths(1);
            case QUARTERLY: return startDate.plusMonths(3);
            case YEARLY: return startDate.plusYears(1);
            default: throw new IllegalArgumentException("Unknown duration");
        }
    }

    private SubscriptionResponseDto mapToDto(UserSubscription subscription) {
        return SubscriptionResponseDto.builder()
                .id(subscription.getId())
                .userId(subscription.getUser().getId())
                .plan(mapPlan(subscription.getPlan()))
                .tier(mapTier(subscription.getTier()))
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .status(subscription.getStatus())
                .build();
    }

    private MembershipPlanDto mapPlan(MembershipPlan plan) {
        return MembershipPlanDto.builder()
                .id(plan.getId())
                .duration(plan.getDuration())
                .basePrice(plan.getBasePrice())
                .build();
    }

    private MembershipTierDto mapTier(MembershipTier tier) {
        List<MembershipBenefitDto> benefitDtos = tier.getTierBenefits().stream()
                .map(tb -> MembershipBenefitDto.builder()
                        .id(tb.getBenefit().getId())
                        .name(tb.getBenefit().getName())
                        .description(tb.getBenefit().getDescription())
                        .configValue(tb.getConfigValue())
                        .build())
                .collect(Collectors.toList());

        return MembershipTierDto.builder()
                .id(tier.getId())
                .name(tier.getName())
                .minOrders(tier.getMinOrders())
                .minOrderValueInMonth(tier.getMinOrderValueInMonth())
                .eligibleCohort(tier.getEligibleCohort())
                .benefits(benefitDtos)
                .build();
    }
}
