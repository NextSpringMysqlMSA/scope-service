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

        // 발열량 조회
        CalorificValue calorificValue = calorificValueRepository.findByFuelTypeId(request.getFuelTypeId())
                .orElseThrow(() -> new IllegalArgumentException("발열량 정보를 찾을 수 없습니다: " + request.getFuelTypeId()));

        // 배출계수 조회
        EmissionFactor emissionFactor = emissionFactorRepository.findByFuelTypeId(request.getFuelTypeId())
                .orElseThrow(() -> new IllegalArgumentException("배출계수 정보를 찾을 수 없습니다: " + request.getFuelTypeId()));

        // CO2 배출량 계산 (연료사용량 × 발열량 × 배출계수)
        BigDecimal emission = emissionCalculationService.calculateEmission(
                request.getFuelUsage(),
                calorificValue.getValue(),
                emissionFactor.getCo2Factor()
        );

        // CH4 배출량 계산
        BigDecimal ch4Emission = emissionCalculationService.calculateEmission(
                request.getFuelUsage(),
                calorificValue.getValue(),
                emissionFactor.getCh4Factor()
        );

        // N2O 배출량 계산
        BigDecimal n2oEmission = emissionCalculationService.calculateEmission(
                request.getFuelUsage(),
                calorificValue.getValue(),
                emissionFactor.getN2oFactor()
        );

        // 총 CO2 등가량 계산 (CH4 * 25 + N2O * 298)
        BigDecimal totalCo2Equivalent = emission
                .add(ch4Emission.multiply(new BigDecimal("25")))
                .add(n2oEmission.multiply(new BigDecimal("298")));

        MobileCombustion mobileCombustion = MobileCombustion.builder()
                .companyId(request.getCompanyId())
                .reportingYear(request.getReportingYear())
                .vehicleType(request.getVehicleType())
                .fuelType(fuelType)
                .fuelUsage(request.getFuelUsage())
                .unit(request.getUnit())
                .co2Emission(emission)
                .ch4Emission(ch4Emission)
                .n2oEmission(n2oEmission)
                .totalCo2Equivalent(totalCo2Equivalent)
                .calculatedAt(LocalDateTime.now())
                .createdBy(request.getCreatedBy())
                .build();

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

        // 연료 타입이 변경된 경우 새로 조회
        if (!existingRecord.getFuelType().getId().equals(request.getFuelTypeId())) {
            FuelType fuelType = fuelTypeRepository.findById(request.getFuelTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("연료 타입을 찾을 수 없습니다: " + request.getFuelTypeId()));
            existingRecord.setFuelType(fuelType);
        }

        // 발열량 및 배출계수 재조회
        CalorificValue calorificValue = calorificValueRepository.findByFuelTypeId(request.getFuelTypeId())
                .orElseThrow(() -> new IllegalArgumentException("발열량 정보를 찾을 수 없습니다: " + request.getFuelTypeId()));

        EmissionFactor emissionFactor = emissionFactorRepository.findByFuelTypeId(request.getFuelTypeId())
                .orElseThrow(() -> new IllegalArgumentException("배출계수 정보를 찾을 수 없습니다: " + request.getFuelTypeId()));

        // 배출량 재계산
        BigDecimal emission = emissionCalculationService.calculateEmission(
                request.getFuelUsage(),
                calorificValue.getValue(),
                emissionFactor.getCo2Factor()
        );

        BigDecimal ch4Emission = emissionCalculationService.calculateEmission(
                request.getFuelUsage(),
                calorificValue.getValue(),
                emissionFactor.getCh4Factor()
        );

        BigDecimal n2oEmission = emissionCalculationService.calculateEmission(
                request.getFuelUsage(),
                calorificValue.getValue(),
                emissionFactor.getN2oFactor()
        );

        BigDecimal totalCo2Equivalent = emission
                .add(ch4Emission.multiply(new BigDecimal("25")))
                .add(n2oEmission.multiply(new BigDecimal("298")));

        // 업데이트
        existingRecord.setReportingYear(request.getReportingYear());
        existingRecord.setVehicleType(request.getVehicleType());
        existingRecord.setFuelUsage(request.getFuelUsage());
        existingRecord.setUnit(request.getUnit());
        existingRecord.setCo2Emission(emission);
        existingRecord.setCh4Emission(ch4Emission);
        existingRecord.setN2oEmission(n2oEmission);
        existingRecord.setTotalCo2Equivalent(totalCo2Equivalent);
        existingRecord.setCalculatedAt(LocalDateTime.now());
        existingRecord.setUpdatedBy(request.getCreatedBy());

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
}
