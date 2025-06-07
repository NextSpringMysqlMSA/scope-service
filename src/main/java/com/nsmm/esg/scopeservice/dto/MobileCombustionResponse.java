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
public class MobileCombustionResponse {

    private Long id;
    private Long companyId;
    private Integer reportingYear;
    private String vehicleType;
    private String fuelTypeName;
    private BigDecimal fuelUsage;
    private String unit;
    private BigDecimal co2Emission;
    private BigDecimal ch4Emission;
    private BigDecimal n2oEmission;
    private BigDecimal totalCo2Equivalent;
    private LocalDateTime calculatedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
