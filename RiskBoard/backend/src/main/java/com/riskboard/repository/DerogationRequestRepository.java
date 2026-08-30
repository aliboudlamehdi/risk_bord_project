package com.riskboard.repository;

import com.riskboard.entity.DerogationRequest;
import com.riskboard.entity.DerogationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DerogationRequestRepository extends JpaRepository<DerogationRequest, Long> {

    List<DerogationRequest> findByStatus(DerogationStatus status);

    @Query("select d from DerogationRequest d join fetch d.counterparty where d.id = :id")
    Optional<DerogationRequest> findByIdWithCounterparty(@Param("id") Long id);

    @Query("select d from DerogationRequest d join fetch d.counterparty")
    List<DerogationRequest> findAllWithCounterparty();

    @Query("select d from DerogationRequest d join fetch d.counterparty where d.status = :status")
    List<DerogationRequest> findByStatusWithCounterparty(@Param("status") DerogationStatus status);
}