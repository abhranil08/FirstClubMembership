package com.firstclub.membership.service;

import com.firstclub.membership.dto.MembershipBenefitDto;
import com.firstclub.membership.dto.MembershipPlanDto;
import com.firstclub.membership.dto.MembershipTierDto;
import com.firstclub.membership.entity.MembershipPlan;
import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.entity.TierBenefit;
import com.firstclub.membership.repository.MembershipPlanRepository;
import com.firstclub.membership.repository.MembershipTierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipCatalogService {

    private final MembershipPlanRepository planRepository;
    private final MembershipTierRepository tierRepository;

    public List<MembershipPlanDto> getAllPlans() {
        return planRepository.findAll().stream()
                .map(this::mapToPlanDto)
                .collect(Collectors.toList());
    }

    public List<MembershipTierDto> getAllTiers() {
        return tierRepository.findAll().stream()
                .map(this::mapToTierDto)
                .collect(Collectors.toList());
    }

    private MembershipPlanDto mapToPlanDto(MembershipPlan plan) {
        return MembershipPlanDto.builder()
                .id(plan.getId())
                .duration(plan.getDuration())
                .basePrice(plan.getBasePrice())
                .build();
    }

    private MembershipTierDto mapToTierDto(MembershipTier tier) {
        List<MembershipBenefitDto> benefitDtos = tier.getTierBenefits().stream()
                .map(this::mapToBenefitDto)
                .collect(Collectors.toList());

        return MembershipTierDto.builder()
                .id(tier.getId())
                .name(tier.getName())
                .minOrders(tier.getMinOrders())
                .minOrderValueInMonth(tier.getMinOrderValueInMonth())
                .eligibleCohort(tier.getEligibleCohort())
                .benefits(benefitDtos)
                .build();
    }

    private MembershipBenefitDto mapToBenefitDto(TierBenefit tierBenefit) {
        return MembershipBenefitDto.builder()
                .id(tierBenefit.getBenefit().getId())
                .name(tierBenefit.getBenefit().getName())
                .description(tierBenefit.getBenefit().getDescription())
                .configValue(tierBenefit.getConfigValue())
                .build();
    }
}
