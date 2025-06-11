package com.nsmm.esg.scopeservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 배출계수 마스터 엔티티
 * 각 연료별 온실가스 배출계수 정보를 관리
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "emission_factor")
public class EmissionFactor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fuel_type_id", nullable = false)
    private FuelType fuelType;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal co2Factor;    // CO2 배출계수 (tCO2/TJ)

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal ch4Factor;    // CH4 배출계수 (kgCH4/TJ)

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal n2oFactor;    // N2O 배출계수 (kgN2O/TJ) - 추가 필요

    @Column(nullable = false)
    private Integer year;            // 적용 연도 - 추가 필요

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true; // 활성화 여부 - 추가 필요


}