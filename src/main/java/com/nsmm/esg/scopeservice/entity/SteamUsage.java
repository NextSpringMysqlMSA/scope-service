package com.nsmm.esg.scopeservice.entity;

import com.nsmm.esg.scopeservice.dto.SteamUsageRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Scope 2 스팀 사용 데이터 엔티티
 * 구매 스팀 사용에 따른 간접 배출 활동 데이터
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "steam_usage")
public class SteamUsage {

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

    @Column(nullable = false, length = 50)
    private String steamType;      // 스팀 타입 (ScopeModal의 steamType)

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal steamUsage; // 스팀 사용량 (ScopeModal의 steamUsage)

    @Column(nullable = false, length = 20)
    private String unit;           // 단위 (GJ)

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

    /**
     * ScopeModal 폼 데이터로 엔티티 업데이트
     */
    public void updateFromScopeModal(String companyId, Integer reportingYear, Integer reportingMonth,
                                   String facilityName, String facilityLocation, String steamType,
                                   BigDecimal steamUsage, String unit, String createdBy) {
        this.companyId = companyId;
        this.reportingYear = reportingYear;
        this.reportingMonth = reportingMonth;
        this.facilityName = facilityName;
        this.facilityLocation = facilityLocation;
        this.steamType = steamType;
        this.steamUsage = steamUsage;
        this.unit = unit;
        this.createdBy = createdBy;
    }

    /**
     * 계산된 배출량 정보 업데이트
     */
    public void updateEmissions(BigDecimal co2Emission, BigDecimal totalEmission) {
        this.co2Emission = co2Emission;
        this.totalEmission = totalEmission;
        this.calculatedAt = LocalDateTime.now();
    }
}
