package com.nsmm.esg.scopeservice.dto.request;

import com.nsmm.esg.scopeservice.entity.enums.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class FuelDataRequest {
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
}
