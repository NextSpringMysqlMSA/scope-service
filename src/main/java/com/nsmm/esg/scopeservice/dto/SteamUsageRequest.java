package com.nsmm.esg.scopeservice.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * Scope 2 스팀 사용 요청 DTO
 * ScopeModal에서 전송되는 데이터 구조에 맞춤
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SteamUsageRequest {

    private String companyId;              // 회사/협력사 ID (UUID)
    private Integer reportingYear;         // 보고 연도
    private Integer reportingMonth;        // 보고 월
    private String facilityName;           // 시설명
    private String facilityLocation;       // 시설 위치
    private String steamType;              // 스팀 타입
    private BigDecimal steamUsage;         // 스팀 사용량
    private String unit;                   // 단위 (GJ)
    private String createdBy;              // 생성자
    private String notes;                  // 비고
}
