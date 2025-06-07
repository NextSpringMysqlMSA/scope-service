package com.nsmm.esg.scopeservice.dto;

import com.nsmm.esg.scopeservice.entity.StationaryCombustion;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Scope 1 고정연소 응답 DTO
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StationaryCombustionResponse {

    private Long id;
    private Long companyId;
    private Integer year;
    private Integer month;
    private String fuelTypeName;
    private String fuelTypeCategory;
    private String facilityName;
    private String facilityType;
    private BigDecimal usage;
    private String fuelUnit;
    private BigDecimal co2Emission;
    private BigDecimal ch4Emission;
    private BigDecimal n2oEmission;
    private BigDecimal totalEmission;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity에서 DTO로 변환
     */
    public static StationaryCombustionResponse fromEntity(StationaryCombustion entity) {
        return StationaryCombustionResponse.builder()
                .id(entity.getId())
                .companyId(entity.getCompanyId())
                .year(entity.getYear())
                .month(entity.getMonth())
                .fuelTypeName(entity.getFuelType().getName())
                .fuelTypeCategory(entity.getFuelType().getCategory())
                .facilityName(entity.getFacilityName())
                .facilityType(entity.getFacilityType())
                .usage(entity.getUsage())
                .fuelUnit(entity.getFuelType().getUnit())
                .co2Emission(entity.getCo2Emission())
                .ch4Emission(entity.getCh4Emission())
                .n2oEmission(entity.getN2oEmission())
                .totalEmission(entity.getTotalEmission())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
