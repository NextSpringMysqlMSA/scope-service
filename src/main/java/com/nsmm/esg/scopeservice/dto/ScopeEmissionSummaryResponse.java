package com.nsmm.esg.scopeservice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Scope 배출량 요약 응답 DTO
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScopeEmissionSummaryResponse {

    private Integer year;
    private Integer month;
    private Scope1Summary scope1;
    private Scope2Summary scope2;
    private BigDecimal totalEmission;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Scope1Summary {
        private BigDecimal stationaryEmission;
        private BigDecimal mobileEmission;
        private BigDecimal totalScope1Emission;
        private List<FuelTypeEmission> fuelTypeBreakdown;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Scope2Summary {
        private BigDecimal electricityEmission;
        private BigDecimal steamEmission;
        private BigDecimal totalScope2Emission;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FuelTypeEmission {
        private String fuelType;
        private String category;
        private BigDecimal emission;
        private BigDecimal percentage;
    }
}
