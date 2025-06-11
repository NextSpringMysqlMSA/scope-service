package com.nsmm.esg.scopeservice.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * Scope 1 고정연소 요청 DTO
 * ScopeModal에서 전송되는 데이터 구조에 맞춤
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StationaryCombustionRequest {

    private String companyId;              // 회사/협력사 ID (UUID)
    private Integer reportingYear;         // 보고 연도
    private Integer reportingMonth;        // 보고 월
    private String facilityName;           // 시설명
    private String facilityLocation;       // 시설 위치
    private String combustionType;         // 연소 타입 (LIQUID, SOLID, GAS)
    private String fuelId;                 // 연료 ID
    private String fuelName;               // 연료명
    private BigDecimal fuelUsage;          // 연료 사용량
    private String unit;                   // 단위
    private String createdBy;              // 생성자
    private String notes;                  // 비고
}
