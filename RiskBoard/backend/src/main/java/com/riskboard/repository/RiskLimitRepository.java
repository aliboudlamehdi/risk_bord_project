package com.riskboard.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.riskboard.entity.Counterparty;
import com.riskboard.entity.LimitType;
import com.riskboard.entity.RiskLimit;

public interface RiskLimitRepository extends JpaRepository<RiskLimit, Long> {

    Optional<RiskLimit> findByCounterpartyAndLimitType(Counterparty counterparty, LimitType limitType);

}
