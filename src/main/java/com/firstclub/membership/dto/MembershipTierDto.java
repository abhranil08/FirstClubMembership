package com.firstclub.membership.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class MembershipTierDto {
    private Long id;
    private String name;
    private Integer minOrders;
    private BigDecimal minOrderValueInMonth;
    private String eligibleCohort;
    private List<MembershipBenefitDto> benefits;
}
