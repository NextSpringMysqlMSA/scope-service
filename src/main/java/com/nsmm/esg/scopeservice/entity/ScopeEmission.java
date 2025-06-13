package com.nsmm.esg.scopeservice.entity;

import com.nsmm.esg.scopeservice.entity.enums.EmissionActivityType;
import com.nsmm.esg.scopeservice.entity.enums.PurposeCategory;
import com.nsmm.esg.scopeservice.entity.enums.ScopeType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "scope_emissions", 
       indexes = {
           @Index(name = "idx_member_company_year_month", 
                  columnList = "memberId, companyId, reportingYear, reportingMonth"),
           @Index(name = "idx_member_company_year", 
                  columnList = "memberId, companyId, reportingYear"),
           @Index(name = "idx_fuel_id", 
                  columnList = "fuelId"),
           @Index(name = "idx_activity_type", 
                  columnList = "emissionActivityType")
       })
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ScopeEmission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 사용자 및 기업 식별자 (Gateway에서 전달받음)
    @Column(name = "member_id", nullable = false)
    private Long memberId; // JWT에서 추출된 회원 ID
    
    @Column(name = "company_id", nullable = false, length = 36)
    private String companyId; // UUID 형태의 기업 ID
    
    // 보고 기간
    @Column(name = "reporting_year", nullable = false)
    private Integer reportingYear; // 2024, 2025 등
    
    @Column(name = "reporting_month", nullable = false)
    private Integer reportingMonth; // 1~12월
    
    // 배출 활동 정보
    @Enumerated(EnumType.STRING)
    @Column(name = "emission_activity_type", nullable = false)
    private EmissionActivityType emissionActivityType; // STATIONARY_COMBUSTION, MOBILE_COMBUSTION, ELECTRICITY, STEAM
    
    @Enumerated(EnumType.STRING)
    @Column(name = "scope_type", nullable = false)
    private ScopeType scopeType; // SCOPE1, SCOPE2
    
    // 연료 정보 (fuel-data.ts 기준)
    @Column(name = "fuel_id", nullable = false, length = 50)
    private String fuelId; // GASOLINE, DIESEL, ELECTRICITY 등
    
    @Column(name = "fuel_name", length = 100)
    private String fuelName; // 휘발유, 경유 등 (조회 편의용)
    
    // 입력 데이터 (모달창에서 입력받은 값)
    @Column(name = "fuel_usage", precision = 15, scale = 3, nullable = false)
    private BigDecimal fuelUsage; // 연료 사용량
    
    @Column(name = "usage_unit", length = 20)
    private String usageUnit; // kL, 톤, 천㎥, kWh, GJ
    
    // 고정연소 전용 추가 정보
    @Enumerated(EnumType.STRING)
    @Column(name = "purpose_category")
    private PurposeCategory purposeCategory; // energy, manufacturing, commercial, domestic
    
    // 스팀 전용 추가 정보
    @Column(name = "steam_type", length = 5)
    private String steamType; // A, B, C (STEAM_A, STEAM_B, STEAM_C에서 추출)
    
    // 계산에 사용된 배출계수들 (감사 추적용)
    @Column(name = "used_ncv", precision = 10, scale = 3)
    private BigDecimal usedNcv;
    
    @Column(name = "used_co2_factor", precision = 15, scale = 6)
    private BigDecimal usedCo2Factor;
    
    @Column(name = "used_ch4_factor", precision = 10, scale = 6)
    private BigDecimal usedCh4Factor;
    
    @Column(name = "used_n2o_factor", precision = 10, scale = 6)
    private BigDecimal usedN2oFactor;
    
    // 계산 결과 (tCO2eq 단위)
    @Column(name = "co2_emission", precision = 15, scale = 6, nullable = false)
    private BigDecimal co2Emission;
    
    @Column(name = "ch4_emission", precision = 15, scale = 6, nullable = false)
    private BigDecimal ch4Emission;
    
    @Column(name = "n2o_emission", precision = 15, scale = 6, nullable = false)
    private BigDecimal n2oEmission;
    
    @Column(name = "total_emission", precision = 15, scale = 6, nullable = false)
    private BigDecimal totalEmission;
    
    // 메타 정보
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "notes", length = 1000)
    private String notes; // 사용자 메모
    
    // 연관관계 - FuelData 참조 (조회 시 연료 정보 가져오기 위해)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fuel_id", insertable = false, updatable = false)
    private FuelData fuelData;
    
    /**
     * 배출량 계산을 포함한 ScopeEmission 생성 메서드
     */
    public static ScopeEmission createWithCalculatedEmissions(
        Long memberId,
        String companyId,
        Integer reportingYear,
        Integer reportingMonth,
        EmissionActivityType emissionActivityType,
        ScopeType scopeType,
        String fuelId,
        String fuelName,
        BigDecimal fuelUsage,
        String usageUnit,
        PurposeCategory purposeCategory,
        String steamType,
        BigDecimal usedNcv,
        BigDecimal usedCo2Factor,
        BigDecimal usedCh4Factor,
        BigDecimal usedN2oFactor,
        String notes
    ) {
        // 배출량 계산
        EmissionCalculationResult result = calculateEmissions(
            fuelUsage, usedNcv, usedCo2Factor, usedCh4Factor, usedN2oFactor
        );
        
        return ScopeEmission.builder()
            .memberId(memberId)
            .companyId(companyId)
            .reportingYear(reportingYear)
            .reportingMonth(reportingMonth)
            .emissionActivityType(emissionActivityType)
            .scopeType(scopeType)
            .fuelId(fuelId)
            .fuelName(fuelName)
            .fuelUsage(fuelUsage)
            .usageUnit(usageUnit)
            .purposeCategory(purposeCategory)
            .steamType(steamType)
            .usedNcv(usedNcv)
            .usedCo2Factor(usedCo2Factor)
            .usedCh4Factor(usedCh4Factor)
            .usedN2oFactor(usedN2oFactor)
            .co2Emission(result.getCo2Emission())
            .ch4Emission(result.getCh4Emission())
            .n2oEmission(result.getN2oEmission())
            .totalEmission(result.getTotalEmission())
            .notes(notes)
            .build();
    }
    
    /**
     * 배출량 계산 로직
     * CO2 + (CH4 * 21) + (N2O * 310)
     */
    private static EmissionCalculationResult calculateEmissions(
        BigDecimal fuelUsage,
        BigDecimal ncv,
        BigDecimal co2Factor,
        BigDecimal ch4Factor,
        BigDecimal n2oFactor
    ) {
        // GWP 값 (Global Warming Potential)
        final BigDecimal CH4_GWP = BigDecimal.valueOf(21);
        final BigDecimal N2O_GWP = BigDecimal.valueOf(310);
        
        // 에너지 소비량 계산 (TJ 단위)
        BigDecimal energyConsumption = fuelUsage.multiply(ncv).divide(BigDecimal.valueOf(1000), 6, BigDecimal.ROUND_HALF_UP);
        
        // 각 가스별 배출량 계산 (tCO2eq)
        BigDecimal co2Emission = energyConsumption.multiply(co2Factor).divide(BigDecimal.valueOf(1000), 6, BigDecimal.ROUND_HALF_UP);
        BigDecimal ch4Emission = energyConsumption.multiply(ch4Factor).multiply(CH4_GWP).divide(BigDecimal.valueOf(1000000), 6, BigDecimal.ROUND_HALF_UP);
        BigDecimal n2oEmission = energyConsumption.multiply(n2oFactor).multiply(N2O_GWP).divide(BigDecimal.valueOf(1000000), 6, BigDecimal.ROUND_HALF_UP);
        
        // 총 배출량
        BigDecimal totalEmission = co2Emission.add(ch4Emission).add(n2oEmission);
        
        return new EmissionCalculationResult(co2Emission, ch4Emission, n2oEmission, totalEmission);
    }
    
    /**
     * 배출량 계산 결과를 담는 내부 클래스
     */
    @Getter
    @AllArgsConstructor
    private static class EmissionCalculationResult {
        private final BigDecimal co2Emission;
        private final BigDecimal ch4Emission;
        private final BigDecimal n2oEmission;
        private final BigDecimal totalEmission;
    }
    
    /**
     * 사용량 변경 시 새로운 인스턴스 반환 (불변성 유지)
     */
    public ScopeEmission withUpdatedUsage(BigDecimal newFuelUsage, String notes) {
        // 새로운 배출량 계산
        EmissionCalculationResult result = calculateEmissions(
            newFuelUsage, this.usedNcv, this.usedCo2Factor, 
            this.usedCh4Factor, this.usedN2oFactor
        );
        
        return ScopeEmission.builder()
            .id(this.id) // 기존 ID 유지 (업데이트용)
            .memberId(this.memberId)
            .companyId(this.companyId)
            .reportingYear(this.reportingYear)
            .reportingMonth(this.reportingMonth)
            .emissionActivityType(this.emissionActivityType)
            .scopeType(this.scopeType)
            .fuelId(this.fuelId)
            .fuelName(this.fuelName)
            .fuelUsage(newFuelUsage) // 새로운 사용량
            .usageUnit(this.usageUnit)
            .purposeCategory(this.purposeCategory)
            .steamType(this.steamType)
            .usedNcv(this.usedNcv)
            .usedCo2Factor(this.usedCo2Factor)
            .usedCh4Factor(this.usedCh4Factor)
            .usedN2oFactor(this.usedN2oFactor)
            .co2Emission(result.getCo2Emission()) // 재계산된 배출량
            .ch4Emission(result.getCh4Emission())
            .n2oEmission(result.getN2oEmission())
            .totalEmission(result.getTotalEmission())
            .notes(notes)
            .createdAt(this.createdAt) // 기존 생성일시 유지
            .build();
    }
    
    /**
     * 메모 변경 시 새로운 인스턴스 반환 (불변성 유지)
     */
    public ScopeEmission withUpdatedNotes(String notes) {
        return ScopeEmission.builder()
            .id(this.id)
            .memberId(this.memberId)
            .companyId(this.companyId)
            .reportingYear(this.reportingYear)
            .reportingMonth(this.reportingMonth)
            .emissionActivityType(this.emissionActivityType)
            .scopeType(this.scopeType)
            .fuelId(this.fuelId)
            .fuelName(this.fuelName)
            .fuelUsage(this.fuelUsage)
            .usageUnit(this.usageUnit)
            .purposeCategory(this.purposeCategory)
            .steamType(this.steamType)
            .usedNcv(this.usedNcv)
            .usedCo2Factor(this.usedCo2Factor)
            .usedCh4Factor(this.usedCh4Factor)
            .usedN2oFactor(this.usedN2oFactor)
            .co2Emission(this.co2Emission)
            .ch4Emission(this.ch4Emission)
            .n2oEmission(this.n2oEmission)
            .totalEmission(this.totalEmission)
            .notes(notes) // 새로운 메모
            .createdAt(this.createdAt)
            .build();
    }
}