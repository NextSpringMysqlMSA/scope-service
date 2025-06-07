package com.nsmm.esg.scopeservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 발열량 마스터 엔티티
 * 각 연료별 발열량(칼로리) 정보를 관리
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "calorific_value")
public class CalorificValue implements Identifiable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fuel_type_id", nullable = false)
    private FuelType fuelType;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal value;       // 발열량 값 (TJ/단위)

    @Column(nullable = false, length = 20)
    private String unit;           // 발열량 단위 (TJ/kL, TJ/ton 등)

    @Column(nullable = false)
    private Integer year;          // 적용 연도

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
