package com.nsmm.esg.scopeservice.repository;

import com.nsmm.esg.scopeservice.entity.EmissionFactor;
import com.nsmm.esg.scopeservice.entity.FuelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
     * 연료 타입별 활성화된 배출계수 조회
     */
    List<EmissionFactor> findByFuelTypeAndIsActiveTrueOrderByYearDesc(FuelType fuelType);

    /**
     * 특정 연도의 활성화된 배출계수 조회
     */
    List<EmissionFactor> findByYearAndIsActiveTrueOrderByFuelType_Category(Integer year);

    /**
     * 연료 ID와 연도로 배출계수 조회
     */
    @Query("SELECT e FROM EmissionFactor e WHERE e.fuelType.id = :fuelTypeId AND e.year = :year AND e.isActive = true")
    Optional<EmissionFactor> findByFuelTypeIdAndYear(@Param("fuelTypeId") Long fuelTypeId, @Param("year") Integer year);
}
