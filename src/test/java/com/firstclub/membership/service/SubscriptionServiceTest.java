package com.firstclub.membership.service;

import com.firstclub.membership.dto.SubscriptionRequestDto;
import com.firstclub.membership.dto.SubscriptionResponseDto;
import com.firstclub.membership.dto.UserPerksDto;
import com.firstclub.membership.entity.MembershipBenefit;
import com.firstclub.membership.entity.MembershipPlan;
import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.entity.TierBenefit;
import com.firstclub.membership.entity.User;
import com.firstclub.membership.entity.UserSubscription;
import com.firstclub.membership.enums.PlanDuration;
import com.firstclub.membership.enums.SubscriptionStatus;
import com.firstclub.membership.exception.ResourceNotFoundException;
import com.firstclub.membership.exception.ValidationException;
import com.firstclub.membership.repository.MembershipPlanRepository;
import com.firstclub.membership.repository.MembershipTierRepository;
import com.firstclub.membership.repository.UserRepository;
import com.firstclub.membership.repository.UserSubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private UserSubscriptionRepository subscriptionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MembershipPlanRepository planRepository;
    @Mock
    private MembershipTierRepository tierRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private User user;
    private MembershipPlan plan;
    private MembershipTier tier;
    private UserSubscription activeSubscription;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).name("Test User").email("test@test.com").build();
        plan = MembershipPlan.builder().id(1L).duration(PlanDuration.MONTHLY).basePrice(new BigDecimal("9.99")).build();
        tier = MembershipTier.builder().id(1L).name("SILVER").build();
        activeSubscription = UserSubscription.builder()
                .id(1L)
                .user(user)
                .plan(plan)
                .tier(tier)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .status(SubscriptionStatus.ACTIVE)
                .build();
    }

    @Test
    void subscribe_Success() {
        SubscriptionRequestDto request = new SubscriptionRequestDto();
        request.setUserId(1L);
        request.setPlanId(1L);
        request.setTierId(1L);

        when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(user));
        when(subscriptionRepository.findByUserIdAndStatus(1L, SubscriptionStatus.ACTIVE)).thenReturn(Optional.empty());
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(tierRepository.findById(1L)).thenReturn(Optional.of(tier));
        when(subscriptionRepository.save(any(UserSubscription.class))).thenAnswer(i -> {
            UserSubscription sub = i.getArgument(0);
            sub.setId(100L);
            return sub;
        });

        SubscriptionResponseDto response = subscriptionService.subscribe(request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(SubscriptionStatus.ACTIVE, response.getStatus());
        verify(subscriptionRepository, times(1)).save(any(UserSubscription.class));
    }

    @Test
    void subscribe_ThrowsException_IfAlreadyActive() {
        SubscriptionRequestDto request = new SubscriptionRequestDto();
        request.setUserId(1L);

        when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(user));
        when(subscriptionRepository.findByUserIdAndStatus(1L, SubscriptionStatus.ACTIVE)).thenReturn(Optional.of(activeSubscription));

        assertThrows(ValidationException.class, () -> subscriptionService.subscribe(request));
    }

    @Test
    void subscribe_ThrowsException_IfCohortNotEligible() {
        SubscriptionRequestDto request = new SubscriptionRequestDto();
        request.setUserId(1L);
        request.setPlanId(1L);
        request.setTierId(1L);

        user.setCohortName("Standard");
        tier.setEligibleCohort("EarlyAdopter");

        when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(user));
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(tierRepository.findById(1L)).thenReturn(Optional.of(tier));

        ValidationException ex = assertThrows(ValidationException.class, () -> subscriptionService.subscribe(request));
        assertEquals("User does not belong to the eligible cohort for this tier", ex.getMessage());
    }

    @Test
    void upgradeOrDowngrade_Success_ChangePlan() {
        MembershipPlan newPlan = MembershipPlan.builder().id(2L).duration(PlanDuration.YEARLY).basePrice(new BigDecimal("89.99")).build();

        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(activeSubscription));
        when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(user));
        when(planRepository.findById(2L)).thenReturn(Optional.of(newPlan));
        when(subscriptionRepository.save(any(UserSubscription.class))).thenReturn(activeSubscription);

        SubscriptionResponseDto response = subscriptionService.upgradeOrDowngrade(1L, 2L, null);

        assertNotNull(response);
        assertEquals(PlanDuration.YEARLY, response.getPlan().getDuration());
        verify(subscriptionRepository, times(1)).save(any(UserSubscription.class));
    }

    @Test
    void upgradeOrDowngrade_ThrowsException_IfCohortNotEligible() {
        MembershipTier newTier = MembershipTier.builder().id(2L).name("PLATINUM").eligibleCohort("EarlyAdopter").build();
        user.setCohortName("Standard");

        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(activeSubscription));
        when(userRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(user));
        when(tierRepository.findById(2L)).thenReturn(Optional.of(newTier));

        ValidationException ex = assertThrows(ValidationException.class, () -> subscriptionService.upgradeOrDowngrade(1L, null, 2L));
        assertEquals("User does not belong to the eligible cohort for this tier", ex.getMessage());
    }

    @Test
    void cancelSubscription_Success() {
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(activeSubscription));
        when(subscriptionRepository.save(any(UserSubscription.class))).thenReturn(activeSubscription);

        subscriptionService.cancelSubscription(1L);

        assertEquals(SubscriptionStatus.CANCELLED, activeSubscription.getStatus());
        verify(subscriptionRepository, times(1)).save(activeSubscription);
    }

    @Test
    void cancelSubscription_ThrowsException_IfAlreadyCancelled() {
        activeSubscription.setStatus(SubscriptionStatus.CANCELLED);
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(activeSubscription));

        assertThrows(ValidationException.class, () -> subscriptionService.cancelSubscription(1L));
    }

    @Test
    void getUserPerks_Success_ActiveSubscription() {
        MembershipBenefit benefit1 = MembershipBenefit.builder().id(10L).name("Priority Support").description("Priority support").build();
        TierBenefit tb1 = TierBenefit.builder().benefit(benefit1).configValue("24/7").build();

        MembershipBenefit benefit2 = MembershipBenefit.builder().id(11L).name("Early Access").description("Early sales access").build();
        TierBenefit tb2 = TierBenefit.builder().benefit(benefit2).configValue("24 Hours").build();

        tier.setTierBenefits(java.util.Set.of(tb1, tb2));

        when(subscriptionRepository.findByUserIdAndStatus(1L, SubscriptionStatus.ACTIVE)).thenReturn(Optional.of(activeSubscription));

        UserPerksDto response = subscriptionService.getUserPerks(1L);

        assertNotNull(response);
        assertEquals("SILVER", response.getTierName());
        assertTrue(response.isHasPrioritySupport());
        assertEquals("24/7", response.getPrioritySupportLevel());
        assertTrue(response.isHasEarlyAccess());
        assertEquals("24 Hours", response.getEarlyAccessHours());
        assertEquals(2, response.getAllBenefits().size());
        assertTrue(response.getAllBenefits().contains("Priority Support (24/7)"));
    }

    @Test
    void getUserPerks_ReturnsDefaultNONE_NoActiveSubscription() {
        when(subscriptionRepository.findByUserIdAndStatus(1L, SubscriptionStatus.ACTIVE)).thenReturn(Optional.empty());

        UserPerksDto response = subscriptionService.getUserPerks(1L);

        assertNotNull(response);
        assertEquals("NONE", response.getTierName());
        assertFalse(response.isHasPrioritySupport());
        assertFalse(response.isHasEarlyAccess());
        assertTrue(response.getAllBenefits().isEmpty());
    }

    @Test
    void getActiveSubscription_ExpiresSubscription_IfEndDateInPast() {
        activeSubscription.setEndDate(LocalDateTime.now().minusMinutes(5));
        when(subscriptionRepository.findByUserIdAndStatus(1L, SubscriptionStatus.ACTIVE)).thenReturn(Optional.of(activeSubscription));
        when(subscriptionRepository.save(any(UserSubscription.class))).thenReturn(activeSubscription);

        Optional<UserSubscription> response = subscriptionService.getActiveSubscription(1L);

        assertTrue(response.isEmpty());
        assertEquals(SubscriptionStatus.EXPIRED, activeSubscription.getStatus());
        verify(subscriptionRepository, times(1)).save(activeSubscription);
    }
}


