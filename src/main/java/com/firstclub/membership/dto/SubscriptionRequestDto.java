package com.firstclub.membership.dto;

import lombok.Data;

@Data
public class SubscriptionRequestDto {
    private Long userId;
    private Long planId;
    private Long tierId;
}
