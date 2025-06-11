package com.nsmm.esg.scopeservice.repository;

import com.nsmm.esg.scopeservice.entity.MobileCombustion;
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
public interface MobileCombustionRepository extends JpaRepository<MobileCombustion, Long> {

    /**
     * 회원별 이동연소 데이터 조회
     */
    List<MobileCombustion> findByMemberIdOrderByYearDescMonthDesc(Long memberId);

    /**
     * 회원별 특정 연도 이동연소 데이터 조회
     */
    List<MobileCombustion> findByMemberIdAndYearOrderByMonthAsc(Long memberId, Integer year);

    /**
     * 회원별 특정 연도/월 이동연소 데이터 조회
     */
    List<MobileCombustion> findByMemberIdAndYearAndMonth(Long memberId, Integer year, Integer month);

    /**
     * 회원별 연도별 총 배출량 합계
     */
    @Query("SELECT SUM(m.totalEmission) FROM MobileCombustion m WHERE m.memberId = :memberId AND m.year = :year")
    BigDecimal sumTotalEmissionByMemberIdAndYear(@Param("memberId") Long memberId, @Param("year") Integer year);

    /**
     * 회원별 월별 총 배출량 합계
     */
    @Query("SELECT SUM(m.totalEmission) FROM MobileCombustion m WHERE m.memberId = :memberId AND m.year = :year AND m.month = :month")
    BigDecimal sumTotalEmissionByMemberIdAndYearAndMonth(@Param("memberId") Long memberId, @Param("year") Integer year, @Param("month") Integer month);

    /**
     * 차량 타입별 배출량 집계
     */
    @Query("SELECT m.vehicleType, SUM(m.totalEmission) FROM MobileCombustion m WHERE m.memberId = :memberId AND m.year = :year GROUP BY m.vehicleType")
    List<Object[]> sumEmissionByVehicleTypeAndMemberIdAndYear(@Param("memberId") Long memberId, @Param("year") Integer year);

    /**
     * 연료 타입별 배출량 집계
     */
    @Query("SELECT m.fuelType.name, SUM(m.totalEmission) FROM MobileCombustion m WHERE m.memberId = :memberId AND m.year = :year GROUP BY m.fuelType.name")
    List<Object[]> sumEmissionByFuelTypeAndMemberIdAndYear(@Param("memberId") Long memberId, @Param("year") Integer year);

    // 협력사별 조회 메서드들

    /**
     * 회원별 및 협력사별 이동연소 데이터 조회
     */
    List<MobileCombustion> findByMemberIdAndPartnerCompanyIdOrderByYearDescMonthDesc(Long memberId, String partnerCompanyId);

    /**
     * 회원별 및 협력사별 특정 연도 이동연소 데이터 조회
     */
    List<MobileCombustion> findByMemberIdAndPartnerCompanyIdAndYearOrderByMonthAsc(Long memberId, String partnerCompanyId, Integer year);

    /**
     * 회원별 및 협력사별 특정 연도/월 이동연소 데이터 조회
     */
    List<MobileCombustion> findByMemberIdAndPartnerCompanyIdAndYearAndMonth(Long memberId, String partnerCompanyId, Integer year, Integer month);

    /**
     * 회원별 및 협력사별 연도별 총 배출량 합계
     */
    @Query("SELECT SUM(m.totalEmission) FROM MobileCombustion m WHERE m.memberId = :memberId AND m.partnerCompanyId = :partnerCompanyId AND m.year = :year")
    BigDecimal sumTotalEmissionByMemberIdAndPartnerCompanyIdAndYear(@Param("memberId") Long memberId, @Param("partnerCompanyId") String partnerCompanyId, @Param("year") Integer year);

    /**
     * 회원별 및 협력사별 월별 총 배출량 합계
     */
    @Query("SELECT SUM(m.totalEmission) FROM MobileCombustion m WHERE m.memberId = :memberId AND m.partnerCompanyId = :partnerCompanyId AND m.year = :year AND m.month = :month")
    BigDecimal sumTotalEmissionByMemberIdAndPartnerCompanyIdAndYearAndMonth(@Param("memberId") Long memberId, @Param("partnerCompanyId") String partnerCompanyId, @Param("year") Integer year, @Param("month") Integer month);

    // Service에서 사용되는 추가 메서드들

    /**
     * 회사별 이동연소 데이터 조회 (생성일 역순)
     */
    @Query("SELECT m FROM MobileCombustion m WHERE m.companyId = :companyId ORDER BY m.createdAt DESC")
    Page<MobileCombustion> findByCompanyIdOrderByCreatedAtDesc(@Param("companyId") Long companyId, Pageable pageable);

    /**
     * 회사별 및 연도별 이동연소 데이터 조회
     */
    List<MobileCombustion> findByCompanyIdAndReportingYear(Long companyId, Integer reportingYear);

    /**
     * 회사별 및 연도별 총 CO2 등가량 합계
     */
    @Query("SELECT SUM(m.totalCo2Equivalent) FROM MobileCombustion m WHERE m.companyId = :companyId AND m.reportingYear = :year")
    Optional<BigDecimal> sumTotalCo2EquivalentByCompanyIdAndYear(@Param("companyId") Long companyId, @Param("year") Integer year);

    /**
     * 차량 타입별 배출량 합계
     */
    @Query("SELECT m.vehicleType, SUM(m.totalCo2Equivalent) FROM MobileCombustion m WHERE m.companyId = :companyId AND m.reportingYear = :year GROUP BY m.vehicleType")
    List<Object[]> sumEmissionByVehicleType(@Param("companyId") Long companyId, @Param("year") Integer year);
}
