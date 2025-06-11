package com.nsmm.esg.scopeservice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Scope 배출량 요약 응답 DTO
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScopeEmissionSummaryResponse {

    // === 기본 식별 정보 ===
    private Long memberId;              // 회원 ID 추가
    private String companyId;           // 회사 ID 추가
    private Integer year;
    private Integer month;              // null이면 연간 집계
    private String aggregationType;     // "MONTHLY", "QUARTERLY", "YEARLY"
    
    // === 집계 결과 ===
    private Scope1Summary scope1;
    private Scope2Summary scope2;
    private BigDecimal totalEmission;
    
    // === 메타데이터 ===
    private LocalDateTime calculatedAt;  // 계산 일시 추가
    private Integer totalDataCount;     // 집계된 데이터 건수 추가
    private String unit;               // 배출량 단위 (tCO2eq)

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Scope1Summary {
        private BigDecimal stationaryEmission;
        private BigDecimal mobileEmission;
        private BigDecimal totalScope1Emission;
        private List<FuelTypeEmission> fuelTypeBreakdown;
        
        // === 상세 정보 추가 ===
        private Integer stationaryDataCount;  // 고정연소 데이터 건수
        private Integer mobileDataCount;      // 이동연소 데이터 건수
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Scope2Summary {
        private BigDecimal electricityEmission;
        private BigDecimal steamEmission;
        private BigDecimal totalScope2Emission;
        
        // === 상세 정보 추가 ===
        private Integer electricityDataCount;  // 전력 데이터 건수
        private Integer steamDataCount;        // 스팀 데이터 건수
        private BigDecimal electricityUsage;   // 총 전력 사용량
        private BigDecimal steamUsage;         // 총 스팀 사용량
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FuelTypeEmission {
        private String fuelId;             // 연료 ID 추가
        private String fuelType;
        private String category;
        private BigDecimal emission;
        private BigDecimal percentage;
        private BigDecimal usage;          // 연료 사용량 추가
        private String unit;              // 사용량 단위 추가
    }
}