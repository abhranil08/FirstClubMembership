package com.firstclub.membership.service.benefit;

import com.firstclub.membership.dto.CheckoutRequestDto;
import com.firstclub.membership.dto.CheckoutResponseDto;
import com.firstclub.membership.entity.TierBenefit;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class DiscountBenefitApplier implements BenefitApplier {

    @Override
    public boolean supports(String benefitName) {
        return "Extra Discount".equalsIgnoreCase(benefitName);
    }

    @Override
    public void apply(TierBenefit benefit, CheckoutRequestDto request, CheckoutResponseDto response) {
        if (benefit.getConfigValue() != null) {
            BigDecimal discountPercentage = new BigDecimal(benefit.getConfigValue());
            BigDecimal discountFactor = discountPercentage.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
            BigDecimal discountAmount = response.getDiscountedPrice().multiply(discountFactor);
            BigDecimal newPrice = response.getDiscountedPrice().subtract(discountAmount);
            response.setDiscountedPrice(newPrice.setScale(2, RoundingMode.HALF_UP));
            response.getAppliedBenefits().add("Extra " + discountPercentage + "% Discount");
        }
    }
}
