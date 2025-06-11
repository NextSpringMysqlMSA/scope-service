package com.nsmm.esg.scopeservice.repository;

import com.nsmm.esg.scopeservice.entity.ElectricityUsage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface ElectricityUsageRepository extends JpaRepository<ElectricityUsage, Long> {

    // 기본 조회 메서드들
    Page<ElectricityUsage> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);
    
    List<ElectricityUsage> findByMemberIdAndReportingYear(Long memberId, Integer reportingYear);
    
    List<ElectricityUsage> findByMemberIdAndCompanyId(Long memberId, String companyId);
    
    List<ElectricityUsage> findByMemberIdAndCompanyIdAndReportingYear(Long memberId, String companyId, Integer reportingYear);

    Page<ElectricityUsage> findByReportingYearOrderByCreatedAtDesc(Integer reportingYear, Pageable pageable);

    // 집계 쿼리들
    @Query("SELECT e.reportingMonth, SUM(e.totalCo2Equivalent) " +
           "FROM ElectricityUsage e " +
           "WHERE e.memberId = :memberId AND e.reportingYear = :year " +
           "GROUP BY e.reportingMonth " +
           "ORDER BY e.reportingMonth")
    List<Object[]> findMonthlyEmissions(@Param("memberId") Long memberId, @Param("year") Integer year);

    @Query("SELECT CASE WHEN e.isRenewable = true THEN e.renewableType ELSE '일반 전력' END, " +
           "SUM(e.totalCo2Equivalent) " +
           "FROM ElectricityUsage e " +
           "WHERE e.memberId = :memberId AND e.reportingYear = :year " +
           "GROUP BY CASE WHEN e.isRenewable = true THEN e.renewableType ELSE '일반 전력' END " +
           "ORDER BY SUM(e.totalCo2Equivalent) DESC")
    List<Object[]> findRenewableEmissions(@Param("memberId") Long memberId, @Param("year") Integer year);

    @Query("SELECT e.facilityLocation, SUM(e.totalCo2Equivalent) " +
           "FROM ElectricityUsage e " +
           "WHERE e.memberId = :memberId AND e.reportingYear = :year " +
           "GROUP BY e.facilityLocation " +
           "ORDER BY SUM(e.totalCo2Equivalent) DESC")
    List<Object[]> findFacilityEmissions(@Param("memberId") Long memberId, @Param("year") Integer year);

    @Query("SELECT e.companyId, SUM(e.totalCo2Equivalent) " +
           "FROM ElectricityUsage e " +
           "WHERE e.memberId = :memberId AND e.reportingYear = :year " +
           "GROUP BY e.companyId " +
           "ORDER BY SUM(e.totalCo2Equivalent) DESC")
    List<Object[]> findPartnerEmissions(@Param("memberId") Long memberId, @Param("year") Integer year);

    // 대시보드 통계용
    @Query("SELECT COUNT(e), SUM(e.totalCo2Equivalent), " +
           "SUM(CASE WHEN e.createdAt >= :startDate THEN 1 ELSE 0 END) " +
           "FROM ElectricityUsage e " +
           "WHERE e.memberId = :memberId")
    Object[] findDashboardStats(@Param("memberId") Long memberId, @Param("startDate") java.time.LocalDateTime startDate);

    // 헬퍼 메서드들
    default Map<String, BigDecimal> getMonthlyEmissionsMap(Long memberId, Integer year) {
        List<Object[]> results = findMonthlyEmissions(memberId, year);
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        for (Object[] result : results) {
            Integer month = (Integer) result[0];
            BigDecimal emission = (BigDecimal) result[1];
            map.put(month.toString(), emission != null ? emission : BigDecimal.ZERO);
        }
        return map;
    }

    default Map<String, BigDecimal> getRenewableEmissionsMap(Long memberId, Integer year) {
        List<Object[]> results = findRenewableEmissions(memberId, year);
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        for (Object[] result : results) {
            String renewableType = (String) result[0];
            BigDecimal emission = (BigDecimal) result[1];
            map.put(renewableType, emission != null ? emission : BigDecimal.ZERO);
        }
        return map;
    }

    default Map<String, BigDecimal> getFacilityEmissionsMap(Long memberId, Integer year) {
        List<Object[]> results = findFacilityEmissions(memberId, year);
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        for (Object[] result : results) {
            String facilityLocation = (String) result[0];
            BigDecimal emission = (BigDecimal) result[1];
            map.put(facilityLocation, emission != null ? emission : BigDecimal.ZERO);
        }
        return map;
    }

    default Map<String, BigDecimal> getPartnerEmissionsMap(Long memberId, Integer year) {
        List<Object[]> results = findPartnerEmissions(memberId, year);
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        for (Object[] result : results) {
            String companyId = (String) result[0];
            BigDecimal emission = (BigDecimal) result[1];
            map.put(companyId, emission != null ? emission : BigDecimal.ZERO);
        }
        return map;
    }
}
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
