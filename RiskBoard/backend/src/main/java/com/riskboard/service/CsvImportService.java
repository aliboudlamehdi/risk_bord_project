package com.riskboard.service;

import com.riskboard.dto.ImportSummary;
import com.riskboard.dto.ImportErrorDto;
import com.riskboard.entity.Counterparty;
import com.riskboard.entity.LimitType;
import com.riskboard.entity.RiskLimit;
import com.riskboard.repository.CounterpartyRepository;
import com.riskboard.repository.RiskLimitRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvImportService {

    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setTrim(true)
            .build();

    private final CounterpartyRepository counterpartyRepository;
    private final RiskLimitRepository riskLimitRepository;

    public CsvImportService(CounterpartyRepository counterpartyRepository,
                            RiskLimitRepository riskLimitRepository) {
        this.counterpartyRepository = counterpartyRepository;
        this.riskLimitRepository = riskLimitRepository;
    }

    @Transactional
    public ImportSummary importRiskLimits(InputStream inputStream) throws IOException {
        int successCount = 0;
        List<ImportErrorDto> errors = new ArrayList<>();

        try (CSVParser parser = CSV_FORMAT.parse(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            for (CSVRecord record : parser) {
                try {
                    importRow(record);
                    successCount++;
                } catch (Exception e) {
                    errors.add(new ImportErrorDto((int) record.getRecordNumber(), e.getMessage()));
                }
            }
        }

        return new ImportSummary(successCount, errors.size(), errors);
    }

    private void importRow(CSVRecord record) {
        String name = requireNonBlank(record, "name");
        String ricosCode = requireNonBlank(record, "ricosCode");
        String country = requireNonBlank(record, "country");
        String sector = requireNonBlank(record, "sector");
        LimitType limitType = parseLimitType(requireNonBlank(record, "limitType"));
        BigDecimal maxAmount = parseAmount(record, "maxAmount");
        BigDecimal usedAmount = parseAmount(record, "usedAmount");
        String currency = requireNonBlank(record, "currency");

        if (maxAmount.signum() <= 0) {
            throw new IllegalArgumentException("maxAmount must be strictly positive");
        }
        if (usedAmount.signum() < 0) {
            throw new IllegalArgumentException("usedAmount must not be negative");
        }

        Counterparty counterparty = counterpartyRepository.findByRicosCode(ricosCode)
                .orElseGet(Counterparty::new);
        counterparty.setName(name);
        counterparty.setRicosCode(ricosCode);
        counterparty.setCountry(country);
        counterparty.setSector(sector);
        counterparty = counterpartyRepository.save(counterparty);

        RiskLimit riskLimit = riskLimitRepository.findByCounterpartyAndLimitType(counterparty, limitType)
                .orElseGet(RiskLimit::new);
        riskLimit.setCounterparty(counterparty);
        riskLimit.setLimitType(limitType);
        riskLimit.setMaxAmount(maxAmount);
        riskLimit.setUsedAmount(usedAmount);
        riskLimit.setCurrency(currency);
        riskLimit.setLastUpdated(LocalDateTime.now());
        riskLimitRepository.save(riskLimit);
    }

    private String requireNonBlank(CSVRecord record, String column) {
        if (!record.isMapped(column)) {
            throw new IllegalArgumentException("missing column: " + column);
        }
        String value = record.get(column);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(column + " must not be blank");
        }
        return value.trim();
    }

    private LimitType parseLimitType(String raw) {
        try {
            return LimitType.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("invalid limitType: " + raw);
        }
    }

    private BigDecimal parseAmount(CSVRecord record, String column) {
        String raw = requireNonBlank(record, column);
        try {
            return new BigDecimal(raw);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("invalid " + column + ": " + raw);
        }
    }
}
