package com.riskboard.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "counterparty", uniqueConstraints = @UniqueConstraint(columnNames = "ricosCode"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Counterparty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String ricosCode;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String sector;
}
