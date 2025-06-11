package com.nsmm.esg.scopeservice.service;

import com.nsmm.esg.scopeservice.dto.SteamUsageRequest;
import com.nsmm.esg.scopeservice.dto.SteamUsageResponse;
import com.nsmm.esg.scopeservice.dto.ScopeEmissionSummaryResponse;
import com.nsmm.esg.scopeservice.entity.SteamUsage;
import com.nsmm.esg.scopeservice.repository.SteamUsageRepository;
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
 * Scope 2 스팀 사용 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SteamUsageService {

    private final SteamUsageRepository steamUsageRepository;
    private final EmissionCalculationService calculationService;

    /**
     * 스팀 사용 데이터 생성
     */
    @Transactional
    public SteamUsageResponse createSteamUsage(Long memberId, SteamUsageRequest request) {
        try {
            SteamUsage entity = SteamUsage.builder()
                    .memberId(memberId)
                    .companyId(request.getCompanyId())
                    .reportingYear(request.getReportingYear())
                    .reportingMonth(request.getReportingMonth())
                    .facilityName(request.getFacilityName())
                    .facilityLocation(request.getFacilityLocation())
                    .combustionType(request.getCombustionType())
                    .fuelId(request.getFuelId())
                    .fuelName(request.getFuelName())
                    .unit(request.getUnit())
                    .distance(request.getDistance())
                    .steamType(request.getSteamType())
                    .usage(request.getUsage())
                    .notes(request.getNotes())
                    .build();

            // 배출량 계산
            BigDecimal totalCo2Equivalent = calculationService.calculateSteamEmission(
                    entity.getUsage(), entity.getSteamType());
            entity.updateEmissions(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, totalCo2Equivalent);

            SteamUsage saved = steamUsageRepository.save(entity);
            return SteamUsageResponse.from(saved);
        } catch (Exception e) {
            log.error("스팀 사용 데이터 생성 실패: memberId={}, error={}", memberId, e.getMessage());
            throw new RuntimeException("스팀 사용 데이터 생성에 실패했습니다.", e);
        }
    }

    /**
     * 스팀 사용 데이터 수정
     */
    @Transactional
    public SteamUsageResponse updateSteamUsage(Long memberId, Long id, SteamUsageRequest request) {
        try {
            SteamUsage entity = steamUsageRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("스팀 사용 데이터를 찾을 수 없습니다: " + id));

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
                    null, // isRenewable - steam doesn't use this
                    null, // renewableType - steam doesn't use this
                    request.getSteamType(),
                    request.getUsage(),
                    request.getNotes()
            );

            // 배출량 재계산
            BigDecimal totalCo2Equivalent = calculationService.calculateSteamEmission(
                    entity.getUsage(), entity.getSteamType());
            entity.updateEmissions(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, totalCo2Equivalent);

            SteamUsage saved = steamUsageRepository.save(entity);
            return SteamUsageResponse.from(saved);
        } catch (Exception e) {
            log.error("스팀 사용 데이터 수정 실패: id={}, error={}", id, e.getMessage());
            throw new RuntimeException("스팀 사용 데이터 수정에 실패했습니다.", e);
        }
    }

    /**
     * 스팀 사용 데이터 삭제
     */
    @Transactional
    public void deleteSteamUsage(Long memberId, Long id) {
        try {
            SteamUsage entity = steamUsageRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("스팀 사용 데이터를 찾을 수 없습니다: " + id));

            if (!entity.getMemberId().equals(memberId)) {
                throw new IllegalArgumentException("권한이 없습니다.");
            }

            steamUsageRepository.delete(entity);
        } catch (Exception e) {
            log.error("스팀 사용 데이터 삭제 실패: id={}, error={}", id, e.getMessage());
            throw new RuntimeException("스팀 사용 데이터 삭제에 실패했습니다.", e);
        }
    }

    /**
     * 스팀 사용 데이터 상세 조회
     */
    @Transactional(readOnly = true)
    public SteamUsageResponse getSteamUsage(Long memberId, Long id) {
        SteamUsage entity = steamUsageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("스팀 사용 데이터를 찾을 수 없습니다: " + id));

        if (!entity.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        return SteamUsageResponse.from(entity);
    }

    /**
     * 스팀 사용 데이터 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<SteamUsageResponse> getSteamUsages(Long memberId, Pageable pageable) {
        Page<SteamUsage> entities = steamUsageRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable);
        return entities.map(SteamUsageResponse::from);
    }

    /**
     * 연도별 스팀 사용 데이터 조회
     */
    @Transactional(readOnly = true)
    public Page<SteamUsageResponse> getSteamUsagesByYear(Integer year, Pageable pageable) {
        Page<SteamUsage> entities = steamUsageRepository.findByReportingYearOrderByCreatedAtDesc(year, pageable);
        return entities.map(SteamUsageResponse::from);
    }

    /**
     * 협력사별 스팀 사용 데이터 조회 (프론트엔드 핵심 API)
     */
    @Transactional(readOnly = true)
    public List<SteamUsageResponse> getSteamUsagesByPartner(Long memberId, String companyId, Integer year) {
        List<SteamUsage> entities = steamUsageRepository.findByMemberIdAndCompanyIdAndReportingYear(memberId, companyId, year);
        return entities.stream()
                .map(SteamUsageResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 월별 배출량 집계
     */
    @Transactional(readOnly = true)
    public ScopeEmissionSummaryResponse getMonthlyEmissions(Long memberId, Integer year) {
        Map<String, BigDecimal> monthlyData = steamUsageRepository.getMonthlyEmissionsMap(memberId, year);
        
        ScopeEmissionSummaryResponse response = new ScopeEmissionSummaryResponse();
        response.setMonthlyData(monthlyData);
        response.setTotalEmission(monthlyData.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        response.setYear(year);
        response.setScope("Scope 2 - 스팀 사용");
        
        return response;
    }

    /**
     * 스팀 타입별 배출량 집계
     */
    @Transactional(readOnly = true)
    public ScopeEmissionSummaryResponse getSteamTypeEmissions(Long memberId, Integer year) {
        Map<String, BigDecimal> steamTypeData = steamUsageRepository.getSteamTypeEmissionsMap(memberId, year);
        
        ScopeEmissionSummaryResponse response = new ScopeEmissionSummaryResponse();
        response.setSteamTypeData(steamTypeData);
        response.setTotalEmission(steamTypeData.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        response.setYear(year);
        response.setScope("Scope 2 - 스팀 타입별");
        
        return response;
    }

    /**
     * 시설별 배출량 집계
     */
    @Transactional(readOnly = true)
    public ScopeEmissionSummaryResponse getFacilityEmissions(Long memberId, Integer year) {
        Map<String, BigDecimal> facilityData = steamUsageRepository.getFacilityEmissionsMap(memberId, year);
        
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
        Map<String, BigDecimal> partnerData = steamUsageRepository.getPartnerEmissionsMap(memberId, year);
        
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
        Object[] stats = steamUsageRepository.findDashboardStats(memberId, lastMonth);
        
        return Map.of(
                "totalRecords", stats[0] != null ? stats[0] : 0L,
                "totalEmissions", stats[1] != null ? stats[1] : BigDecimal.ZERO,
                "recentRecords", stats[2] != null ? stats[2] : 0L
        );
    }

    /**
     * 스팀 사용 데이터 상세 조회 (Controller 호환)
     */
    @Transactional(readOnly = true)
    public SteamUsageResponse getById(Long id, Long memberId) {
        return getSteamUsage(memberId, id);
    }

    /**
     * 협력사별 데이터 조회 (Controller 호환)
     */
    @Transactional(readOnly = true)
    public List<SteamUsageResponse> getByPartnerAndYear(Long memberId, String companyId, Integer year) {
        return getSteamUsagesByPartner(memberId, companyId, year);
    }

    /**
     * 협력사별 전체 데이터 조회 (Controller 호환)
     */
    @Transactional(readOnly = true)
    public List<SteamUsageResponse> getByPartner(Long memberId, String companyId) {
        List<SteamUsage> entities = steamUsageRepository.findByMemberIdAndCompanyId(memberId, companyId);
        return entities.stream()
                .map(SteamUsageResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 전체 데이터 조회 (Controller 호환)
     */
    @Transactional(readOnly = true)
    public List<SteamUsageResponse> getAllByMember(Long memberId) {
        List<SteamUsage> entities = steamUsageRepository.findByMemberIdAndReportingYear(memberId, null);
        return entities.stream()
                .map(SteamUsageResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 연도별 데이터 조회 (Controller 호환)
     */
    @Transactional(readOnly = true)
    public List<SteamUsageResponse> getByYear(Long memberId, Integer year) {
        List<SteamUsage> entities = steamUsageRepository.findByMemberIdAndReportingYear(memberId, year);
        return entities.stream()
                .map(SteamUsageResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 월별 배출량 집계 (Controller 호환)
     */
    @Transactional(readOnly = true)
    public List<ScopeEmissionSummaryResponse> getMonthlyEmissionSummary(Long memberId, Integer year) {
        Map<String, BigDecimal> monthlyData = steamUsageRepository.getMonthlyEmissionsMap(memberId, year);
        
        return monthlyData.entrySet().stream()
                .map(entry -> {
                    ScopeEmissionSummaryResponse response = new ScopeEmissionSummaryResponse();
                    response.setCategory(entry.getKey());
                    response.setTotalEmission(entry.getValue());
                    return response;
                })
                .collect(Collectors.toList());
    }

    /**
     * 시설별 배출량 집계 (Controller 호환)
     */
    @Transactional(readOnly = true)
    public List<ScopeEmissionSummaryResponse> getEmissionSummaryByFacility(Long memberId, Integer year) {
        Map<String, BigDecimal> facilityData = steamUsageRepository.getFacilityEmissionsMap(memberId, year);
        
        return facilityData.entrySet().stream()
                .map(entry -> {
                    ScopeEmissionSummaryResponse response = new ScopeEmissionSummaryResponse();
                    response.setCategory(entry.getKey());
                    response.setTotalEmission(entry.getValue());
                    return response;
                })
                .collect(Collectors.toList());
    }

    /**
     * 협력사별 배출량 집계 (Controller 호환)
     */
    @Transactional(readOnly = true)
    public List<ScopeEmissionSummaryResponse> getEmissionSummaryByPartner(Long memberId, Integer year) {
        Map<String, BigDecimal> partnerData = steamUsageRepository.getPartnerEmissionsMap(memberId, year);
        
        return partnerData.entrySet().stream()
                .map(entry -> {
                    ScopeEmissionSummaryResponse response = new ScopeEmissionSummaryResponse();
                    response.setCategory(entry.getKey());
                    response.setTotalEmission(entry.getValue());
                    return response;
                })
                .collect(Collectors.toList());
    }

    /**
     * 스팀 타입별 배출량 집계 (Controller 호환)
     */
    @Transactional(readOnly = true)
    public List<ScopeEmissionSummaryResponse> getEmissionSummaryBySteamType(Long memberId, Integer year) {
        Map<String, BigDecimal> steamTypeData = steamUsageRepository.getSteamTypeEmissionsMap(memberId, year);
        
        return steamTypeData.entrySet().stream()
                .map(entry -> {
                    ScopeEmissionSummaryResponse response = new ScopeEmissionSummaryResponse();
                    response.setCategory(entry.getKey());
                    response.setTotalEmission(entry.getValue());
                    return response;
                })
                .collect(Collectors.toList());
    }

    /**
     * 공급업체별 배출량 집계 (Controller 호환)
     */
    @Transactional(readOnly = true)
    public List<ScopeEmissionSummaryResponse> getEmissionSummaryBySupplier(Long memberId, Integer year) {
        // 현재 repository에 supplier 메서드가 없으므로 빈 리스트 반환
        return List.of();
    }

    /**
     * 연도별 총 배출량 조회 (Controller 호환)
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalEmissionByYear(Long memberId, Integer year, String partnerCompanyId) {
        List<SteamUsage> entities;
        if (partnerCompanyId != null) {
            entities = steamUsageRepository.findByMemberIdAndCompanyIdAndReportingYear(memberId, partnerCompanyId, year);
        } else {
            entities = steamUsageRepository.findByMemberIdAndReportingYear(memberId, year);
        }
        
        return entities.stream()
                .map(SteamUsage::getTotalCo2Equivalent)
                .filter(emission -> emission != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 대시보드 통계 조회 (Controller 호환)
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats(Long memberId, Integer year) {
        LocalDateTime lastMonth = LocalDateTime.now().minusMonths(1);
        Object[] stats = steamUsageRepository.findDashboardStats(memberId, lastMonth);
        
        return Map.of(
                "totalRecords", stats[0] != null ? stats[0] : 0L,
                "totalEmissions", stats[1] != null ? stats[1] : BigDecimal.ZERO,
                "recentRecords", stats[2] != null ? stats[2] : 0L,
                "year", year
        );
    }
}
