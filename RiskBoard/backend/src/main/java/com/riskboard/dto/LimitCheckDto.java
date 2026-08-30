package com.riskboard.dto;

import java.math.BigDecimal;

public record LimitCheckDto(boolean limitExists, BigDecimal maxAmount, boolean amountValid) {
}