package com.firstclub.membership.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "membership_tiers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipTier extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g. SILVER, GOLD, PLATINUM

    @Column(name = "min_orders", nullable = false)
    private Integer minOrders;

    @Column(name = "min_order_value_in_month", nullable = false)
    private BigDecimal minOrderValueInMonth;

    @Column(name = "eligible_cohort")
    private String eligibleCohort;

    @OneToMany(mappedBy = "tier", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private Set<TierBenefit> tierBenefits = new HashSet<>();
}
