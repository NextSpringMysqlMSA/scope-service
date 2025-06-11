package com.nsmm.esg.scopeservice.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * Scope 1 이동연소 요청 DTO
 * ScopeModal에서 전송되는 데이터 구조에 맞춤
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MobileCombustionRequest {
    
    private Long memberId;                 // 회원 ID 추가
    private String companyId;              // 회사/협력사 ID (UUID)
    private Integer reportingYear;         // 보고 연도
    private Integer reportingMonth;        // 보고 월
    
    private String vehicleType;            // 차량 유형
    private String transportType;          // 교통수단 유형 (ROAD, AVIATION, RAILWAY, MARINE)
    private String fuelId;                 // 연료 ID
    private String fuelName;               // 연료명
    private BigDecimal fuelUsage;          // 연료 사용량
    private String unit;                   // 단위
    private BigDecimal distance;           // 이동거리 (km)
    private String createdBy;              // 생성자
    private String notes;                  // 비고
}
