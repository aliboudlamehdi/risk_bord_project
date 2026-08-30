package com.riskboard.service;

import com.riskboard.entity.Counterparty;
import com.riskboard.entity.LimitType;
import com.riskboard.entity.RiskLimit;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RiskCalculationServiceTest {

    private final RiskCalculationService service = new RiskCalculationService();

    private RiskLimit riskLimit(Counterparty counterparty, BigDecimal maxAmount, BigDecimal usedAmount) {
        return RiskLimit.builder()
                .counterparty(counterparty)
                .limitType(LimitType.CREDIT)
                .maxAmount(maxAmount)
                .usedAmount(usedAmount)
                .currency("EUR")
                .lastUpdated(LocalDateTime.now())
                .build();
    }

    private Counterparty counterparty(String sector) {
        return Counterparty.builder()
                .name("Test")
                .ricosCode("RICOS0000" + sector.hashCode())
                .country("FR")
                .sector(sector)
                .build();
    }

    @Test
    void usageRateBelow70PercentIsGreen() {
        RiskLimit limit = riskLimit(counterparty("Banking"),
                new BigDecimal("50000000"), new BigDecimal("32000000"));

        BigDecimal usageRate = service.usageRate(limit);

        assertThat(usageRate).isEqualByComparingTo("64.00");
        assertThat(service.alertLevel(usageRate)).isEqualTo(AlertLevel.GREEN);
    }

    @Test
    void usageRateJustBelow70PercentIsStillGreen() {
        BigDecimal usageRate = new BigDecimal("69.99");

        assertThat(service.alertLevel(usageRate)).isEqualTo(AlertLevel.GREEN);
    }

    @Test
    void usageRateAt70PercentIsOrange() {
        RiskLimit limit = riskLimit(counterparty("Banking"),
                new BigDecimal("10000000"), new BigDecimal("7000000"));

        BigDecimal usageRate = service.usageRate(limit);

        assertThat(usageRate).isEqualByComparingTo("70.00");
        assertThat(service.alertLevel(usageRate)).isEqualTo(AlertLevel.ORANGE);
    }

    @Test
    void usageRateBetween70And90PercentIsOrange() {
        RiskLimit limit = riskLimit(counterparty("Banking"),
                new BigDecimal("20000000"), new BigDecimal("16000000"));

        BigDecimal usageRate = service.usageRate(limit);

        assertThat(usageRate).isEqualByComparingTo("80.00");
        assertThat(service.alertLevel(usageRate)).isEqualTo(AlertLevel.ORANGE);
    }

    @Test
    void usageRateAt90PercentIsStillOrange() {
        BigDecimal usageRate = new BigDecimal("90.00");

        assertThat(service.alertLevel(usageRate)).isEqualTo(AlertLevel.ORANGE);
    }

    @Test
    void usageRateAbove90PercentIsRed() {
        RiskLimit limit = riskLimit(counterparty("Banking"),
                new BigDecimal("20000000"), new BigDecimal("18500000"));

        BigDecimal usageRate = service.usageRate(limit);

        assertThat(usageRate).isEqualByComparingTo("92.50");
        assertThat(service.alertLevel(usageRate)).isEqualTo(AlertLevel.RED);
    }

    @Test
    void aggregatesExposureBySector() {
        Counterparty banking1 = counterparty("Banking");
        Counterparty banking2 = counterparty("Banking");
        Counterparty energy = counterparty("Energy");

        List<RiskLimit> riskLimits = List.of(
                riskLimit(banking1, new BigDecimal("50000000"), new BigDecimal("32000000")),
                riskLimit(banking2, new BigDecimal("30000000"), new BigDecimal("29500000")),
                riskLimit(energy, new BigDecimal("25000000"), new BigDecimal("15000000")));

        var exposure = service.aggregatedExposureBySector(riskLimits);

        assertThat(exposure)
                .hasSize(2)
                .containsEntry("Banking", new BigDecimal("61500000"))
                .containsEntry("Energy", new BigDecimal("15000000"));
    }

    @Test
    void aggregatedExposureBySectorIsEmptyForNoRiskLimits() {
        assertThat(service.aggregatedExposureBySector(List.of())).isEmpty();
    }
}
