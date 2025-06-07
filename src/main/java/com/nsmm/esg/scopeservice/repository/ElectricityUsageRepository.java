package com.nsmm.esg.scopeservice.repository;

import com.nsmm.esg.scopeservice.entity.ElectricityUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ElectricityUsageRepository extends JpaRepository<ElectricityUsage, Long> {

    /**
     * 회원별 전력 사용 데이터 조회
     */
    List<ElectricityUsage> findByMemberIdOrderByYearDescMonthDesc(Long memberId);

    /**
     * 회원별 특정 연도 전력 사용 데이터 조회
     */
    List<ElectricityUsage> findByMemberIdAndYearOrderByMonthAsc(Long memberId, Integer year);

    /**
     * 회원별 특정 연도/월 전력 사용 데이터 조회
     */
    List<ElectricityUsage> findByMemberIdAndYearAndMonth(Long memberId, Integer year, Integer month);

    /**
     * 회원별 연도별 총 배출량 합계
     */
    @Query("SELECT SUM(e.totalEmission) FROM ElectricityUsage e WHERE e.memberId = :memberId AND e.year = :year")
    BigDecimal sumTotalEmissionByMemberIdAndYear(@Param("memberId") Long memberId, @Param("year") Integer year);

    /**
     * 회원별 월별 총 배출량 합계
     */
    @Query("SELECT SUM(e.totalEmission) FROM ElectricityUsage e WHERE e.memberId = :memberId AND e.year = :year AND e.month = :month")
    BigDecimal sumTotalEmissionByMemberIdAndYearAndMonth(@Param("memberId") Long memberId, @Param("year") Integer year, @Param("month") Integer month);

    /**
     * 회원별 전력 사용량 합계
     */
    @Query("SELECT SUM(e.usage) FROM ElectricityUsage e WHERE e.memberId = :memberId AND e.year = :year")
    BigDecimal sumUsageByMemberIdAndYear(@Param("memberId") Long memberId, @Param("year") Integer year);

    /**
     * 공급업체별 배출량 집계
     */
    @Query("SELECT e.supplier, SUM(e.totalEmission) FROM ElectricityUsage e WHERE e.memberId = :memberId AND e.year = :year GROUP BY e.supplier")
    List<Object[]> sumEmissionBySupplierAndMemberIdAndYear(@Param("memberId") Long memberId, @Param("year") Integer year);
}
