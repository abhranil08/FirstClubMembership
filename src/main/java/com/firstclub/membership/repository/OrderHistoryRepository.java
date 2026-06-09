package com.firstclub.membership.repository;

import com.firstclub.membership.entity.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {
    List<OrderHistory> findByUserIdAndOrderDateBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
}
