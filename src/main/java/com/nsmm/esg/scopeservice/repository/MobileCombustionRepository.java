package com.nsmm.esg.scopeservice.repository;

import com.nsmm.esg.scopeservice.entity.MobileCombustion;
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
import java.util.Optional;

@Repository
public interface MobileCombustionRepository extends JpaRepository<MobileCombustion, Long> {

    // 기본 조회 메서드들
    Page<MobileCombustion> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);
    
    List<MobileCombustion> findByMemberIdAndReportingYear(Long memberId, Integer reportingYear);
    
    List<MobileCombustion> findByMemberIdAndCompanyId(Long memberId, String companyId);
    
    List<MobileCombustion> findByMemberIdAndCompanyIdAndReportingYear(Long memberId, String companyId, Integer reportingYear);

    Page<MobileCombustion> findByReportingYearOrderByCreatedAtDesc(Integer reportingYear, Pageable pageable);

    // 집계 쿼리들
    @Query("SELECT m.reportingMonth, SUM(m.totalCo2Equivalent) " +
           "FROM MobileCombustion m " +
           "WHERE m.memberId = :memberId AND m.reportingYear = :year " +
           "GROUP BY m.reportingMonth " +
           "ORDER BY m.reportingMonth")
    List<Object[]> findMonthlyEmissions(@Param("memberId") Long memberId, @Param("year") Integer year);

    @Query("SELECT m.fuelName, SUM(m.totalCo2Equivalent) " +
           "FROM MobileCombustion m " +
           "WHERE m.memberId = :memberId AND m.reportingYear = :year " +
           "GROUP BY m.fuelName " +
           "ORDER BY SUM(m.totalCo2Equivalent) DESC")
    List<Object[]> findFuelEmissions(@Param("memberId") Long memberId, @Param("year") Integer year);

    @Query("SELECT m.facilityLocation, SUM(m.totalCo2Equivalent) " +
           "FROM MobileCombustion m " +
           "WHERE m.memberId = :memberId AND m.reportingYear = :year " +
           "GROUP BY m.facilityLocation " +
           "ORDER BY SUM(m.totalCo2Equivalent) DESC")
    List<Object[]> findFacilityEmissions(@Param("memberId") Long memberId, @Param("year") Integer year);

    @Query("SELECT m.companyId, SUM(m.totalCo2Equivalent) " +
           "FROM MobileCombustion m " +
           "WHERE m.memberId = :memberId AND m.reportingYear = :year " +
           "GROUP BY m.companyId " +
           "ORDER BY SUM(m.totalCo2Equivalent) DESC")
    List<Object[]> findPartnerEmissions(@Param("memberId") Long memberId, @Param("year") Integer year);

    @Query("SELECT m.combustionType, SUM(m.totalCo2Equivalent) " +
           "FROM MobileCombustion m " +
           "WHERE m.memberId = :memberId AND m.reportingYear = :year " +
           "GROUP BY m.combustionType " +
           "ORDER BY SUM(m.totalCo2Equivalent) DESC")
    List<Object[]> findVehicleEmissions(@Param("memberId") Long memberId, @Param("year") Integer year);

    // 대시보드 통계용
    @Query("SELECT COUNT(m), SUM(m.totalCo2Equivalent), " +
           "SUM(CASE WHEN m.createdAt >= :startDate THEN 1 ELSE 0 END) " +
           "FROM MobileCombustion m " +
           "WHERE m.memberId = :memberId")
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

    default Map<String, BigDecimal> getFuelEmissionsMap(Long memberId, Integer year) {
        List<Object[]> results = findFuelEmissions(memberId, year);
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        for (Object[] result : results) {
            String fuelName = (String) result[0];
            BigDecimal emission = (BigDecimal) result[1];
            map.put(fuelName, emission != null ? emission : BigDecimal.ZERO);
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

    default Map<String, BigDecimal> getVehicleEmissionsMap(Long memberId, Integer year) {
        List<Object[]> results = findVehicleEmissions(memberId, year);
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        for (Object[] result : results) {
            String combustionType = (String) result[0];
            BigDecimal emission = (BigDecimal) result[1];
            map.put(combustionType, emission != null ? emission : BigDecimal.ZERO);
        }
        return map;
    }
}
