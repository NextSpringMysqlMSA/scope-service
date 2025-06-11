package com.nsmm.esg.scopeservice.repository;

import com.nsmm.esg.scopeservice.entity.MobileCombustion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface MobileCombustionRepository extends JpaRepository<MobileCombustion, Long> {

    List<MobileCombustion> findByMemberId(Long memberId);

    List<MobileCombustion> findByMemberIdAndReportingYear(Long memberId, Integer reportingYear);

    List<MobileCombustion> findByMemberIdAndCompanyId(Long memberId, String companyId);

    List<MobileCombustion> findByMemberIdAndCompanyIdAndReportingYear(Long memberId, String companyId, Integer reportingYear);

    @Query("SELECT m.reportingMonth, COALESCE(SUM(m.totalCo2Equivalent), 0) FROM MobileCombustion m WHERE m.memberId = :memberId AND m.reportingYear = :year GROUP BY m.reportingMonth ORDER BY m.reportingMonth")
    List<Object[]> findMonthlyEmissions(@Param("memberId") Long memberId, @Param("year") Integer year);

    @Query("SELECT m.fuelName, COALESCE(SUM(m.totalCo2Equivalent), 0) FROM MobileCombustion m WHERE m.memberId = :memberId AND m.reportingYear = :year GROUP BY m.fuelName ORDER BY SUM(m.totalCo2Equivalent) DESC")
    List<Object[]> findFuelEmissions(@Param("memberId") Long memberId, @Param("year") Integer year);

    @Query("SELECT m.vehicleType, COALESCE(SUM(m.totalCo2Equivalent), 0) FROM MobileCombustion m WHERE m.memberId = :memberId AND m.reportingYear = :year GROUP BY m.vehicleType ORDER BY SUM(m.totalCo2Equivalent) DESC")
    List<Object[]> findVehicleEmissions(@Param("memberId") Long memberId, @Param("year") Integer year);

    @Query("SELECT m.companyId, COALESCE(SUM(m.totalCo2Equivalent), 0) FROM MobileCombustion m WHERE m.memberId = :memberId AND m.reportingYear = :year GROUP BY m.companyId ORDER BY SUM(m.totalCo2Equivalent) DESC")
    List<Object[]> findPartnerEmissions(@Param("memberId") Long memberId, @Param("year") Integer year);

    @Query("SELECT COALESCE(SUM(m.totalCo2Equivalent), 0) FROM MobileCombustion m WHERE m.memberId = :memberId AND m.reportingYear = :year")
    BigDecimal getTotalEmissionByMemberAndYear(@Param("memberId") Long memberId, @Param("year") Integer year);

    @Query("SELECT COALESCE(SUM(m.totalCo2Equivalent), 0) FROM MobileCombustion m WHERE m.memberId = :memberId AND m.companyId = :companyId AND m.reportingYear = :year")
    BigDecimal getTotalEmissionByMemberAndPartnerAndYear(@Param("memberId") Long memberId, @Param("companyId") String companyId, @Param("year") Integer year);

    @Query("SELECT m.reportingMonth, COALESCE(SUM(m.totalCo2Equivalent), 0) FROM MobileCombustion m WHERE m.memberId = :memberId AND m.reportingYear = :year AND m.companyId = :companyId GROUP BY m.reportingMonth ORDER BY m.reportingMonth")
    List<Object[]> findMonthlyEmissionsByPartner(@Param("memberId") Long memberId, @Param("year") Integer year, @Param("companyId") String companyId);

    @Query("SELECT m.fuelName, COALESCE(SUM(m.totalCo2Equivalent), 0) FROM MobileCombustion m WHERE m.memberId = :memberId AND m.reportingYear = :year AND m.companyId = :companyId GROUP BY m.fuelName ORDER BY SUM(m.totalCo2Equivalent) DESC")
    List<Object[]> findFuelEmissionsByPartner(@Param("memberId") Long memberId, @Param("year") Integer year, @Param("companyId") String companyId);

    @Query("SELECT m.vehicleType, COALESCE(SUM(m.totalCo2Equivalent), 0) FROM MobileCombustion m WHERE m.memberId = :memberId AND m.reportingYear = :year AND m.companyId = :companyId GROUP BY m.vehicleType ORDER BY SUM(m.totalCo2Equivalent) DESC")
    List<Object[]> findVehicleEmissionsByPartner(@Param("memberId") Long memberId, @Param("year") Integer year, @Param("companyId") String companyId);

    default Map<Integer, BigDecimal> getMonthlyEmissionSummary(Long memberId, Integer year, String companyId) {
        List<Object[]> results = (companyId != null) ? findMonthlyEmissionsByPartner(memberId, year, companyId) : findMonthlyEmissions(memberId, year);
        Map<Integer, BigDecimal> map = new LinkedHashMap<>();
        for (Object[] result : results) {
            map.put((Integer) result[0], (BigDecimal) result[1]);
        }
        return map;
    }

    default Map<String, BigDecimal> getEmissionSummaryByFuel(Long memberId, Integer year, String companyId) {
        List<Object[]> results = (companyId != null) ? findFuelEmissionsByPartner(memberId, year, companyId) : findFuelEmissions(memberId, year);
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        for (Object[] result : results) {
            map.put((String) result[0], (BigDecimal) result[1]);
        }
        return map;
    }

    default Map<String, BigDecimal> getEmissionSummaryByVehicle(Long memberId, Integer year, String companyId) {
        List<Object[]> results = (companyId != null) ? findVehicleEmissionsByPartner(memberId, year, companyId) : findVehicleEmissions(memberId, year);
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        for (Object[] result : results) {
            map.put((String) result[0], (BigDecimal) result[1]);
        }
        return map;
    }

    default Map<String, BigDecimal> getEmissionSummaryByPartner(Long memberId, Integer year) {
        List<Object[]> results = findPartnerEmissions(memberId, year);
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        for (Object[] result : results) {
            map.put((String) result[0], (BigDecimal) result[1]);
        }
        return map;
    }
}