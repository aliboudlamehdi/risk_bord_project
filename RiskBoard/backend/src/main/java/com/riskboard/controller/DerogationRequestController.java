package com.riskboard.controller;

import com.riskboard.dto.CreateDerogationRequestDto;
import com.riskboard.dto.DerogationRequestDto;
import com.riskboard.dto.LimitCheckDto;
import com.riskboard.entity.DerogationStatus;
import com.riskboard.entity.LimitType;
import com.riskboard.service.DerogationRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/derogation-requests")
public class DerogationRequestController {

    private final DerogationRequestService derogationRequestService;

    public DerogationRequestController(DerogationRequestService derogationRequestService) {
        this.derogationRequestService = derogationRequestService;
    }

    @GetMapping
    public List<DerogationRequestDto> findAll(@RequestParam(required = false) DerogationStatus status) {
        return derogationRequestService.findAll(status);
    }

    @GetMapping("/limit-check")
    public LimitCheckDto checkLimit(@RequestParam Long counterpartyId,
                                     @RequestParam LimitType limitType,
                                     @RequestParam(required = false) BigDecimal amount) {
        return derogationRequestService.checkLimit(counterpartyId, limitType, amount);
    }

    @PostMapping
    public ResponseEntity<DerogationRequestDto> create(@Valid @RequestBody CreateDerogationRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(derogationRequestService.create(dto));
    }

    @PatchMapping("/{id}/approve")
    public DerogationRequestDto approve(@PathVariable Long id) {
        return derogationRequestService.approve(id);
    }

    @PatchMapping("/{id}/reject")
    public DerogationRequestDto reject(@PathVariable Long id) {
        return derogationRequestService.reject(id);
    }
}