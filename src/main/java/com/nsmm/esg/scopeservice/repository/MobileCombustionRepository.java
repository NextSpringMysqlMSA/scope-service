package com.nsmm.esg.scopeservice.repository;

import com.nsmm.esg.scopeservice.entity.MobileCombustion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface MobileCombustionRepository extends JpaRepository<MobileCombustion, Long> {

    /**
     * 회원별 이동연소 데이터 조회
     */
    List<MobileCombustion> findByMemberIdOrderByYearDescMonthDesc(Long memberId);

    /**
     * 회원별 특정 연도 이동연소 데이터 조회
     */
    List<MobileCombustion> findByMemberIdAndYearOrderByMonthAsc(Long memberId, Integer year);

    /**
     * 회원별 특정 연도/월 이동연소 데이터 조회
     */
    List<MobileCombustion> findByMemberIdAndYearAndMonth(Long memberId, Integer year, Integer month);

    /**
     * 회원별 연도별 총 배출량 합계
     */
    @Query("SELECT SUM(m.totalEmission) FROM MobileCombustion m WHERE m.memberId = :memberId AND m.year = :year")
    BigDecimal sumTotalEmissionByMemberIdAndYear(@Param("memberId") Long memberId, @Param("year") Integer year);

    /**
     * 회원별 월별 총 배출량 합계
     */
    @Query("SELECT SUM(m.totalEmission) FROM MobileCombustion m WHERE m.memberId = :memberId AND m.year = :year AND m.month = :month")
    BigDecimal sumTotalEmissionByMemberIdAndYearAndMonth(@Param("memberId") Long memberId, @Param("year") Integer year, @Param("month") Integer month);

    /**
     * 차량 타입별 배출량 집계
     */
    @Query("SELECT m.vehicleType, SUM(m.totalEmission) FROM MobileCombustion m WHERE m.memberId = :memberId AND m.year = :year GROUP BY m.vehicleType")
    List<Object[]> sumEmissionByVehicleTypeAndMemberIdAndYear(@Param("memberId") Long memberId, @Param("year") Integer year);

    /**
     * 연료 타입별 배출량 집계
     */
    @Query("SELECT m.fuelType.name, SUM(m.totalEmission) FROM MobileCombustion m WHERE m.memberId = :memberId AND m.year = :year GROUP BY m.fuelType.name")
    List<Object[]> sumEmissionByFuelTypeAndMemberIdAndYear(@Param("memberId") Long memberId, @Param("year") Integer year);
}
