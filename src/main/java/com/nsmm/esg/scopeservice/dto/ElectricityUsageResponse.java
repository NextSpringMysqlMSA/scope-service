package com.nsmm.esg.scopeservice.dto;

import com.nsmm.esg.scopeservice.entity.ElectricityUsage;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Scope 2 전력 사용 응답 DTO
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ElectricityUsageResponse {

    private Long id;
    private Long companyId;
    private Integer year;
    private Integer month;
    private String facilityName;
    private String supplier;
    private BigDecimal usage;
    private BigDecimal emissionFactor;
    private BigDecimal totalEmission;
    private Boolean isRenewable;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity에서 DTO로 변환
     */
    public static ElectricityUsageResponse fromEntity(ElectricityUsage entity) {
        return ElectricityUsageResponse.builder()
                .id(entity.getId())
                .companyId(entity.getCompanyId())
                .year(entity.getYear())
                .month(entity.getMonth())
                .facilityName(entity.getFacilityName())
                .supplier(entity.getSupplier())
                .usage(entity.getUsage())
                .emissionFactor(entity.getEmissionFactor())
                .totalEmission(entity.getTotalEmission())
                .isRenewable(entity.getIsRenewable())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
