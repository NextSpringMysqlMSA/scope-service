package com.nsmm.esg.scopeservice.service;

import com.nsmm.esg.scopeservice.dto.MobileCombustionRequest;
import com.nsmm.esg.scopeservice.dto.MobileCombustionResponse;
import com.nsmm.esg.scopeservice.dto.ScopeEmissionSummaryResponse;
import com.nsmm.esg.scopeservice.entity.MobileCombustion;
import com.nsmm.esg.scopeservice.repository.MobileCombustionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Scope 1 이동연소 서비스
 * 컨트롤러 API에 정확히 대응하는 메서드만 포함
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MobileCombustionService {

    private final MobileCombustionRepository mobileCombustionRepository;
    private final EmissionCalculationService emissionCalculationService;

    // =============================================================================
    // 핵심 CRUD 메서드 (컨트롤러 1:1 대응)
    // =============================================================================

    /**
     * 이동연소 데이터 생성 (POST /)
     */
    @Transactional
    public MobileCombustionResponse createMobileCombustion(Long memberId, MobileCombustionRequest request) {
        try {
            // 1. Request 검증
            validateRequest(request);

            // 2. 엔티티 생성
            MobileCombustion entity = MobileCombustion.builder()
                    .memberId(memberId)
                    .companyId(request.getCompanyId())
                    .reportingYear(request.getReportingYear())
                    .reportingMonth(request.getReportingMonth())
                    .facilityLocation(request.getFacilityLocation())
                    .combustionType(request.getCombustionType())
                    .fuelId(request.getFuelId())
                    .fuelName(request.getFuelName())
                    .distance(request.getDistance())
                    .unit(request.getUnit())
                    .createdBy(request.getCreatedBy())
                    .notes(request.getNotes())
                    .build();

            // 3. 배출량 계산 및 설정
            calculateAndSetEmissions(entity);

            // 4. 저장
            MobileCombustion saved = mobileCombustionRepository.save(entity);
            log.info("이동연소 데이터 생성 완료 - ID: {}", saved.getId());

            return convertToResponse(saved);

        } catch (Exception e) {
            log.error("이동연소 데이터 생성 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("이동연소 데이터 생성 실패", e);
        }
    }

    /**
     * 이동연소 데이터 수정 (PUT /{id})
     */
    @Transactional
    public MobileCombustionResponse updateMobileCombustion(Long id, Long memberId, MobileCombustionRequest request) {
        try {
            // 1. 기존 데이터 조회 및 권한 확인
            MobileCombustion entity = mobileCombustionRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("이동연소 데이터를 찾을 수 없습니다: " + id));

            if (!entity.getMemberId().equals(memberId)) {
                throw new IllegalArgumentException("해당 데이터에 대한 접근 권한이 없습니다.");
            }

            // 2. Request 검증
            validateRequest(request);

            // 3. 엔티티 업데이트
            entity.updateFromScopeModal(request);

            // 4. 배출량 재계산
            calculateAndSetEmissions(entity);

            // 5. 저장
            MobileCombustion updated = mobileCombustionRepository.save(entity);
            log.info("이동연소 데이터 수정 완료 - ID: {}", id);

            return convertToResponse(updated);

        } catch (Exception e) {
            log.error("이동연소 데이터 수정 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("이동연소 데이터 수정 실패", e);
        }
    }

    /**
     * 이동연소 데이터 삭제 (DELETE /{id})
     */
    @Transactional
    public void deleteMobileCombustion(Long id, Long memberId) {
        try {
            MobileCombustion entity = mobileCombustionRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("이동연소 데이터를 찾을 수 없습니다: " + id));

            if (!entity.getMemberId().equals(memberId)) {
                throw new IllegalArgumentException("해당 데이터에 대한 접근 권한이 없습니다.");
            }

            mobileCombustionRepository.delete(entity);
            log.info("이동연소 데이터 삭제 완료 - ID: {}", id);

        } catch (Exception e) {
            log.error("이동연소 데이터 삭제 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("이동연소 데이터 삭제 실패", e);
        }
    }

    /**
     * 이동연소 데이터 상세 조회 (GET /{id})
     */
    @Transactional(readOnly = true)
    public MobileCombustionResponse getById(Long id, Long memberId) {
        MobileCombustion entity = mobileCombustionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("이동연소 데이터를 찾을 수 없습니다: " + id));

        if (!entity.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("해당 데이터에 대한 접근 권한이 없습니다.");
        }

        return convertToResponse(entity);
    }

    // =============================================================================
    // 협력사별 조회 메서드 (핵심)
    // =============================================================================

    /**
     * 협력사별 연도별 이동연소 데이터 조회 (GET /partner/{partnerCompanyId}/year/{year})
     */
    @Transactional(readOnly = true)
    public List<MobileCombustionResponse> getByPartnerAndYear(
            Long memberId, String partnerCompanyId, Integer year) {
        List<MobileCombustion> entities = mobileCombustionRepository
                .findByMemberIdAndCompanyIdAndReportingYear(memberId, partnerCompanyId, year);
        
        return entities.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 협력사별 전체 이동연소 데이터 조회 (GET /partner/{partnerCompanyId})
     */
    @Transactional(readOnly = true)
    public List<MobileCombustionResponse> getByPartner(Long memberId, String partnerCompanyId) {
        List<MobileCombustion> entities = mobileCombustionRepository
                .findByMemberIdAndCompanyId(memberId, partnerCompanyId);
        
        return entities.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // =============================================================================
    // 기본 조회 메서드
    // =============================================================================

    /**
     * 회원별 전체 이동연소 데이터 조회 (GET /)
     */
    @Transactional(readOnly = true)
    public List<MobileCombustionResponse> getAllByMember(Long memberId) {
        List<MobileCombustion> entities = mobileCombustionRepository.findByMemberId(memberId);
        
        return entities.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 회원별 연도별 이동연소 데이터 조회 (GET /year/{year})
     */
    @Transactional(readOnly = true)
    public List<MobileCombustionResponse> getByYear(Long memberId, Integer year) {
        List<MobileCombustion> entities = mobileCombustionRepository
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
    public List<ScopeEmissionSummaryResponse> getMonthlyEmissionSummary(Long memberId, Integer year, String partnerCompanyId) {
        Map<Integer, BigDecimal> monthlyData = mobileCombustionRepository.getMonthlyEmissionSummary(memberId, year, partnerCompanyId);
        
        return monthlyData.entrySet().stream()
                .map(entry -> ScopeEmissionSummaryResponse.builder()
                        .category(entry.getKey().toString())
                        .totalEmission(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 연료별 배출량 집계
     */
    @Transactional(readOnly = true)
    public List<ScopeEmissionSummaryResponse> getEmissionSummaryByFuel(Long memberId, Integer year, String partnerCompanyId) {
        Map<String, BigDecimal> fuelData = mobileCombustionRepository.getEmissionSummaryByFuel(memberId, year, partnerCompanyId);
        
        return fuelData.entrySet().stream()
                .map(entry -> ScopeEmissionSummaryResponse.builder()
                        .category(entry.getKey())
                        .totalEmission(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 시설별 배출량 집계
     */
    @Transactional(readOnly = true)
    public List<ScopeEmissionSummaryResponse> getEmissionSummaryByFacility(Long memberId, Integer year, String partnerCompanyId) {
        Map<String, BigDecimal> facilityData = mobileCombustionRepository.getEmissionSummaryByFacility(memberId, year, partnerCompanyId);
        
        return facilityData.entrySet().stream()
                .map(entry -> ScopeEmissionSummaryResponse.builder()
                        .category(entry.getKey())
                        .totalEmission(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 협력사별 배출량 집계
     */
    @Transactional(readOnly = true)
    public List<ScopeEmissionSummaryResponse> getEmissionSummaryByPartner(Long memberId, Integer year) {
        Map<String, BigDecimal> partnerData = mobileCombustionRepository.getEmissionSummaryByPartner(memberId, year);
        
        return partnerData.entrySet().stream()
                .map(entry -> ScopeEmissionSummaryResponse.builder()
                        .category(entry.getKey())
                        .totalEmission(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 차량별 배출량 집계
     */
    @Transactional(readOnly = true)
    public List<ScopeEmissionSummaryResponse> getEmissionSummaryByVehicle(Long memberId, Integer year, String partnerCompanyId) {
        Map<String, BigDecimal> vehicleData = mobileCombustionRepository.getEmissionSummaryByVehicle(memberId, year, partnerCompanyId);
        
        return vehicleData.entrySet().stream()
                .map(entry -> ScopeEmissionSummaryResponse.builder()
                        .category(entry.getKey())
                        .totalEmission(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 연도별 총 배출량
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalEmissionByYear(Long memberId, Integer year, String partnerCompanyId) {
        if (partnerCompanyId != null) {
            return mobileCombustionRepository.getTotalEmissionByMemberAndPartnerAndYear(memberId, partnerCompanyId, year);
        } else {
            return mobileCombustionRepository.getTotalEmissionByMemberAndYear(memberId, year);
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
                "facilityBreakdown", getEmissionSummaryByFacility(memberId, year, null),
                "vehicleBreakdown", getEmissionSummaryByVehicle(memberId, year, null)
        );
    }

    // =============================================================================
    // 내부 유틸리티 메서드
    // =============================================================================

    /**
     * Request 검증
     */
    private void validateRequest(MobileCombustionRequest request) {
        if (request.getCompanyId() == null || request.getCompanyId().trim().isEmpty()) {
            throw new IllegalArgumentException("회사 ID는 필수입니다.");
        }
        if (request.getReportingYear() == null) {
            throw new IllegalArgumentException("보고 연도는 필수입니다.");
        }
        if (request.getReportingMonth() == null) {
            throw new IllegalArgumentException("보고 월은 필수입니다.");
        }
        if (request.getDistance() == null || request.getDistance().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("이동거리는 0보다 커야 합니다.");
        }
    }

    /**
     * 배출량 계산 및 엔티티에 설정
     */
    private void calculateAndSetEmissions(MobileCombustion entity) {
        try {
            // EmissionCalculationService를 통한 배출량 계산
            var emissionResult = emissionCalculationService.calculateScope1MobileEmission(
                    entity.getFuelId(), entity.getDistance(), entity.getReportingYear());
            
            entity.updateEmissions(
                    emissionResult.getCo2Emission(),
                    emissionResult.getCh4Emission(),
                    emissionResult.getN2oEmission(),
                    emissionResult.getTotalCo2Equivalent()
            );
            
        } catch (Exception e) {
            log.warn("배출량 계산 실패, 기본값 사용: {}", e.getMessage());
            // 계산 실패 시 0으로 설정
            entity.updateEmissions(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
    }

    /**
     * 엔티티를 응답 DTO로 변환
     */
    private MobileCombustionResponse convertToResponse(MobileCombustion entity) {
        return MobileCombustionResponse.builder()
                .id(entity.getId())
                .companyId(entity.getCompanyId())
                .reportingYear(entity.getReportingYear())
                .reportingMonth(entity.getReportingMonth())
                .facilityLocation(entity.getFacilityLocation())
                .combustionType(entity.getCombustionType())
                .fuelId(entity.getFuelId())
                .fuelName(entity.getFuelName())
                .distance(entity.getDistance())
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
        BigDecimal ch4Emission = BigDecimal.ZERO;
        BigDecimal n2oEmission = BigDecimal.ZERO;
        BigDecimal totalEmission = BigDecimal.ZERO;
        
        mobileCombustion.updateEmissions(co2Emission, ch4Emission, n2oEmission, totalEmission);

        MobileCombustion saved = mobileCombustionRepository.save(mobileCombustion);
        log.info("Mobile combustion record created with ID: {}", saved.getId());

        return convertToResponse(saved);
    }

    public MobileCombustionResponse findById(Long id) {
        MobileCombustion mobileCombustion = mobileCombustionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("모바일 연소 기록을 찾을 수 없습니다: " + id));
        return convertToResponse(mobileCombustion);
    }

    public Page<MobileCombustionResponse> findByCompanyId(Long companyId, Pageable pageable) {
        Page<MobileCombustion> mobileCombustions = mobileCombustionRepository.findByCompanyIdOrderByCreatedAtDesc(companyId, pageable);
        return mobileCombustions.map(this::convertToResponse);
    }

    public List<MobileCombustionResponse> findByCompanyIdAndYear(Long companyId, Integer year) {
        List<MobileCombustion> mobileCombustions = mobileCombustionRepository.findByCompanyIdAndReportingYear(companyId, year);
        return mobileCombustions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public MobileCombustionResponse update(Long id, MobileCombustionRequest request) {
        log.info("Updating mobile combustion record: {}", id);

        MobileCombustion existingRecord = mobileCombustionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("모바일 연소 기록을 찾을 수 없습니다: " + id));

        // 연료 타입 조회
        FuelType fuelType = fuelTypeRepository.findById(request.getFuelTypeId())
                .orElseThrow(() -> new IllegalArgumentException("연료 타입을 찾을 수 없습니다: " + request.getFuelTypeId()));

        // 엔티티 업데이트
        existingRecord.updateFromRequest(request, fuelType);

        MobileCombustion updated = mobileCombustionRepository.save(existingRecord);
        log.info("Mobile combustion record updated: {}", id);

        return convertToResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting mobile combustion record: {}", id);

        if (!mobileCombustionRepository.existsById(id)) {
            throw new IllegalArgumentException("모바일 연소 기록을 찾을 수 없습니다: " + id);
        }

        mobileCombustionRepository.deleteById(id);
        log.info("Mobile combustion record deleted: {}", id);
    }

    public BigDecimal getTotalEmissionByCompanyAndYear(Long companyId, Integer year) {
        return mobileCombustionRepository.sumTotalCo2EquivalentByCompanyIdAndYear(companyId, year)
                .orElse(BigDecimal.ZERO);
    }

    public List<Object[]> getEmissionSummaryByVehicleType(Long companyId, Integer year) {
        return mobileCombustionRepository.sumEmissionByVehicleType(companyId, year);
    }

    private MobileCombustionResponse convertToResponse(MobileCombustion mobileCombustion) {
        return MobileCombustionResponse.builder()
                .id(mobileCombustion.getId())
                .companyId(mobileCombustion.getCompanyId())
                .reportingYear(mobileCombustion.getReportingYear())
                .vehicleType(mobileCombustion.getVehicleType())
                .fuelTypeName(mobileCombustion.getFuelType().getName())
                .fuelUsage(mobileCombustion.getFuelUsage())
                .unit(mobileCombustion.getUnit())
                .co2Emission(mobileCombustion.getCo2Emission())
                .ch4Emission(mobileCombustion.getCh4Emission())
                .n2oEmission(mobileCombustion.getN2oEmission())
                .totalCo2Equivalent(mobileCombustion.getTotalCo2Equivalent())
                .calculatedAt(mobileCombustion.getCalculatedAt())
                .createdAt(mobileCombustion.getCreatedAt())
                .updatedAt(mobileCombustion.getUpdatedAt())
                .createdBy(mobileCombustion.getCreatedBy())
                .updatedBy(mobileCombustion.getUpdatedBy())
                .build();
    }

    // 협력사별 조회 메서드들

    /**
     * 회원별 및 협력사별 이동연소 데이터 목록 조회
     */
    public List<MobileCombustionResponse> getMobileCombustionListByPartner(Long memberId, String partnerCompanyId) {
        return mobileCombustionRepository.findByMemberIdAndPartnerCompanyIdOrderByYearDescMonthDesc(memberId, partnerCompanyId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 회원별 및 협력사별 특정 연도 이동연소 데이터 조회
     */
    public List<MobileCombustionResponse> getMobileCombustionByPartnerAndYear(Long memberId, String partnerCompanyId, Integer year) {
        return mobileCombustionRepository.findByMemberIdAndPartnerCompanyIdAndYearOrderByMonthAsc(memberId, partnerCompanyId, year).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 회원별 및 협력사별 연도별 이동연소 총 배출량 조회
     */
    public BigDecimal getTotalEmissionByPartnerAndYear(Long memberId, String partnerCompanyId, Integer year) {
        BigDecimal totalEmission = mobileCombustionRepository.sumTotalEmissionByMemberIdAndPartnerCompanyIdAndYear(memberId, partnerCompanyId, year);
        return totalEmission != null ? totalEmission : BigDecimal.ZERO;
    }
}
