package com.firstclub.membership.service;

import com.firstclub.membership.dto.CheckoutRequestDto;
import com.firstclub.membership.dto.CheckoutResponseDto;
import com.firstclub.membership.entity.UserSubscription;
import com.firstclub.membership.enums.SubscriptionStatus;
import com.firstclub.membership.repository.UserSubscriptionRepository;
import com.firstclub.membership.service.benefit.BenefitApplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private final UserSubscriptionRepository subscriptionRepository;
    private final List<BenefitApplier> benefitAppliers;

    @Transactional(readOnly = true)
    public CheckoutResponseDto applyMembershipBenefits(CheckoutRequestDto request) {
        CheckoutResponseDto response = CheckoutResponseDto.builder()
                .originalPrice(request.getOriginalPrice())
                .discountedPrice(request.getOriginalPrice())
                .shippingSpeed("Standard") // default shipping speed
                .appliedBenefits(new ArrayList<>())
                .build();

        Optional<UserSubscription> activeSub = subscriptionRepository.findByUserIdAndStatus(request.getUserId(), SubscriptionStatus.ACTIVE);
        if (activeSub.isPresent()) {
            activeSub.get().getTier().getTierBenefits().forEach(tierBenefit -> {
                benefitAppliers.stream()
                        .filter(applier -> applier.supports(tierBenefit.getBenefit().getName()))
                        .forEach(applier -> applier.apply(tierBenefit, request, response));
            });
        }

        return response;
    }
}
