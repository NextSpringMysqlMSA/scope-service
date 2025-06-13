package com.nsmm.esg.scopeservice.dto.request;

import com.nsmm.esg.scopeservice.entity.enums.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ScopeEmissionRequest {
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
    private String notes;
}
