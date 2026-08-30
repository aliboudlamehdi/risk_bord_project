package com.riskboard.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.riskboard.entity.Counterparty;

public interface CounterpartyRepository extends JpaRepository<Counterparty, Long> {

    Optional<Counterparty> findByRicosCode(String ricosCode);
}
