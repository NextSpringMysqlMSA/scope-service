package com.nsmm.esg.scopeservice.entity;

import com.nsmm.esg.scopeservice.dto.ElectricityUsageRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Scope 2 전력 사용 데이터 엔티티
 * 구매 전력 사용에 따른 간접 배출 활동 데이터
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "electricity_usage")
public class ElectricityUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;         // 회원 ID

    @Column(nullable = false, length = 36)
    private String companyId;      // 회사/협력사 ID (UUID)

    @Column(nullable = false)
    private Integer reportingYear;     // 보고 연도 (ScopeModal의 reportingYear)

    @Column(nullable = false)
    private Integer reportingMonth;    // 보고 월 (ScopeModal의 reportingMonth)

    @Column(nullable = false, length = 100)
    private String facilityName;   // 시설명 (ScopeModal의 facilityName)

    @Column(length = 100)
    private String facilityLocation; // 시설 위치 (ScopeModal의 facilityLocation)

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal electricityUsage; // 전력 사용량 (ScopeModal의 electricityUsage)

    @Column(nullable = false, length = 20)
    private String unit;           // 단위 (kWh)

    @Column(nullable = false)
    @Builder.Default
    private Boolean isRenewable = false;  // 재생에너지 여부 (ScopeModal의 isRenewable)

    @Column(length = 100)
    private String renewableType;  // 재생에너지 타입 (ScopeModal의 renewableType)

    // 계산된 배출량 정보
    @Column(precision = 15, scale = 4)
    private BigDecimal co2Emission;     // CO2 배출량 (tCO2)

    @Column(precision = 15, scale = 4)
    private BigDecimal totalEmission;   // 총 배출량 (tCO2eq)

    private LocalDateTime calculatedAt; // 계산 일시

    @Column(length = 100)
    private String createdBy;      // 생성자 (ScopeModal의 createdBy)

    @Column(length = 500)
    private String notes;          // 비고

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public void updateFromRequest(ElectricityUsageRequest request) {
        this.memberId = request.getMemberId();
        this.companyId = request.getCompanyId();
        this.reportingYear = request.getReportingYear();
        this.reportingMonth = request.getReportingMonth();
        this.facilityName = request.getFacilityName();
        this.facilityLocation = request.getFacilityLocation();
        this.electricityUsage = request.getElectricityUsage();
        this.unit = request.getUnit();
        this.isRenewable = request.getIsRenewable();
        this.renewableType = request.getRenewableType();
        this.createdBy = request.getCreatedBy();
        this.notes = request.getNotes();
    }

    public void updateEmissions(BigDecimal co2Emission) {
    this.co2Emission = co2Emission;
    this.totalEmission = co2Emission; // 전력은 CO2와 동일
    this.calculatedAt = LocalDateTime.now();
}
}
