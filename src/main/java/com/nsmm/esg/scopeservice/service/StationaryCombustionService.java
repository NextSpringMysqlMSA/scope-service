package com.nsmm.esg.scopeservice.service;

import com.nsmm.esg.scopeservice.dto.StationaryCombustionRequest;
import com.nsmm.esg.scopeservice.dto.StationaryCombustionResponse;
import com.nsmm.esg.scopeservice.dto.ScopeEmissionSummaryResponse;
import com.nsmm.esg.scopeservice.entity.StationaryCombustion;
import com.nsmm.esg.scopeservice.repository.FuelTypeRepository;
import com.nsmm.esg.scopeservice.repository.StationaryCombustionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Scope 1 고정연소 서비스
 * 컨트롤러 API에 정확히 대응하는 메서드만 포함
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StationaryCombustionService {

    private final StationaryCombustionRepository stationaryCombustionRepository;
    private final FuelTypeRepository fuelTypeRepository;
    private final EmissionCalculationService emissionCalculationService;

    // =============================================================================
    // 핵심 CRUD 메서드 (컨트롤러 1:1 대응)
    // =============================================================================

    /**
     * 고정연소 데이터 생성 (POST /)
     */
    @Transactional
    public StationaryCombustionResponse createStationaryCombustion(StationaryCombustionRequest request) {
        try {
            // 1. Request 검증
            validateRequest(request);

            // 2. 엔티티 생성
            StationaryCombustion entity = StationaryCombustion.builder()
                    .memberId(request.getMemberId())
                    .companyId(request.getCompanyId())
                    .reportingYear(request.getReportingYear())
                    .reportingMonth(request.getReportingMonth())
                    .facilityName(request.getFacilityName())
                    .facilityLocation(request.getFacilityLocation())
                    .combustionType(request.getCombustionType())
                    .fuelId(request.getFuelId())
                    .fuelName(request.getFuelName())
                    .fuelUsage(request.getFuelUsage())
                    .unit(request.getUnit())
                    .createdBy(request.getCreatedBy())
                    .notes(request.getNotes())
                    .build();

            // 3. 배출량 계산 및 설정
            calculateAndSetEmissions(entity);

            // 4. 저장
            StationaryCombustion saved = stationaryCombustionRepository.save(entity);
            log.info("고정연소 데이터 생성 완료 - ID: {}", saved.getId());

            return convertToResponse(saved);

        } catch (Exception e) {
            log.error("고정연소 데이터 생성 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("고정연소 데이터 생성 실패", e);
        }
    }

    /**
     * 고정연소 데이터 수정 (PUT /{id})
     */
    @Transactional
    public StationaryCombustionResponse updateStationaryCombustion(Long id, Long memberId, StationaryCombustionRequest request) {
        try {
            // 1. 기존 데이터 조회 및 권한 확인
            StationaryCombustion entity = stationaryCombustionRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("고정연소 데이터를 찾을 수 없습니다: " + id));

            if (!entity.getMemberId().equals(memberId)) {
                throw new IllegalArgumentException("해당 데이터에 대한 접근 권한이 없습니다.");
            }

            // 2. Request 검증
            validateRequest(request);

            // 3. 엔티티 업데이트
            entity.updateFromRequest(request);

            // 4. 배출량 재계산
            calculateAndSetEmissions(entity);

            // 5. 저장
            StationaryCombustion updated = stationaryCombustionRepository.save(entity);
            log.info("고정연소 데이터 수정 완료 - ID: {}", id);

            return convertToResponse(updated);

        } catch (Exception e) {
            log.error("고정연소 데이터 수정 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("고정연소 데이터 수정 실패", e);
        }
    }

    /**
     * 고정연소 데이터 삭제 (DELETE /{id})
     */
    @Transactional
    public void deleteStationaryCombustion(Long id, Long memberId) {
        try {
            StationaryCombustion entity = stationaryCombustionRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("고정연소 데이터를 찾을 수 없습니다: " + id));

            if (!entity.getMemberId().equals(memberId)) {
                throw new IllegalArgumentException("해당 데이터에 대한 접근 권한이 없습니다.");
            }

            stationaryCombustionRepository.delete(entity);
            log.info("고정연소 데이터 삭제 완료 - ID: {}", id);

        } catch (Exception e) {
            log.error("고정연소 데이터 삭제 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("고정연소 데이터 삭제 실패", e);
        }
    }

    /**
     * 고정연소 데이터 상세 조회 (GET /{id})
     */
    @Transactional(readOnly = true)
    public StationaryCombustionResponse getById(Long id, Long memberId) {
        StationaryCombustion entity = stationaryCombustionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("고정연소 데이터를 찾을 수 없습니다: " + id));

        if (!entity.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("해당 데이터에 대한 접근 권한이 없습니다.");
        }

        return convertToResponse(entity);
    }

    // =============================================================================
    // 협력사별 조회 메서드 (핵심)
    // =============================================================================

    /**
     * 협력사별 연도별 고정연소 데이터 조회 (GET /partner/{companyId}/year/{year})
     */
    @Transactional(readOnly = true)
    public List<StationaryCombustionResponse> getByPartnerAndYear(
            Long memberId, String companyId, Integer year) {
        List<StationaryCombustion> entities = stationaryCombustionRepository
                .findByMemberIdAndCompanyIdAndReportingYear(memberId, companyId, year);
        
        return entities.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 협력사별 전체 고정연소 데이터 조회 (GET /partner/{companyId})
     */
    @Transactional(readOnly = true)
    public List<StationaryCombustionResponse> getByPartner(
            Long memberId, String companyId) {
        List<StationaryCombustion> entities = stationaryCombustionRepository
                .findByMemberIdAndCompanyId(memberId, companyId);
        
        return entities.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // =============================================================================
    // 기본 조회 메서드
    // =============================================================================

    /**
     * 회원별 전체 고정연소 데이터 조회 (GET /)
     */
    @Transactional(readOnly = true)
    public List<StationaryCombustionResponse> getAllByMember(Long memberId) {
        List<StationaryCombustion> entities = stationaryCombustionRepository.findByMemberId(memberId);
        
        return entities.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 회원별 연도별 고정연소 데이터 조회 (GET /year/{year})
     */
    @Transactional(readOnly = true)
    public List<StationaryCombustionResponse> getByYear(Long memberId, Integer year) {
        List<StationaryCombustion> entities = stationaryCombustionRepository
                .findByMemberIdAndReportingYear(memberId, year);
        
        return entities.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // =============================================================================
    // 집계 및 통계 메서드 (차트용)
    // =============================================================================

    /**
     * 월별 배출량 집계
     */
    @Transactional(readOnly = true)
    public List<ScopeEmissionSummaryResponse> getMonthlyEmissionSummary(Long memberId, Integer year, String companyId) {
        Map<Integer, BigDecimal> monthlyData = stationaryCombustionRepository.getMonthlyEmissionSummary(memberId, year, companyId);
        
        return monthlyData.entrySet().stream()
                .map(entry -> ScopeEmissionSummaryResponse.builder()
                        .memberId(memberId)
                        .year(year)
                        .month(entry.getKey())
                        .companyId(companyId)
                        .aggregationType("MONTHLY")
                        .totalEmission(entry.getValue())
                        .unit("tCO2eq")
                        .calculatedAt(java.time.LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 연료별 배출량 집계
     */
    @Transactional(readOnly = true)
    public List<ScopeEmissionSummaryResponse> getEmissionSummaryByFuel(Long memberId, Integer year, String companyId) {
        Map<String, BigDecimal> fuelData = stationaryCombustionRepository.getEmissionSummaryByFuel(memberId, year, companyId);
        
        return fuelData.entrySet().stream()
                .map(entry -> ScopeEmissionSummaryResponse.builder()
                        .memberId(memberId)
                        .year(year)
                        .companyId(companyId)
                        .aggregationType("BY_FUEL")
                        .totalEmission(entry.getValue())
                        .unit("tCO2eq")
                        .calculatedAt(java.time.LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 시설별 배출량 집계
     */
    @Transactional(readOnly = true)
    public List<ScopeEmissionSummaryResponse> getEmissionSummaryByFacility(Long memberId, Integer year, String companyId) {
        Map<String, BigDecimal> facilityData = stationaryCombustionRepository.getEmissionSummaryByFacility(memberId, year, companyId);
        
        return facilityData.entrySet().stream()
                .map(entry -> ScopeEmissionSummaryResponse.builder()
                        .memberId(memberId)
                        .year(year)
                        .companyId(companyId)
                        .aggregationType("BY_FACILITY")
                        .totalEmission(entry.getValue())
                        .unit("tCO2eq")
                        .calculatedAt(java.time.LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 협력사별 배출량 집계
     */
    @Transactional(readOnly = true)
    public List<ScopeEmissionSummaryResponse> getEmissionSummaryByPartner(Long memberId, Integer year) {
        Map<String, BigDecimal> partnerData = stationaryCombustionRepository.getEmissionSummaryByPartner(memberId, year);
        
        return partnerData.entrySet().stream()
                .map(entry -> ScopeEmissionSummaryResponse.builder()
                        .memberId(memberId)
                        .year(year)
                        .companyId(entry.getKey())
                        .aggregationType("BY_PARTNER")
                        .totalEmission(entry.getValue())
                        .unit("tCO2eq")
                        .calculatedAt(java.time.LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 연도별 총 배출량
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalEmissionByYear(Long memberId, Integer year, String companyId) {
        if (companyId != null) {
            return stationaryCombustionRepository.getTotalEmissionByMemberAndPartnerAndYear(memberId, companyId, year);
        } else {
            return stationaryCombustionRepository.getTotalEmissionByMemberAndYear(memberId, year);
        }
    }

    /**
     * 대시보드용 통계
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats(Long memberId, Integer year) {
        return Map.of(
                "totalEmission", getTotalEmissionByYear(memberId, year, null),
                "monthlyEmissions", getMonthlyEmissionSummary(memberId, year, null),
                "fuelTypeBreakdown", getEmissionSummaryByFuel(memberId, year, null),
                "facilityBreakdown", getEmissionSummaryByFacility(memberId, year, null)
        );
    }

    // =============================================================================
    // 내부 유틸리티 메서드
    // =============================================================================

    /**
     * Request 검증
     */
    private void validateRequest(StationaryCombustionRequest request) {
        if (request.getCompanyId() == null || request.getCompanyId().trim().isEmpty()) {
            throw new IllegalArgumentException("회사 ID는 필수입니다.");
        }
        if (request.getReportingYear() == null) {
            throw new IllegalArgumentException("보고 연도는 필수입니다.");
        }
        if (request.getReportingMonth() == null) {
            throw new IllegalArgumentException("보고 월은 필수입니다.");
        }
        if (request.getFuelUsage() == null || request.getFuelUsage().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("연료 사용량은 0보다 커야 합니다.");
        }
    }

    /**
     * 배출량 계산 및 엔티티에 설정
     */
    private void calculateAndSetEmissions(StationaryCombustion entity) {
        try {
            // EmissionCalculationService를 통한 배출량 계산
            var emissionResult = emissionCalculationService.calculateScope1StationaryEmission(
                    entity.getFuelId(), entity.getFuelUsage(), entity.getReportingYear());
            
            entity.updateEmissions(
                    emissionResult.getCo2Emission(),
                    emissionResult.getCh4Emission(),
                    emissionResult.getN2oEmission(),
                    emissionResult.getTotalCo2Equivalent()
            );
            
            log.debug("고정연소 배출량 계산 완료 - 연료: {}, 사용량: {}, 총 배출량: {}", 
                    entity.getFuelName(), entity.getFuelUsage(), emissionResult.getTotalCo2Equivalent());
            
        } catch (Exception e) {
            log.warn("배출량 계산 실패, 기본값 사용: {}", e.getMessage());
            // 계산 실패 시 0으로 설정
            entity.updateEmissions(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
    }

    /**
     * 엔티티를 응답 DTO로 변환
     */
    private StationaryCombustionResponse convertToResponse(StationaryCombustion entity) {
        return StationaryCombustionResponse.builder()
                .id(entity.getId())
                .companyId(entity.getCompanyId())
                .reportingYear(entity.getReportingYear())
                .reportingMonth(entity.getReportingMonth())
                .facilityLocation(entity.getFacilityLocation())
                .combustionType(entity.getCombustionType())
                .fuelId(entity.getFuelId())
                .fuelName(entity.getFuelName())
                .fuelUsage(entity.getFuelUsage())
                .unit(entity.getUnit())
                .co2Emission(entity.getCo2Emission())
                .ch4Emission(entity.getCh4Emission())
                .n2oEmission(entity.getN2oEmission())
                .totalCo2Equivalent(entity.getTotalCo2Equivalent())
                .calculatedAt(entity.getCalculatedAt())
                .createdBy(entity.getCreatedBy())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
