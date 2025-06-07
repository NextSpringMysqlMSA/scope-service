package com.nsmm.esg.scopeservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Scope 2 스팀 사용 데이터 엔티티
 * 구매 스팀 사용에 따른 간접 배출 활동 데이터
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "steam_usage")
public class SteamUsage implements Identifiable<Long> {

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
    private String supplier;       // 스팀 공급업체

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal usage;      // 스팀 사용량 (톤)

    @Column(nullable = false, precision = 12, scale = 6)
    private BigDecimal emissionFactor;  // 스팀 배출계수 (tCO2/톤)

    @Column(nullable = false, precision = 15, scale = 4)
    private BigDecimal totalEmission;   // 총 배출량 (tCO2eq)

    @Column(length = 500)
    private String notes;          // 비고

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
