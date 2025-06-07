package com.nsmm.esg.scopeservice.repository;

import com.nsmm.esg.scopeservice.entity.CalorificValue;
import com.nsmm.esg.scopeservice.entity.FuelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
     * 연료 타입별 활성화된 발열량 조회
     */
    List<CalorificValue> findByFuelTypeAndIsActiveTrueOrderByYearDesc(FuelType fuelType);

    /**
     * 특정 연도의 활성화된 발열량 조회
     */
    List<CalorificValue> findByYearAndIsActiveTrueOrderByFuelType_Category(Integer year);

    /**
     * 연료 ID와 연도로 발열량 조회
     */
    @Query("SELECT c FROM CalorificValue c WHERE c.fuelType.id = :fuelTypeId AND c.year = :year AND c.isActive = true")
    Optional<CalorificValue> findByFuelTypeIdAndYear(@Param("fuelTypeId") Long fuelTypeId, @Param("year") Integer year);
}
