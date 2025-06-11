package com.nsmm.esg.scopeservice.dto;

import com.nsmm.esg.scopeservice.entity.SteamUsage;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Scope 2 스팀 사용 응답 DTO
 * 스팀 사용량 전용 필드만 포함
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SteamUsageResponse {

    private Long id;
    private Long memberId;                 // 회원 ID 추가
    private String companyId;              // 회사/협력사 ID (UUID)
    private Integer reportingYear;         // 보고 연도
    private Integer reportingMonth;        // 보고 월
    private String facilityName;           // 시설명
    private String facilityLocation;       // 시설 위치
    
    // 🎯 스팀 전용 필드들
    private String steamType;              // 스팀 타입 (고압, 중압, 저압 등)
    private BigDecimal usage;              // 스팀 사용량
    private String unit;                   // 단위 (GJ, MJ 등)
    
    // 🎯 스팀 배출량 (CO2만)
    private BigDecimal co2Emission;        // CO2 배출량
    private BigDecimal totalCo2Equivalent; // 총 CO2 등가량 (스팀은 CO2와 동일)
    private LocalDateTime calculatedAt;    // 계산 일시
    
    private String notes;                  // 비고
    private LocalDateTime createdAt;       // 생성일시
    private LocalDateTime updatedAt;       // 수정일시

  
}