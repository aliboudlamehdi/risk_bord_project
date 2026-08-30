package com.riskboard.service;

import com.riskboard.dto.CreateDerogationRequestDto;
import com.riskboard.dto.DerogationRequestDto;
import com.riskboard.dto.LimitCheckDto;
import com.riskboard.entity.*;
import com.riskboard.exception.BusinessRuleException;
import com.riskboard.exception.ResourceNotFoundException;
import com.riskboard.repository.CounterpartyRepository;
import com.riskboard.repository.DerogationRequestRepository;
import com.riskboard.repository.RiskLimitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DerogationRequestService {

    private static final BigDecimal MAX_AMOUNT_MULTIPLIER = BigDecimal.valueOf(1.5);

    private final DerogationRequestRepository derogationRequestRepository;
    private final CounterpartyRepository counterpartyRepository;
    private final RiskLimitRepository riskLimitRepository;

    public DerogationRequestService(DerogationRequestRepository derogationRequestRepository,
                                    CounterpartyRepository counterpartyRepository,
                                    RiskLimitRepository riskLimitRepository) {
        this.derogationRequestRepository = derogationRequestRepository;
        this.counterpartyRepository = counterpartyRepository;
        this.riskLimitRepository = riskLimitRepository;
    }

    @Transactional(readOnly = true)
    public LimitCheckDto checkLimit(Long counterpartyId, LimitType limitType, BigDecimal amount) {
        Counterparty counterparty = counterpartyRepository.findById(counterpartyId)
                .orElseThrow(() -> new ResourceNotFoundException("Contrepartie introuvable: " + counterpartyId));

        Optional<RiskLimit> riskLimit = riskLimitRepository.findByCounterpartyAndLimitType(counterparty, limitType);
        if (riskLimit.isEmpty()) {
            return new LimitCheckDto(false, null, false);
        }

        BigDecimal maxAmount = riskLimit.get().getMaxAmount();
        boolean amountValid = amount == null || isWithinThreshold(amount, maxAmount);
        return new LimitCheckDto(true, maxAmount, amountValid);
    }

    public DerogationRequestDto create(CreateDerogationRequestDto dto) {
        Counterparty counterparty = counterpartyRepository.findById(dto.counterpartyId())
                .orElseThrow(() -> new ResourceNotFoundException("Contrepartie introuvable: " + dto.counterpartyId()));

        RiskLimit riskLimit = riskLimitRepository.findByCounterpartyAndLimitType(counterparty, dto.limitType())
                .orElseThrow(() -> new BusinessRuleException(
                        "Aucune limite " + dto.limitType() + " n'existe pour cette contrepartie"));

        if (!isWithinThreshold(dto.amount(), riskLimit.getMaxAmount())) {
            BigDecimal threshold = riskLimit.getMaxAmount().multiply(MAX_AMOUNT_MULTIPLIER);
            throw new BusinessRuleException(
                    "Le montant demandé dépasse 150% de la limite max (" + threshold + ")");
        }

        DerogationRequest derogationRequest = DerogationRequest.builder()
                .counterparty(counterparty)
                .limitType(dto.limitType())
                .amount(dto.amount())
                .reason(dto.reason())
                .requestedBy(dto.requestedBy())
                .status(DerogationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        return toDto(derogationRequestRepository.save(derogationRequest));
    }

    @Transactional(readOnly = true)
    public List<DerogationRequestDto> findAll(DerogationStatus status) {
        List<DerogationRequest> requests = status == null
                ? derogationRequestRepository.findAllWithCounterparty()
                : derogationRequestRepository.findByStatusWithCounterparty(status);
        return requests.stream().map(this::toDto).toList();
    }

    public DerogationRequestDto approve(Long id) {
        return transition(id, DerogationStatus.APPROVED);
    }

    public DerogationRequestDto reject(Long id) {
        return transition(id, DerogationStatus.REJECTED);
    }

    private DerogationRequestDto transition(Long id, DerogationStatus targetStatus) {
        DerogationRequest derogationRequest = derogationRequestRepository.findByIdWithCounterparty(id)
                .orElseThrow(() -> new ResourceNotFoundException("Demande de dérogation introuvable: " + id));

        if (derogationRequest.getStatus() != DerogationStatus.PENDING) {
            throw new BusinessRuleException("Seule une demande en attente peut être traitée");
        }

        derogationRequest.setStatus(targetStatus);
        return toDto(derogationRequestRepository.save(derogationRequest));
    }

    private boolean isWithinThreshold(BigDecimal amount, BigDecimal maxAmount) {
        return amount.compareTo(maxAmount.multiply(MAX_AMOUNT_MULTIPLIER)) <= 0;
    }

    private DerogationRequestDto toDto(DerogationRequest derogationRequest) {
        Counterparty counterparty = derogationRequest.getCounterparty();
        return new DerogationRequestDto(
                derogationRequest.getId(),
                counterparty.getId(),
                counterparty.getName(),
                derogationRequest.getLimitType(),
                derogationRequest.getAmount(),
                derogationRequest.getReason(),
                derogationRequest.getRequestedBy(),
                derogationRequest.getStatus(),
                derogationRequest.getCreatedAt());
    }
}
