package com.nsmm.esg.scopeservice.repository;

import com.nsmm.esg.scopeservice.entity.SteamUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SteamUsageRepository extends JpaRepository<SteamUsage, Long> {

    /**
     * 회원별 스팀 사용 데이터 조회
     */
    List<SteamUsage> findByMemberIdOrderByYearDescMonthDesc(Long memberId);

    /**
     * 회원별 특정 연도 스팀 사용 데이터 조회
     */
    List<SteamUsage> findByMemberIdAndYearOrderByMonthAsc(Long memberId, Integer year);

    /**
     * 회원별 특정 연도/월 스팀 사용 데이터 조회
     */
    List<SteamUsage> findByMemberIdAndYearAndMonth(Long memberId, Integer year, Integer month);

    /**
     * 회원별 연도별 총 배출량 합계
     */
    @Query("SELECT SUM(s.totalEmission) FROM SteamUsage s WHERE s.memberId = :memberId AND s.year = :year")
    BigDecimal sumTotalEmissionByMemberIdAndYear(@Param("memberId") Long memberId, @Param("year") Integer year);

    /**
     * 회원별 월별 총 배출량 합계
     */
    @Query("SELECT SUM(s.totalEmission) FROM SteamUsage s WHERE s.memberId = :memberId AND s.year = :year AND s.month = :month")
    BigDecimal sumTotalEmissionByMemberIdAndYearAndMonth(@Param("memberId") Long memberId, @Param("year") Integer year, @Param("month") Integer month);

    /**
     * 회원별 스팀 사용량 합계
     */
    @Query("SELECT SUM(s.usage) FROM SteamUsage s WHERE s.memberId = :memberId AND s.year = :year")
    BigDecimal sumUsageByMemberIdAndYear(@Param("memberId") Long memberId, @Param("year") Integer year);

    /**
     * 공급업체별 배출량 집계
     */
    @Query("SELECT s.supplier, SUM(s.totalEmission) FROM SteamUsage s WHERE s.memberId = :memberId AND s.year = :year GROUP BY s.supplier")
    List<Object[]> sumEmissionBySupplierAndMemberIdAndYear(@Param("memberId") Long memberId, @Param("year") Integer year);
}
