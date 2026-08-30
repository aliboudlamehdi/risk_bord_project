package com.riskboard.dto;

import java.util.List;

public record ImportSummary(int successCount, int errorCount, List<ImportErrorDto> errors) {
}