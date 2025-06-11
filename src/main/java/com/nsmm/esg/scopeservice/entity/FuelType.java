package com.nsmm.esg.scopeservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 연료 타입 마스터 엔티티
 * 석유계, 석탄계, 가스계, 차량용 등 연료 타입 정보를 관리
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "fuel_type")
public class FuelType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String category;        // 연료 카테고리 (석유계, 석탄계, 가스계, 차량용)

    @Column(nullable = false, length = 100)
    private String name;           // 연료명 (경유, 휘발유, LNG, 등유 등)

    @Column(length = 200)
    private String description;    // 연료 설명

    @Column(nullable = false, length = 20)
    private String unit;           // 단위 (L, kg, m³, kWh 등)

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
