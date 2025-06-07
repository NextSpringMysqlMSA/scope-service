package com.nsmm.esg.scopeservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Scope 2 전력 사용 데이터 엔티티
 * 구매 전력 사용에 따른 간접 배출 활동 데이터
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "electricity_usage")
public class ElectricityUsage implements Identifiable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;         // 회원 ID

    @Column(nullable = false)
    private Long companyId;        // 회사 ID

    @Column(nullable = false)
    private Integer year;          // 데이터 연도

    @Column(nullable = false)
    private Integer month;         // 데이터 월

    @Column(nullable = false, length = 100)
    private String facilityName;   // 사업장명

    @Column(nullable = false, length = 50)
    private String supplier;       // 전력 공급업체

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal usage;      // 전력 사용량 (kWh)

    @Column(nullable = false, precision = 12, scale = 6)
    private BigDecimal emissionFactor;  // 전력 배출계수 (tCO2/MWh)

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal totalEmission;   // 총 배출량 (tCO2eq)

    @Column(nullable = false)
    @Builder.Default
    private Boolean isRenewable = false;  // 재생에너지 여부

    @Column(length = 500)
    private String notes;          // 비고

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
