package com.firstclub.membership.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tier_benefits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TierBenefit extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private MembershipTier tier;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "benefit_id", nullable = false)
    private MembershipBenefit benefit;

    @Column(name = "config_value")
    private String configValue; // e.g. "10" for 10% discount
}
