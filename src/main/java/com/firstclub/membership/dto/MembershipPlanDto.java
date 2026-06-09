package com.firstclub.membership.dto;

import com.firstclub.membership.enums.PlanDuration;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MembershipPlanDto {
    private Long id;
    private PlanDuration duration;
    private BigDecimal basePrice;
}
