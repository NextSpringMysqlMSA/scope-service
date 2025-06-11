package com.nsmm.esg.scopeservice.repository;

import com.nsmm.esg.scopeservice.entity.SteamUsage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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

    // 협력사별 조회 메서드들

    /**
     * 회원별 및 협력사별 스팀 사용 데이터 조회
     */
    List<SteamUsage> findByMemberIdAndPartnerCompanyIdOrderByYearDescMonthDesc(Long memberId, String partnerCompanyId);

    /**
     * 회원별 및 협력사별 특정 연도 스팀 사용 데이터 조회
     */
    List<SteamUsage> findByMemberIdAndPartnerCompanyIdAndYearOrderByMonthAsc(Long memberId, String partnerCompanyId, Integer year);

    /**
     * 회원별 및 협력사별 특정 연도/월 스팀 사용 데이터 조회
     */
    List<SteamUsage> findByMemberIdAndPartnerCompanyIdAndYearAndMonth(Long memberId, String partnerCompanyId, Integer year, Integer month);

    /**
     * 회원별 및 협력사별 연도별 총 배출량 합계
     */
    @Query("SELECT SUM(s.totalEmission) FROM SteamUsage s WHERE s.memberId = :memberId AND s.partnerCompanyId = :partnerCompanyId AND s.year = :year")
    BigDecimal sumTotalEmissionByMemberIdAndPartnerCompanyIdAndYear(@Param("memberId") Long memberId, @Param("partnerCompanyId") String partnerCompanyId, @Param("year") Integer year);

    /**
     * 회원별 및 협력사별 월별 총 배출량 합계
     */
    @Query("SELECT SUM(s.totalEmission) FROM SteamUsage s WHERE s.memberId = :memberId AND s.partnerCompanyId = :partnerCompanyId AND s.year = :year AND s.month = :month")
    BigDecimal sumTotalEmissionByMemberIdAndPartnerCompanyIdAndYearAndMonth(@Param("memberId") Long memberId, @Param("partnerCompanyId") String partnerCompanyId, @Param("year") Integer year, @Param("month") Integer month);

    // Service에서 사용되는 추가 메서드들

    /**
     * 회사별 스팀 사용 데이터 조회 (생성일 역순)
     */
    @Query("SELECT s FROM SteamUsage s WHERE s.companyId = :companyId ORDER BY s.createdAt DESC")
    Page<SteamUsage> findByCompanyIdOrderByCreatedAtDesc(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 회사별 및 연도별 스팀 사용 데이터 조회
     */
    List<SteamUsage> findByCompanyIdAndReportingYear(Long companyId, Integer reportingYear);

    /**
     * 회사별 및 연도별 CO2 배출량 합계
     */
    @Query("SELECT SUM(s.co2Emission) FROM SteamUsage s WHERE s.companyId = :companyId AND s.reportingYear = :year")
    Optional<BigDecimal> sumCo2EmissionByCompanyIdAndYear(@Param("companyId") Long companyId, @Param("year") Integer year);

    /**
     * 시설별 배출량 합계
     */
    @Query("SELECT s.facilityName, SUM(s.co2Emission) FROM SteamUsage s WHERE s.companyId = :companyId AND s.reportingYear = :year GROUP BY s.facilityName")
    List<Object[]> sumEmissionByFacility(@Param("companyId") Long companyId, @Param("year") Integer year);
}
