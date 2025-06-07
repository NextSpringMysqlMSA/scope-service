package com.nsmm.esg.scopeservice.dto;

import com.nsmm.esg.scopeservice.entity.StationaryCombustion;
import lombok.*;

import java.math.BigDecimal;

/**
 * Scope 1 고정연소 요청 DTO
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StationaryCombustionRequest {

    private Long companyId;
    private Integer year;
    private Integer month;
    private Long fuelTypeId;
    private String facilityName;
    private String facilityType;
    private BigDecimal usage;
    private String notes;

    /**
     * Entity로 변환 (배출량은 서비스에서 계산)
     */
    public StationaryCombustion toEntity(Long memberId) {
        return StationaryCombustion.builder()
                .memberId(memberId)
                .companyId(companyId)
                .year(year)
                .month(month)
                .facilityName(facilityName)
                .facilityType(facilityType)
                .usage(usage)
                .notes(notes)
                .build();
    }
}
