package com.nsmm.esg.scopeservice.repository;

import com.nsmm.esg.scopeservice.entity.EmissionFactor;
import com.nsmm.esg.scopeservice.entity.FuelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmissionFactorRepository extends JpaRepository<EmissionFactor, Long> {

    /**
     * 연료 타입과 연도로 배출계수 조회
     */
    Optional<EmissionFactor> findByFuelTypeAndYearAndIsActiveTrue(FuelType fuelType, Integer year);

    /**
     * 연료 타입 ID와 연도로 배출계수 조회
     */
    Optional<EmissionFactor> findByFuelType_IdAndYearAndIsActiveTrue(Long fuelTypeId, Integer year);

    /**
     * 연료 타입별 최신순 배출계수 목록
     */
    List<EmissionFactor> findByFuelTypeAndIsActiveTrueOrderByYearDesc(FuelType fuelType);

    /**
     * 특정 연도 기준 전체 배출계수 조회
     */
    List<EmissionFactor> findByYearAndIsActiveTrueOrderByFuelType_Category(Integer year);

    /**
     * 연료 타입 ID 기준 활성 배출계수 존재 여부
     */
    boolean existsByFuelTypeId(Long fuelTypeId);

    Optional<EmissionFactor> findByFuelTypeId(Long fuelTypeId);
}
