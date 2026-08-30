package com.riskboard.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "risk_limit", uniqueConstraints =
        @UniqueConstraint(columnNames = {"counterparty_id", "limitType"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiskLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "counterparty_id", nullable = false)
    private Counterparty counterparty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LimitType limitType;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal maxAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal usedAmount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;
}
