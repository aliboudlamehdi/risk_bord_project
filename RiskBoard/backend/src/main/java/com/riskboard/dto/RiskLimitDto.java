package com.riskboard.dto;

import com.riskboard.entity.AlertLevel;
import com.riskboard.entity.LimitType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RiskLimitDto(
        Long id,
        Long counterpartyId,
        String counterpartyName,
        String sector,
        String country,
        LimitType limitType,
        BigDecimal maxAmount,
        BigDecimal usedAmount,
        String currency,
        BigDecimal usageRate,
        AlertLevel alertLevel,
        LocalDateTime lastUpdated) {
}