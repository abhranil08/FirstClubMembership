package com.firstclub.membership.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CheckoutRequestDto {
    private Long userId;
    private BigDecimal originalPrice;
}
