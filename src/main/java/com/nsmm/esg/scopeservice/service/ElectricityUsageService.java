package com.nsmm.esg.scopeservice.service;

import com.nsmm.esg.scopeservice.dto.ElectricityUsageRequest;
import com.nsmm.esg.scopeservice.dto.ElectricityUsageResponse;
import com.nsmm.esg.scopeservice.dto.ScopeEmissionSummaryResponse;
import com.nsmm.esg.scopeservice.entity.ElectricityUsage;
import com.nsmm.esg.scopeservice.repository.ElectricityUsageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Scope 2 전력 사용 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ElectricityUsageService {

    private final ElectricityUsageRepository electricityUsageRepository;
    private final EmissionCalculationService calculationService;

    /**
     * 전력 사용 데이터 생성
     */
    @Transactional
    public ElectricityUsageResponse createElectricityUsage(Long memberId, ElectricityUsageRequest request) {
        try {
            ElectricityUsage entity = ElectricityUsage.builder()
                    .memberId(memberId)
                    .companyId(request.getCompanyId())
                    .reportingYear(request.getReportingYear())
                    .reportingMonth(request.getReportingMonth())
                    .facilityLocation(request.getFacilityLocation())
                    .combustionType(request.getCombustionType())
                    .fuelId(request.getFuelId())
                    .fuelName(request.getFuelName())
                    .unit(request.getUnit())
                    .distance(request.getDistance())
                    .isRenewable(request.getIsRenewable())
                    .renewableType(request.getRenewableType())
                    .usage(request.getUsage())
                    .notes(request.getNotes())
                    .build();

            // 배출량 계산
            BigDecimal totalCo2Equivalent = calculationService.calculateElectricityEmission(
                    entity.getUsage(), entity.getIsRenewable());
            entity.updateEmissions(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, totalCo2Equivalent);

            ElectricityUsage saved = electricityUsageRepository.save(entity);
            return ElectricityUsageResponse.from(saved);
        } catch (Exception e) {
            log.error("전력 사용 데이터 생성 실패: memberId={}, error={}", memberId, e.getMessage());
            throw new RuntimeException("전력 사용 데이터 생성에 실패했습니다.", e);
        }
    }

    /**
     * 전력 사용 데이터 수정
     */
    @Transactional
    public ElectricityUsageResponse updateElectricityUsage(Long memberId, Long id, ElectricityUsageRequest request) {
        try {
            ElectricityUsage entity = electricityUsageRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("전력 사용 데이터를 찾을 수 없습니다: " + id));

            if (!entity.getMemberId().equals(memberId)) {
                throw new IllegalArgumentException("권한이 없습니다.");
            }

            entity.updateFromScopeModal(
                    request.getCompanyId(),
                    request.getReportingYear(),
                    request.getReportingMonth(),
                    request.getFacilityLocation(),
                    request.getCombustionType(),
                    request.getFuelId(),
                    request.getFuelName(),
                    request.getUnit(),
                    request.getDistance(),
                    request.getIsRenewable(),
                    request.getRenewableType(),
                    request.getUsage(),
                    request.getNotes()
            );

            // 배출량 재계산
            BigDecimal totalCo2Equivalent = calculationService.calculateElectricityEmission(
                    entity.getUsage(), entity.getIsRenewable());
            entity.updateEmissions(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, totalCo2Equivalent);

            ElectricityUsage saved = electricityUsageRepository.save(entity);
            return ElectricityUsageResponse.from(saved);
        } catch (Exception e) {
            log.error("전력 사용 데이터 수정 실패: id={}, error={}", id, e.getMessage());
            throw new RuntimeException("전력 사용 데이터 수정에 실패했습니다.", e);
        }
    }

    /**
     * 전력 사용 데이터 삭제
     */
    @Transactional
    public void deleteElectricityUsage(Long memberId, Long id) {
        try {
            ElectricityUsage entity = electricityUsageRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("전력 사용 데이터를 찾을 수 없습니다: " + id));

            if (!entity.getMemberId().equals(memberId)) {
                throw new IllegalArgumentException("권한이 없습니다.");
            }

            electricityUsageRepository.delete(entity);
        } catch (Exception e) {
            log.error("전력 사용 데이터 삭제 실패: id={}, error={}", id, e.getMessage());
            throw new RuntimeException("전력 사용 데이터 삭제에 실패했습니다.", e);
        }
    }

    /**
     * 전력 사용 데이터 상세 조회
     */
    @Transactional(readOnly = true)
    public ElectricityUsageResponse getElectricityUsage(Long memberId, Long id) {
        ElectricityUsage entity = electricityUsageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("전력 사용 데이터를 찾을 수 없습니다: " + id));

        if (!entity.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        return ElectricityUsageResponse.from(entity);
    }

    /**
     * 전력 사용 데이터 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<ElectricityUsageResponse> getElectricityUsages(Long memberId, Pageable pageable) {
        Page<ElectricityUsage> entities = electricityUsageRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable);
        return entities.map(ElectricityUsageResponse::from);
    }

    /**
     * 연도별 전력 사용 데이터 조회
     */
    @Transactional(readOnly = true)
    public Page<ElectricityUsageResponse> getElectricityUsagesByYear(Integer year, Pageable pageable) {
        Page<ElectricityUsage> entities = electricityUsageRepository.findByReportingYearOrderByCreatedAtDesc(year, pageable);
        return entities.map(ElectricityUsageResponse::from);
    }

    /**
     * 협력사별 전력 사용 데이터 조회 (프론트엔드 핵심 API)
     */
    @Transactional(readOnly = true)
    public List<ElectricityUsageResponse> getElectricityUsagesByPartner(Long memberId, String companyId, Integer year) {
        List<ElectricityUsage> entities = electricityUsageRepository.findByMemberIdAndCompanyIdAndReportingYear(memberId, companyId, year);
        return entities.stream()
                .map(ElectricityUsageResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 월별 배출량 집계
     */
    @Transactional(readOnly = true)
    public ScopeEmissionSummaryResponse getMonthlyEmissions(Long memberId, Integer year) {
        Map<String, BigDecimal> monthlyData = electricityUsageRepository.getMonthlyEmissionsMap(memberId, year);
        
        ScopeEmissionSummaryResponse response = new ScopeEmissionSummaryResponse();
        response.setMonthlyData(monthlyData);
        response.setTotalEmission(monthlyData.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        response.setYear(year);
        response.setScope("Scope 2 - 전력 사용");
        
        return response;
    }

    /**
     * 재생에너지별 배출량 집계
     */
    @Transactional(readOnly = true)
    public ScopeEmissionSummaryResponse getRenewableEmissions(Long memberId, Integer year) {
        Map<String, BigDecimal> renewableData = electricityUsageRepository.getRenewableEmissionsMap(memberId, year);
        
        ScopeEmissionSummaryResponse response = new ScopeEmissionSummaryResponse();
        response.setRenewableData(renewableData);
        response.setTotalEmission(renewableData.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        response.setYear(year);
        response.setScope("Scope 2 - 재생에너지별");
        
        return response;
    }

    /**
     * 시설별 배출량 집계
     */
    @Transactional(readOnly = true)
    public ScopeEmissionSummaryResponse getFacilityEmissions(Long memberId, Integer year) {
        Map<String, BigDecimal> facilityData = electricityUsageRepository.getFacilityEmissionsMap(memberId, year);
        
        ScopeEmissionSummaryResponse response = new ScopeEmissionSummaryResponse();
        response.setFacilityData(facilityData);
        response.setTotalEmission(facilityData.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        response.setYear(year);
        response.setScope("Scope 2 - 시설별");
        
        return response;
    }

    /**
     * 협력사별 배출량 집계
     */
    @Transactional(readOnly = true)
    public ScopeEmissionSummaryResponse getPartnerEmissions(Long memberId, Integer year) {
        Map<String, BigDecimal> partnerData = electricityUsageRepository.getPartnerEmissionsMap(memberId, year);
        
        ScopeEmissionSummaryResponse response = new ScopeEmissionSummaryResponse();
        response.setPartnerData(partnerData);
        response.setTotalEmission(partnerData.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        response.setYear(year);
        response.setScope("Scope 2 - 협력사별");
        
        return response;
    }

    /**
     * 대시보드 통계 조회
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats(Long memberId) {
        LocalDateTime lastMonth = LocalDateTime.now().minusMonths(1);
        Object[] stats = electricityUsageRepository.findDashboardStats(memberId, lastMonth);
        
        return Map.of(
                "totalRecords", stats[0] != null ? stats[0] : 0L,
                "totalEmissions", stats[1] != null ? stats[1] : BigDecimal.ZERO,
                "recentRecords", stats[2] != null ? stats[2] : 0L
        );
    }
}
