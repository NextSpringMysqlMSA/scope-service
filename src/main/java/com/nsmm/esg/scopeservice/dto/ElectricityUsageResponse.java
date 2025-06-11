package com.nsmm.esg.scopeservice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Scope 2 전력 사용 응답 DTO
 * 프론트엔드에서 필요한 데이터 구조에 맞춤
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElectricityUsageResponse {

    private Long id;
    private String companyId;              // 회사/협력사 ID (UUID)
    private Integer reportingYear;         // 보고 연도
    private Integer reportingMonth;        // 보고 월
    private String facilityName;           // 시설명
    private String facilityLocation;       // 시설 위치
    private BigDecimal electricityUsage;   // 전력 사용량
    private String unit;                   // 단위 (kWh)
    private Boolean isRenewable;           // 재생에너지 여부
    private String renewableType;          // 재생에너지 타입
    
    // 계산된 배출량 정보
    private BigDecimal co2Emission;        // CO2 배출량
    private BigDecimal totalEmission;      // 총 배출량
    private LocalDateTime calculatedAt;    // 계산 일시
    
    private String createdBy;              // 생성자
    private String notes;                  // 비고
    private LocalDateTime createdAt;       // 생성일시
    private LocalDateTime updatedAt;       // 수정일시
}
