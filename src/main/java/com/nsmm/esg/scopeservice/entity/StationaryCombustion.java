package com.nsmm.esg.scopeservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Scope 1 고정연소 데이터 엔티티
 * 사업장 내 보일러, 발전기 등의 고정연소 활동 데이터
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "stationary_combustion")
public class StationaryCombustion implements Identifiable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;         // 회원 ID

    @Column(nullable = false)
    private Long companyId;        // 회사 ID

    @Column(nullable = false)
    private Integer year;          // 데이터 연도

    @Column(nullable = false)
    private Integer month;         // 데이터 월

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fuel_type_id", nullable = false)
    private FuelType fuelType;     // 연료 타입

    @Column(nullable = false, length = 100)
    private String facilityName;   // 시설명

    @Column(nullable = false, length = 50)
    private String facilityType;   // 시설 유형 (보일러, 발전기, 히터 등)

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal usage;      // 연료 사용량

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal co2Emission;    // CO2 배출량 (tCO2)

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal ch4Emission;    // CH4 배출량 (tCO2eq)

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal n2oEmission;    // N2O 배출량 (tCO2eq)

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal totalEmission;  // 총 배출량 (tCO2eq)

    @Column(length = 500)
    private String notes;          // 비고

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
