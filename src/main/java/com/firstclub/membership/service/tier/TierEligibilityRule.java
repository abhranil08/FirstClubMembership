package com.firstclub.membership.service.tier;

import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.entity.User;

import java.math.BigDecimal;

public interface TierEligibilityRule {
    boolean isEligible(User user, MembershipTier tier, int orderCount, BigDecimal totalOrderValue);
}
