package com.firstclub.membership.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CheckoutResponseDto {
    private BigDecimal originalPrice;
    private BigDecimal discountedPrice;
    private String shippingSpeed; // Standard, Express, Same Day
    private List<String> appliedBenefits;
}
