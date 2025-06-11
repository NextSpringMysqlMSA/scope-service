package com.nsmm.esg.scopeservice.repository;

import com.nsmm.esg.scopeservice.entity.CalorificValue;
import com.nsmm.esg.scopeservice.entity.FuelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalorificValueRepository extends JpaRepository<CalorificValue, Long> {

    /**
     * 연료 타입과 연도로 발열량 조회
     */
    Optional<CalorificValue> findByFuelTypeAndYearAndIsActiveTrue(FuelType fuelType, Integer year);

    /**
     * 연료 타입 ID로 발열량 조회
     */
    Optional<CalorificValue> findByFuelType_IdAndYearAndIsActiveTrue(Long fuelTypeId, Integer year);

    /**
     * 연료 타입별 활성화된 발열량 목록 (최신 연도부터)
     */
    List<CalorificValue> findByFuelTypeAndIsActiveTrueOrderByYearDesc(FuelType fuelType);

    /**
     * 연도 기준 전체 연료 발열량 조회
     */
    List<CalorificValue> findByYearAndIsActiveTrueOrderByFuelType_Category(Integer year);

    /**
     * 연료 타입 ID로 활성화 여부 확인
     */
    boolean existsByFuelTypeId(Long fuelTypeId);

    Optional<CalorificValue> findByFuelTypeId(Long fuelTypeId);
}
