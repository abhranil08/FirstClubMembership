package com.firstclub.membership.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class UserPerksDto {
    private String tierName;
    private boolean hasPrioritySupport;
    private String prioritySupportLevel;
    private boolean hasEarlyAccess;
    private String earlyAccessHours;
    private List<String> allBenefits;
}
