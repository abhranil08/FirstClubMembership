package com.firstclub.membership.service.benefit;

import com.firstclub.membership.dto.CheckoutRequestDto;
import com.firstclub.membership.dto.CheckoutResponseDto;
import com.firstclub.membership.entity.TierBenefit;

public interface BenefitApplier {
    boolean supports(String benefitName);
    void apply(TierBenefit benefit, CheckoutRequestDto request, CheckoutResponseDto response);
}
