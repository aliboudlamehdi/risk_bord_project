package com.riskboard.service;

import com.riskboard.entity.AlertLevel;
import com.riskboard.entity.RiskLimit;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RiskCalculationService {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal ORANGE_THRESHOLD = BigDecimal.valueOf(70);
    private static final BigDecimal RED_THRESHOLD = BigDecimal.valueOf(90);

    public BigDecimal usageRate(RiskLimit riskLimit) {
        return usageRate(riskLimit.getUsedAmount(), riskLimit.getMaxAmount());
    }

    public BigDecimal usageRate(BigDecimal usedAmount, BigDecimal maxAmount) {
        return usedAmount
                .divide(maxAmount, 6, RoundingMode.HALF_UP)
                .multiply(HUNDRED)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public AlertLevel alertLevel(BigDecimal usageRate) {
        if (usageRate.compareTo(ORANGE_THRESHOLD) < 0) {
            return AlertLevel.GREEN;
        }
        if (usageRate.compareTo(RED_THRESHOLD) <= 0) {
            return AlertLevel.ORANGE;
        }
        return AlertLevel.RED;
    }

    public Map<String, BigDecimal> aggregatedExposureBySector(List<RiskLimit> riskLimits) {
        return riskLimits.stream()
                .collect(Collectors.groupingBy(
                        riskLimit -> riskLimit.getCounterparty().getSector(),
                        Collectors.reducing(BigDecimal.ZERO, RiskLimit::getUsedAmount, BigDecimal::add)));
    }
}
