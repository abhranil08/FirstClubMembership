package com.firstclub.membership.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderRequestDto {
    private Long userId;
    private BigDecimal amount;
}
