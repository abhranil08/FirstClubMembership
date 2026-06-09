package com.firstclub.membership.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MembershipBenefitDto {
    private Long id;
    private String name;
    private String description;
    private String configValue;
}
