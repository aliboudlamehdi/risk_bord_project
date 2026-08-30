package com.riskboard.dto;

import com.riskboard.entity.LimitType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateDerogationRequestDto(
        @NotNull(message = "La contrepartie est obligatoire")
        Long counterpartyId,

        @NotNull(message = "Le type de risque est obligatoire")
        LimitType limitType,

        @NotNull(message = "Le montant demandé est obligatoire")
        @Positive(message = "Le montant demandé doit être strictement supérieur à 0")
        BigDecimal amount,

        @NotBlank(message = "La raison est obligatoire")
        @Size(min = 20, message = "La raison doit contenir au moins 20 caractères")
        String reason,

        @NotBlank(message = "Le champ 'demandé par' est obligatoire")
        @Size(min = 6, message = "Le champ 'demandé par' doit contenir au moins 6 caractères")
        String requestedBy) {
}