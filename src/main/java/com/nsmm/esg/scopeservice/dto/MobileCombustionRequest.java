package com.nsmm.esg.scopeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MobileCombustionRequest {

    @NotNull(message = "회사 ID는 필수입니다.")
    private Long companyId;

    @NotNull(message = "보고연도는 필수입니다.")
    @Min(value = 2000, message = "보고연도는 2000년 이후여야 합니다.")
    private Integer reportingYear;

    @NotBlank(message = "차량 유형은 필수입니다.")
    private String vehicleType;

    @NotNull(message = "연료 타입 ID는 필수입니다.")
    private Long fuelTypeId;

    @NotNull(message = "연료 사용량은 필수입니다.")
    @Positive(message = "연료 사용량은 양수여야 합니다.")
    private BigDecimal fuelUsage;

    @NotBlank(message = "단위는 필수입니다.")
    private String unit;

    private String createdBy;
}
