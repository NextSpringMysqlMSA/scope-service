package com.nsmm.esg.scopeservice.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * Scope 2 스팀 사용 요청 DTO
 * 스팀 사용량 전용 필드만 포함
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SteamUsageRequest {

    private Long memberId;                 // 회원 ID 추가
    private String companyId;              // 회사/협력사 ID (UUID)
    private Integer reportingYear;         // 보고 연도
    private Integer reportingMonth;        // 보고 월
    private String facilityName;           // 시설명
    private String facilityLocation;       // 시설 위치
    
    // 🎯 스팀 전용 필드들만
    private String steamType;              // 스팀 타입 (고압, 중압, 저압 등)
    private BigDecimal steamUsage;              // 스팀 사용량
    private String unit;                   // 단위 (GJ, MJ 등)
    private String createdBy;              // 생성자 (ScopeModal의 createdBy)
    private String notes;                  // 비고
}