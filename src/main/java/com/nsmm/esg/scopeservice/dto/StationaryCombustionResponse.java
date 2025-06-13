package com.nsmm.esg.scopeservice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Scope 1 고정연소 응답 DTO
 * 프론트엔드에서 필요한 데이터 구조에 맞춤
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StationaryCombustionResponse {

    private Long id;
    private Long memberId;                 // 회원 ID 추가
    private String companyId;              // 회사/협력사 ID (UUID)
    private Integer reportingYear;         // 보고 연도
    private Integer reportingMonth;        // 보고 월
    private String facilityName;           // 시설명
    private String facilityLocation;       // 시설 위치
    private String combustionType;         // 연소 타입
    private String fuelId;                 // 연료 ID
    private String fuelName;               // 연료명
    private BigDecimal fuelUsage;          // 연료 사용량
    private String unit;                   // 단위
    
    // 계산된 배출량 정보
    private BigDecimal co2Emission;        // CO2 배출량
    private BigDecimal ch4Emission;        // CH4 배출량
    private BigDecimal n2oEmission;        // N2O 배출량
    private BigDecimal totalCo2Equivalent; // 총 배출량
    private LocalDateTime calculatedAt;    // 계산 일시
    
    private LocalDateTime createdAt;       // 생성일시
    private LocalDateTime updatedAt;       // 수정일시
}
