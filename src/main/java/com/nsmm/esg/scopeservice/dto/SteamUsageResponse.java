package com.nsmm.esg.scopeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SteamUsageResponse {

    private Long id;
    private Long companyId;
    private Integer reportingYear;
    private String facilityName;
    private BigDecimal steamUsage;
    private String unit;
    private BigDecimal co2Emission;
    private LocalDateTime calculatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
