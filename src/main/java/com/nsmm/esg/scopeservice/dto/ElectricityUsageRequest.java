package com.nsmm.esg.scopeservice.dto;

import com.nsmm.esg.scopeservice.entity.ElectricityUsage;
import lombok.*;

import java.math.BigDecimal;

/**
 * Scope 2 전력 사용 요청 DTO
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ElectricityUsageRequest {

    private Long companyId;
    private Integer year;
    private Integer month;
    private String facilityName;
    private String supplier;
    private BigDecimal usage;
    private BigDecimal emissionFactor;
    private Boolean isRenewable;
    private String notes;

    /**
     * Entity로 변환 (배출량은 서비스에서 계산)
     */
    public ElectricityUsage toEntity(Long memberId) {
        return ElectricityUsage.builder()
                .memberId(memberId)
                .companyId(companyId)
                .year(year)
                .month(month)
                .facilityName(facilityName)
                .supplier(supplier)
                .usage(usage)
                .emissionFactor(emissionFactor)
                .isRenewable(isRenewable != null ? isRenewable : false)
                .notes(notes)
                .build();
    }
}
