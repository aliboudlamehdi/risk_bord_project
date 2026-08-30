package com.riskboard.dto;

import com.riskboard.entity.LimitType;

import java.math.BigDecimal;

public record SectorExposureDto(LimitType limitType, String sector, BigDecimal totalUsed) {
}