package com.nsmm.esg.scopeservice.repository;

import com.nsmm.esg.scopeservice.entity.StationaryCombustion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface StationaryCombustionRepository extends JpaRepository<StationaryCombustion, Long> {

    /**
     * 회원별 고정연소 데이터 조회
     */
    List<StationaryCombustion> findByMemberIdOrderByYearDescMonthDesc(Long memberId);

    /**
     * 회원별 특정 연도 고정연소 데이터 조회
     */
    List<StationaryCombustion> findByMemberIdAndYearOrderByMonthAsc(Long memberId, Integer year);

    /**
     * 회원별 특정 연도/월 고정연소 데이터 조회
     */
    List<StationaryCombustion> findByMemberIdAndYearAndMonth(Long memberId, Integer year, Integer month);

    /**
     * 회원별 연도별 총 배출량 합계
     */
    @Query("SELECT SUM(s.totalEmission) FROM StationaryCombustion s WHERE s.memberId = :memberId AND s.year = :year")
    BigDecimal sumTotalEmissionByMemberIdAndYear(@Param("memberId") Long memberId, @Param("year") Integer year);

    /**
     * 회원별 월별 총 배출량 합계
     */
    @Query("SELECT SUM(s.totalEmission) FROM StationaryCombustion s WHERE s.memberId = :memberId AND s.year = :year AND s.month = :month")
    BigDecimal sumTotalEmissionByMemberIdAndYearAndMonth(@Param("memberId") Long memberId, @Param("year") Integer year, @Param("month") Integer month);

    /**
     * 연료 타입별 배출량 집계
     */
    @Query("SELECT s.fuelType.name, SUM(s.totalEmission) FROM StationaryCombustion s WHERE s.memberId = :memberId AND s.year = :year GROUP BY s.fuelType.name")
    List<Object[]> sumEmissionByFuelTypeAndMemberIdAndYear(@Param("memberId") Long memberId, @Param("year") Integer year);
}
