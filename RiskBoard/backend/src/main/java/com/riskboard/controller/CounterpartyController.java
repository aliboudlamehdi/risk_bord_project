package com.riskboard.controller;

import com.riskboard.dto.CounterpartyDto;
import com.riskboard.service.CounterpartyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/counterparties")
public class CounterpartyController {

    private final CounterpartyService counterpartyService;

    public CounterpartyController(CounterpartyService counterpartyService) {
        this.counterpartyService = counterpartyService;
    }

    @GetMapping
    public List<CounterpartyDto> findAll() {
        return counterpartyService.findAll();
    }
}