package com.nsmm.esg.scopeservice.service;

import com.nsmm.esg.scopeservice.dto.MobileCombustionRequest;
import com.nsmm.esg.scopeservice.dto.MobileCombustionResponse;
import com.nsmm.esg.scopeservice.entity.MobileCombustion;
import com.nsmm.esg.scopeservice.entity.FuelType;
import com.nsmm.esg.scopeservice.entity.CalorificValue;
import com.nsmm.esg.scopeservice.entity.EmissionFactor;
import com.nsmm.esg.scopeservice.repository.MobileCombustionRepository;
import com.nsmm.esg.scopeservice.repository.FuelTypeRepository;
import com.nsmm.esg.scopeservice.repository.CalorificValueRepository;
import com.nsmm.esg.scopeservice.repository.EmissionFactorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MobileCombustionService {

    private final MobileCombustionRepository mobileCombustionRepository;
    private final FuelTypeRepository fuelTypeRepository;
    private final CalorificValueRepository calorificValueRepository;
    private final EmissionFactorRepository emissionFactorRepository;
    private final EmissionCalculationService emissionCalculationService;

    @Transactional
    public MobileCombustionResponse create(MobileCombustionRequest request) {
        log.info("Creating mobile combustion record for company: {}", request.getCompanyId());

        // 연료 타입 조회
        FuelType fuelType = fuelTypeRepository.findById(request.getFuelTypeId())
                .orElseThrow(() -> new IllegalArgumentException("연료 타입을 찾을 수 없습니다: " + request.getFuelTypeId()));

        // 엔티티 생성
        MobileCombustion mobileCombustion = request.toEntity(1L, fuelType); // 임시로 1L 사용
        
        // 배출량 계산
        // 임시로 기본 배출량 설정 (실제로는 배출계수를 통한 계산 필요)
        BigDecimal co2Emission = BigDecimal.ZERO;
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
