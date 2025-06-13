package com.nsmm.esg.scopeservice.dto.response;

import com.nsmm.esg.scopeservice.entity.enums.*;
import lombok.Getter;
import lombok.Builder;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ScopeEmissionResponse {
    private Long id;
    private Long memberId;
    private String companyId;
    private Integer reportingYear;
    private Integer reportingMonth;
    private EmissionActivityType emissionActivityType;
    private ScopeType scopeType;
    private String fuelId;
    private String fuelName;
    private BigDecimal fuelUsage;
    private String usageUnit;
    private PurposeCategory purposeCategory;
    private String steamType;
    private BigDecimal usedNcv;
    private BigDecimal usedCo2Factor;
    private BigDecimal usedCh4Factor;
    private BigDecimal usedN2oFactor;
    private BigDecimal co2Emission;
    private BigDecimal ch4Emission;
    private BigDecimal n2oEmission;
    private BigDecimal totalEmission;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String notes;
}
