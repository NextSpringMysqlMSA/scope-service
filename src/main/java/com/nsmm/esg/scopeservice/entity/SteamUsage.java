package com.nsmm.esg.scopeservice.entity;

import com.nsmm.esg.scopeservice.dto.SteamUsageRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private Integer reportingYear;     // 보고 연도

    @Column(nullable = false)
    private Integer reportingMonth;    // 보고 월

    @Column(nullable = false, length = 100)
    private String facilityName;   // 시설명

    @Column(length = 100)
    private String facilityLocation; // 시설 위치

    // 스팀 전용 필드들
    @Column(nullable = false, length = 50)
    private String steamType;      // 스팀 타입 (고압, 중압, 저압 등)

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal steamUsage;      // 스팀 사용량

    @Column(nullable = false, length = 20)
    private String unit;           // 단위 (GJ, MJ 등)

    // 스팀 배출량 (CO2만)
    @Column(precision = 15, scale = 4)
    private BigDecimal co2Emission;     // CO2 배출량 (tCO2)

    @Column(precision = 15, scale = 4)
    private BigDecimal totalCo2Equivalent; // 총 CO2 등가량 (스팀은 CO2와 동일)

    private LocalDateTime calculatedAt; // 계산 일시

    @Column(length = 100)
    private String createdBy;      // 생성자

    @Column(length = 500)
    private String notes;          // 비고

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * SteamUsageRequest로 엔티티 업데이트 (스팀 전용)
     */
    public void updateFromRequest(SteamUsageRequest request) {
        this.memberId = request.getMemberId();
        this.companyId = request.getCompanyId();
        this.reportingYear = request.getReportingYear();
        this.reportingMonth = request.getReportingMonth();
        this.facilityName = request.getFacilityName();
        this.facilityLocation = request.getFacilityLocation();
        this.steamType = request.getSteamType();
        this.steamUsage = request.getSteamUsage();
        this.unit = request.getUnit();
        this.createdBy = request.getCreatedBy();  // 누락된 필드 추가
        this.notes = request.getNotes();
    }

    /**
     * 계산된 배출량 정보 업데이트 (스팀은 CO2만)
     */
    public void updateEmissions(BigDecimal co2Emission) {
        this.co2Emission = co2Emission;
        this.totalCo2Equivalent = co2Emission; // 스팀은 CO2와 동일
        this.calculatedAt = LocalDateTime.now();
    }
}