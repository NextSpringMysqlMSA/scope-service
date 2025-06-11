package com.nsmm.esg.scopeservice.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * Scope 2 전력 사용 요청 DTO
 * ScopeModal에서 전송되는 데이터 구조에 맞춤
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElectricityUsageRequest {

    private Long memberId;                 // 회원 ID 추가
    private String companyId;              // 회사/협력사 ID (UUID)
    private Integer reportingYear;         // 보고 연도
    private Integer reportingMonth;        // 보고 월
    private String facilityName;           // 시설명
    private String facilityLocation;       // 시설 위치
    private BigDecimal electricityUsage;   // 전력 사용량
    private String unit;                   // 단위 (kWh)
    private Boolean isRenewable;           // 재생에너지 여부
    private String renewableType;          // 재생에너지 타입
    private String createdBy;              // 생성자
    private String notes;                  // 비고
}
