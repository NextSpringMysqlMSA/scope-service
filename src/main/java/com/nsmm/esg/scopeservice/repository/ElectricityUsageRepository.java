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

    // 협력사별 조회 메서드들

    /**
     * 회원별 및 협력사별 전력 사용 데이터 조회
     */
    List<ElectricityUsage> findByMemberIdAndPartnerCompanyIdOrderByYearDescMonthDesc(Long memberId, String partnerCompanyId);

    /**
     * 회원별 및 협력사별 특정 연도 전력 사용 데이터 조회
     */
    List<ElectricityUsage> findByMemberIdAndPartnerCompanyIdAndYearOrderByMonthAsc(Long memberId, String partnerCompanyId, Integer year);

    /**
     * 회원별 및 협력사별 특정 연도/월 전력 사용 데이터 조회
     */
    List<ElectricityUsage> findByMemberIdAndPartnerCompanyIdAndYearAndMonth(Long memberId, String partnerCompanyId, Integer year, Integer month);

    /**
     * 회원별 및 협력사별 연도별 총 배출량 합계
     */
    @Query("SELECT SUM(e.totalEmission) FROM ElectricityUsage e WHERE e.memberId = :memberId AND e.partnerCompanyId = :partnerCompanyId AND e.year = :year")
    BigDecimal sumTotalEmissionByMemberIdAndPartnerCompanyIdAndYear(@Param("memberId") Long memberId, @Param("partnerCompanyId") String partnerCompanyId, @Param("year") Integer year);

    /**
     * 회원별 및 협력사별 월별 총 배출량 합계
     */
    @Query("SELECT SUM(e.totalEmission) FROM ElectricityUsage e WHERE e.memberId = :memberId AND e.partnerCompanyId = :partnerCompanyId AND e.year = :year AND e.month = :month")
    BigDecimal sumTotalEmissionByMemberIdAndPartnerCompanyIdAndYearAndMonth(@Param("memberId") Long memberId, @Param("partnerCompanyId") String partnerCompanyId, @Param("year") Integer year, @Param("month") Integer month);

    // Service에서 사용되는 추가 메서드들 (필요 시 추가 예정)
}
