package com.riskboard.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "derogation_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DerogationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "counterparty_id", nullable = false)
    private Counterparty counterparty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LimitType limitType;

    @Column(nullable = false)
    private String requestedBy;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 1000)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DerogationStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
