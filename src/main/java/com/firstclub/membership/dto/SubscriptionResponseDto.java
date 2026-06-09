package com.firstclub.membership.dto;

import com.firstclub.membership.enums.SubscriptionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SubscriptionResponseDto {
    private Long id;
    private Long userId;
    private MembershipPlanDto plan;
    private MembershipTierDto tier;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private SubscriptionStatus status;
}
