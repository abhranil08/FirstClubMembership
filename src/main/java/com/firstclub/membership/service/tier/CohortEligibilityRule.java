package com.firstclub.membership.service.tier;

import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CohortEligibilityRule implements TierEligibilityRule {

    @Override
    public boolean isEligible(User user, MembershipTier tier, int orderCount, BigDecimal totalOrderValue) {
        if (tier.getEligibleCohort() == null) {
            return true;
        }
        return tier.getEligibleCohort().equalsIgnoreCase(user.getCohortName());
    }
}
