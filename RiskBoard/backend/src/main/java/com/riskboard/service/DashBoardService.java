package com.riskboard.service;

import com.riskboard.dto.RiskLimitDto;
import com.riskboard.dto.SectorExposureDto;
import com.riskboard.entity.AlertLevel;
import com.riskboard.entity.Counterparty;
import com.riskboard.entity.LimitType;
import com.riskboard.entity.RiskLimit;
import com.riskboard.repository.RiskLimitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DashBoardService {

    private final RiskLimitRepository riskLimitRepository;
    private final RiskCalculationService riskCalculationService;

    public DashBoardService(RiskLimitRepository riskLimitRepository,
                                 RiskCalculationService riskCalculationService) {
        this.riskLimitRepository = riskLimitRepository;
        this.riskCalculationService = riskCalculationService;
    }

    public List<RiskLimitDto> findAll() {
        return riskLimitRepository.findAllWithCounterparty().stream()
                .map(this::toDto)
                .toList();
    }

    public List<SectorExposureDto> getSectorExposure(LimitType limitType) {
        List<RiskLimit> riskLimits = riskLimitRepository.findByLimitTypeWithCounterparty(limitType);
        Map<String, BigDecimal> exposureBySector = riskCalculationService.aggregatedExposureBySector(riskLimits);

        return exposureBySector.entrySet().stream()
                .map(entry -> new SectorExposureDto(limitType, entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(SectorExposureDto::sector))
                .toList();
    }

    private RiskLimitDto toDto(RiskLimit riskLimit) {
        BigDecimal usageRate = riskCalculationService.usageRate(riskLimit);
        AlertLevel alertLevel = riskCalculationService.alertLevel(usageRate);
        Counterparty counterparty = riskLimit.getCounterparty();

        return new RiskLimitDto(
                riskLimit.getId(),
                counterparty.getId(),
                counterparty.getName(),
                counterparty.getSector(),
                counterparty.getCountry(),
                riskLimit.getLimitType(),
                riskLimit.getMaxAmount(),
                riskLimit.getUsedAmount(),
                riskLimit.getCurrency(),
                usageRate,
                alertLevel,
                riskLimit.getLastUpdated());
    }
}