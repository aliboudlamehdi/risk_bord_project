package com.riskboard.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.riskboard.entity.Counterparty;
import com.riskboard.entity.LimitType;
import com.riskboard.entity.RiskLimit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RiskLimitRepository extends JpaRepository<RiskLimit, Long> {

    Optional<RiskLimit> findByCounterpartyAndLimitType(Counterparty counterparty, LimitType limitType);

    @Query("select rl from RiskLimit rl join fetch rl.counterparty")
    List<RiskLimit> findAllWithCounterparty();

    @Query("select rl from RiskLimit rl join fetch rl.counterparty where rl.limitType = :limitType")
    List<RiskLimit> findByLimitTypeWithCounterparty(@Param("limitType") LimitType limitType);
}
