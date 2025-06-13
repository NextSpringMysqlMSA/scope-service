package com.nsmm.esg.scopeservice.entity;

import com.nsmm.esg.scopeservice.entity.enums.EmissionActivityType;
import com.nsmm.esg.scopeservice.entity.enums.FuelCategory;
import com.nsmm.esg.scopeservice.entity.enums.SubcategoryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fuel_data")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class FuelData {
    
    @Id
    @Column(name = "fuel_id", length = 50)
    private String fuelId; // GASOLINE, DIESEL, ELECTRICITY 등
    
    @Column(name = "fuel_name", nullable = false, length = 100)
    private String fuelName; // 휘발유, 경유, 전기 등
    
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private FuelCategory category;
    
    @Column(name = "unit", nullable = false, length = 20)
    private String unit; // kL, 톤, 천㎥, kWh, GJ
    
    @Enumerated(EnumType.STRING)
    @Column(name = "emission_activity_type", nullable = false)
    private EmissionActivityType emissionActivityType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "subcategory_type", nullable = false)
    private SubcategoryType subcategoryType;
    
    // 발열량 (GJ/단위)
    @Column(name = "gcv", precision = 10, scale = 3)
    private BigDecimal gcv; // 총발열량
    
    @Column(name = "ncv", precision = 10, scale = 3)
    private BigDecimal ncv; // 순발열량
    
    // CO2 배출계수 (kgCO2/GJ 또는 kgCO2/kWh)
    @Column(name = "co2_factor", precision = 15, scale = 6)
    private BigDecimal co2Factor;
    
    // CH4 배출계수 (용도별) - kgCH4/GJ
    @Column(name = "ch4_factor_energy", precision = 10, scale = 6)
    private BigDecimal ch4FactorEnergy;
    
    @Column(name = "ch4_factor_manufacturing", precision = 10, scale = 6)
    private BigDecimal ch4FactorManufacturing;
    
    @Column(name = "ch4_factor_commercial", precision = 10, scale = 6)
    private BigDecimal ch4FactorCommercial;
    
    @Column(name = "ch4_factor_domestic", precision = 10, scale = 6)
    private BigDecimal ch4FactorDomestic;
    
    // N2O 배출계수 (용도별) - kgN2O/GJ
    @Column(name = "n2o_factor_energy", precision = 10, scale = 6)
    private BigDecimal n2oFactorEnergy;
    
    @Column(name = "n2o_factor_manufacturing", precision = 10, scale = 6)
    private BigDecimal n2oFactorManufacturing;
    
    @Column(name = "n2o_factor_commercial", precision = 10, scale = 6)
    private BigDecimal n2oFactorCommercial;
    
    @Column(name = "n2o_factor_domestic", precision = 10, scale = 6)
    private BigDecimal n2oFactorDomestic;
    
    // 이동연소 배출계수 (mobileEmissionFactors가 있는 연료만)
    @Column(name = "mobile_co2_factor", precision = 15, scale = 6)
    private BigDecimal mobileCo2Factor;
    
    @Column(name = "mobile_ch4_factor", precision = 10, scale = 6)
    private BigDecimal mobileCh4Factor;
    
    @Column(name = "mobile_n2o_factor", precision = 10, scale = 6)
    private BigDecimal mobileN2oFactor;
    
    @Column(name = "has_mobile_factors")
    private Boolean hasMobileFactors = false;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}