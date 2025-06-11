package com.nsmm.esg.scopeservice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Scope 1 이동연소 응답 DTO
 * 프론트엔드에서 필요한 데이터 구조에 맞춤
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MobileCombustionResponse {

    private Long id;
    private Long memberId;                 // 회원 ID 추가
    private String companyId;              // 회사/협력사 ID (UUID)
    private Integer reportingYear;         // 보고 연도
    private Integer reportingMonth;        // 보고 월
    private String vehicleType;            // 차량 유형
    private String transportType;          // 교통수단 유형
    private String fuelId;                 // 연료 ID
    private String fuelName;               // 연료명
    private BigDecimal fuelUsage;          // 연료 사용량
    private String unit;                   // 단위
    private BigDecimal distance;           // 이동거리 (km)
    
    // 계산된 배출량 정보
    private BigDecimal co2Emission;        // CO2 배출량
    private BigDecimal ch4Emission;        // CH4 배출량
    private BigDecimal n2oEmission;        // N2O 배출량
    private BigDecimal totalCo2Equivalent; // 총 배출량
    private LocalDateTime calculatedAt;    // 계산 일시
    
    private String createdBy;              // 생성자
    private String notes;                  // 비고
    private LocalDateTime createdAt;       // 생성일시
    private LocalDateTime updatedAt;       // 수정일시
}