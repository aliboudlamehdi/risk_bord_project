package com.riskboard.service;

import com.riskboard.dto.CounterpartyDto;
import com.riskboard.entity.Counterparty;
import com.riskboard.repository.CounterpartyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CounterpartyService {

    private final CounterpartyRepository counterpartyRepository;

    public CounterpartyService(CounterpartyRepository counterpartyRepository) {
        this.counterpartyRepository = counterpartyRepository;
    }

    public List<CounterpartyDto> findAll() {
        return counterpartyRepository.findAll().stream()
                .map(this::toDto)
                .sorted(Comparator.comparing(CounterpartyDto::name))
                .toList();
    }

    private CounterpartyDto toDto(Counterparty counterparty) {
        return new CounterpartyDto(
                counterparty.getId(),
                counterparty.getName(),
                counterparty.getRicosCode(),
                counterparty.getCountry(),
                counterparty.getSector());
    }
}