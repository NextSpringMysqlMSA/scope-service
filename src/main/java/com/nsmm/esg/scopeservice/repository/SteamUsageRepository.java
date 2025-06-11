package com.nsmm.esg.scopeservice.repository;

import com.nsmm.esg.scopeservice.entity.SteamUsage;
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
public interface SteamUsageRepository extends JpaRepository<SteamUsage, Long> {

    // 기본 조회 메서드들
    Page<SteamUsage> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    List<SteamUsage> findByMemberIdAndReportingYear(Long memberId, Integer reportingYear);

    List<SteamUsage> findByMemberIdAndCompanyId(Long memberId, String companyId);

    List<SteamUsage> findByMemberIdAndCompanyIdAndReportingYear(Long memberId, String companyId, Integer reportingYear);

    Page<SteamUsage> findByReportingYearOrderByCreatedAtDesc(Integer reportingYear, Pageable pageable);

    // 집계 쿼리들
    @Query("SELECT s.reportingMonth, SUM(s.totalCo2Equivalent) " +
            "FROM SteamUsage s " +
            "WHERE s.memberId = :memberId AND s.reportingYear = :year " +
            "GROUP BY s.reportingMonth " +
            "ORDER BY s.reportingMonth")
    List<Object[]> findMonthlyEmissions(@Param("memberId") Long memberId, @Param("year") Integer year);

    @Query("SELECT s.steamType, SUM(s.totalCo2Equivalent) " +
            "FROM SteamUsage s " +
            "WHERE s.memberId = :memberId AND s.reportingYear = :year " +
            "GROUP BY s.steamType " +
            "ORDER BY SUM(s.totalCo2Equivalent) DESC")
    List<Object[]> findSteamTypeEmissions(@Param("memberId") Long memberId, @Param("year") Integer year);

    @Query("SELECT s.facilityLocation, SUM(s.totalCo2Equivalent) " +
            "FROM SteamUsage s " +
            "WHERE s.memberId = :memberId AND s.reportingYear = :year " +
            "GROUP BY s.facilityLocation " +
            "ORDER BY SUM(s.totalCo2Equivalent) DESC")
    List<Object[]> findFacilityEmissions(@Param("memberId") Long memberId, @Param("year") Integer year);

    @Query("SELECT s.companyId, SUM(s.totalCo2Equivalent) " +
            "FROM SteamUsage s " +
            "WHERE s.memberId = :memberId AND s.reportingYear = :year " +
            "GROUP BY s.companyId " +
            "ORDER BY SUM(s.totalCo2Equivalent) DESC")
    List<Object[]> findPartnerEmissions(@Param("memberId") Long memberId, @Param("year") Integer year);

    // 대시보드 통계용
    @Query("SELECT COUNT(s), SUM(s.totalCo2Equivalent), " +
            "SUM(CASE WHEN s.createdAt >= :startDate THEN 1 ELSE 0 END) " +
            "FROM SteamUsage s " +
            "WHERE s.memberId = :memberId")
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

    default Map<String, BigDecimal> getSteamTypeEmissionsMap(Long memberId, Integer year) {
        List<Object[]> results = findSteamTypeEmissions(memberId, year);
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        for (Object[] result : results) {
            String steamType = (String) result[0];
            BigDecimal emission = (BigDecimal) result[1];
            map.put(steamType, emission != null ? emission : BigDecimal.ZERO);
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
