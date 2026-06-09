package com.firstclub.membership.repository;

import com.firstclub.membership.entity.UserSubscription;
import com.firstclub.membership.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    Optional<UserSubscription> findByUserIdAndStatus(Long userId, SubscriptionStatus status);
    List<UserSubscription> findByUserId(Long userId);
}
