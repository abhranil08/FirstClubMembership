package com.firstclub.membership.repository;

import com.firstclub.membership.entity.MembershipPlan;
import com.firstclub.membership.enums.PlanDuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {
    Optional<MembershipPlan> findByDuration(PlanDuration duration);
}
