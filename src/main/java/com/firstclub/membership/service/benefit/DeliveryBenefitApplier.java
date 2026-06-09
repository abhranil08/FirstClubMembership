package com.firstclub.membership.service.benefit;

import com.firstclub.membership.dto.CheckoutRequestDto;
import com.firstclub.membership.dto.CheckoutResponseDto;
import com.firstclub.membership.entity.TierBenefit;
import org.springframework.stereotype.Component;

@Component
public class DeliveryBenefitApplier implements BenefitApplier {

    @Override
    public boolean supports(String benefitName) {
        return "Free Delivery".equalsIgnoreCase(benefitName);
    }

    @Override
    public void apply(TierBenefit benefit, CheckoutRequestDto request, CheckoutResponseDto response) {
        if (benefit.getConfigValue() != null) {
            response.setShippingSpeed(benefit.getConfigValue());
            response.getAppliedBenefits().add("Free " + benefit.getConfigValue() + " Delivery");
        }
    }
}
