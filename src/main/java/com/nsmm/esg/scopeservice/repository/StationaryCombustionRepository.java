package com.nsmm.esg.scopeservice.repository;

import com.nsmm.esg.scopeservice.entity.StationaryCombustion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 고정연소 데이터 레포지토리
 * 컨트롤러와 서비스에서 실제로 사용하는 메서드만 포함
 */
@Repository
public interface StationaryCombustionRepository extends JpaRepository<StationaryCombustion, Long> {

    // =============================================================================
    // 기본 조회 메서드 (컨트롤러 API 대응)
    // =============================================================================

    /**
     * 회원별 전체 조회 (GET /)
     */
    List<StationaryCombustion> findByMemberId(Long memberId);

    /**
     * 회원별 연도별 조회 (GET /year/{year})
     */
    List<StationaryCombustion> findByMemberIdAndReportingYear(Long memberId, Integer reportingYear);

    /**
     * 회원별 협력사별 조회 (GET /partner/{partnerCompanyId})
     */
    List<StationaryCombustion> findByMemberIdAndCompanyId(Long memberId, String companyId);

    /**
     * 회원별 협력사별 연도별 조회 (GET /partner/{partnerCompanyId}/year/{year}) - 핵심
     */
    List<StationaryCombustion> findByMemberIdAndCompanyIdAndReportingYear(
            Long memberId, String companyId, Integer reportingYear);

    // =============================================================================
    // 집계 쿼리 (차트 및 통계용)
    // =============================================================================

    /**
     * 월별 배출량 집계
     */
    @Query("SELECT sc.reportingMonth, SUM(sc.totalCo2Equivalent) " +
           "FROM StationaryCombustion sc " +
           "WHERE sc.memberId = :memberId " +
           "AND sc.reportingYear = :year " +
           "AND (:partnerCompanyId IS NULL OR sc.companyId = :partnerCompanyId) " +
           "GROUP BY sc.reportingMonth " +
           "ORDER BY sc.reportingMonth")
    List<Object[]> findMonthlyEmissionSummary(
            @Param("memberId") Long memberId,
            @Param("year") Integer year,
            @Param("partnerCompanyId") String partnerCompanyId);

    /**
     * 연료별 배출량 집계
     */
    @Query("SELECT sc.fuelName, SUM(sc.totalCo2Equivalent) " +
           "FROM StationaryCombustion sc " +
           "WHERE sc.memberId = :memberId " +
           "AND sc.reportingYear = :year " +
           "AND (:partnerCompanyId IS NULL OR sc.companyId = :partnerCompanyId) " +
           "GROUP BY sc.fuelName " +
           "ORDER BY SUM(sc.totalCo2Equivalent) DESC")
    List<Object[]> findEmissionSummaryByFuel(
            @Param("memberId") Long memberId,
            @Param("year") Integer year,
            @Param("partnerCompanyId") String partnerCompanyId);

    /**
     * 시설별 배출량 집계
     */
    @Query("SELECT sc.facilityLocation, SUM(sc.totalCo2Equivalent) " +
           "FROM StationaryCombustion sc " +
           "WHERE sc.memberId = :memberId " +
           "AND sc.reportingYear = :year " +
           "AND (:partnerCompanyId IS NULL OR sc.companyId = :partnerCompanyId) " +
           "GROUP BY sc.facilityLocation " +
           "ORDER BY SUM(sc.totalCo2Equivalent) DESC")
    List<Object[]> findEmissionSummaryByFacility(
            @Param("memberId") Long memberId,
            @Param("year") Integer year,
            @Param("partnerCompanyId") String partnerCompanyId);

    /**
     * 협력사별 배출량 집계
     */
    @Query("SELECT sc.companyId, SUM(sc.totalCo2Equivalent) " +
           "FROM StationaryCombustion sc " +
           "WHERE sc.memberId = :memberId " +
           "AND sc.reportingYear = :year " +
           "GROUP BY sc.companyId " +
           "ORDER BY SUM(sc.totalCo2Equivalent) DESC")
    List<Object[]> findEmissionSummaryByPartner(
            @Param("memberId") Long memberId,
            @Param("year") Integer year);

    /**
     * 연도별 총 배출량 (회원별)
     */
    @Query("SELECT COALESCE(SUM(sc.totalCo2Equivalent), 0) " +
           "FROM StationaryCombustion sc " +
           "WHERE sc.memberId = :memberId " +
           "AND sc.reportingYear = :year")
    BigDecimal getTotalEmissionByMemberAndYear(@Param("memberId") Long memberId, @Param("year") Integer year);

    /**
     * 연도별 총 배출량 (회원별 + 협력사별)
     */
    @Query("SELECT COALESCE(SUM(sc.totalCo2Equivalent), 0) " +
           "FROM StationaryCombustion sc " +
           "WHERE sc.memberId = :memberId " +
           "AND sc.companyId = :partnerCompanyId " +
           "AND sc.reportingYear = :year")
    BigDecimal getTotalEmissionByMemberAndPartnerAndYear(
            @Param("memberId") Long memberId, 
            @Param("partnerCompanyId") String partnerCompanyId, 
            @Param("year") Integer year);

    // =============================================================================
    // 헬퍼 메서드 (집계 데이터를 Map으로 변환하기 위한 default 메서드들)
    // =============================================================================

    default Map<Integer, BigDecimal> getMonthlyEmissionSummary(Long memberId, Integer year, String partnerCompanyId) {
        List<Object[]> results = findMonthlyEmissionSummary(memberId, year, partnerCompanyId);
        return results.stream()
                .collect(java.util.stream.Collectors.toMap(
                        row -> (Integer) row[0],
                        row -> (BigDecimal) row[1]
                ));
    }

    default Map<String, BigDecimal> getEmissionSummaryByFuel(Long memberId, Integer year, String partnerCompanyId) {
        List<Object[]> results = findEmissionSummaryByFuel(memberId, year, partnerCompanyId);
        return results.stream()
                .collect(java.util.stream.Collectors.toMap(
                        row -> (String) row[0],
                        row -> (BigDecimal) row[1]
                ));
    }

    default Map<String, BigDecimal> getEmissionSummaryByFacility(Long memberId, Integer year, String partnerCompanyId) {
        List<Object[]> results = findEmissionSummaryByFacility(memberId, year, partnerCompanyId);
        return results.stream()
                .collect(java.util.stream.Collectors.toMap(
                        row -> (String) row[0],
                        row -> (BigDecimal) row[1]
                ));
    }

    default Map<String, BigDecimal> getEmissionSummaryByPartner(Long memberId, Integer year) {
        List<Object[]> results = findEmissionSummaryByPartner(memberId, year);
        return results.stream()
                .collect(java.util.stream.Collectors.toMap(
                        row -> (String) row[0],
                        row -> (BigDecimal) row[1]
                ));
    }

}
