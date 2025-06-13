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
public class FuelDataResponse {
    private String fuelId;
    private String fuelName;
    private FuelCategory category;
    private String unit;
    private EmissionActivityType emissionActivityType;
    private SubcategoryType subcategoryType;
    private BigDecimal gcv;
    private BigDecimal ncv;
    private BigDecimal co2Factor;
    private BigDecimal ch4FactorEnergy;
    private BigDecimal ch4FactorManufacturing;
    private BigDecimal ch4FactorCommercial;
    private BigDecimal ch4FactorDomestic;
    private BigDecimal n2oFactorEnergy;
    private BigDecimal n2oFactorManufacturing;
    private BigDecimal n2oFactorCommercial;
    private BigDecimal n2oFactorDomestic;
    private BigDecimal mobileCo2Factor;
    private BigDecimal mobileCh4Factor;
    private BigDecimal mobileN2oFactor;
    private Boolean hasMobileFactors;
    private LocalDateTime createdAt;
}
