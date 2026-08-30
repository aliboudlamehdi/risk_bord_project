package com.riskboard.dto;

import com.riskboard.entity.DerogationStatus;
import com.riskboard.entity.LimitType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DerogationRequestDto(
        Long id,
        Long counterpartyId,
        String counterpartyName,
        LimitType limitType,
        BigDecimal amount,
        String reason,
        String requestedBy,
        DerogationStatus status,
        LocalDateTime createdAt) {
}