package com.nsmm.esg.scopeservice.entity;

import com.nsmm.esg.scopeservice.dto.StationaryCombustionRequest;
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
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "stationary_combustion")
public class StationaryCombustion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;         // 회원 ID

    @Column(nullable = false, length = 36)
    private String companyId;      // 회사/협력사 ID (UUID)

    @Column(nullable = false)
    private Integer reportingYear;     // 보고 연도

    @Column(nullable = false)
    private Integer reportingMonth;    // 보고 월

    @Column(nullable = false, length = 100)
    private String facilityName;   // 시설명

    @Column(length = 100)
    private String facilityLocation; // 시설 위치

    @Column(nullable = false, length = 50)
    private String combustionType; // 연소 타입 (LIQUID, SOLID, GAS)

    @Column(nullable = false, length = 50)
    private String fuelId;         // 연료 ID

    @Column(length = 100)
    private String fuelName;       // 연료명

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal fuelUsage;  // 연료 사용량

    @Column(nullable = false, length = 20)
    private String unit;           // 단위 (L, kg, m³ 등)

    // 계산된 배출량 정보
    @Column(precision = 15, scale = 4)
    private BigDecimal co2Emission;    // CO2 배출량 (tCO2)

    @Column(precision = 15, scale = 4)
    private BigDecimal ch4Emission;    // CH4 배출량 (tCO2eq)

    @Column(precision = 15, scale = 4)
    private BigDecimal n2oEmission;    // N2O 배출량 (tCO2eq)

    @Column(precision = 15, scale = 4)
    private BigDecimal totalCo2Equivalent; // 총 배출량 (tCO2eq)

    private LocalDateTime calculatedAt;    // 계산 일시

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 수정: StationaryCombustionRequest로 엔티티 업데이트
     */
    public void updateFromRequest(StationaryCombustionRequest request) {
        this.memberId = request.getMemberId();
        this.companyId = request.getCompanyId();
        this.reportingYear = request.getReportingYear();
        this.reportingMonth = request.getReportingMonth();
        this.facilityName = request.getFacilityName();
        this.facilityLocation = request.getFacilityLocation();
        this.combustionType = request.getCombustionType();
        this.fuelId = request.getFuelId();
        this.fuelName = request.getFuelName();
        this.fuelUsage = request.getFuelUsage();
        this.unit = request.getUnit();
    }

    /**
     * 계산된 배출량 정보 업데이트
     */
    public void updateEmissions(BigDecimal co2Emission, BigDecimal ch4Emission, 
                               BigDecimal n2oEmission, BigDecimal totalCo2Equivalent) {
        this.co2Emission = co2Emission;
        this.ch4Emission = ch4Emission;
        this.n2oEmission = n2oEmission;
        this.totalCo2Equivalent = totalCo2Equivalent;
        this.calculatedAt = LocalDateTime.now();
    }
}