package com.nsmm.esg.scopeservice.service;

import com.nsmm.esg.scopeservice.dto.request.ScopeEmissionRequest;
import com.nsmm.esg.scopeservice.dto.response.ScopeEmissionResponse;
import com.nsmm.esg.scopeservice.entity.ScopeEmission;
import com.nsmm.esg.scopeservice.entity.enums.EmissionActivityType;
import com.nsmm.esg.scopeservice.repository.ScopeEmissionRepository;
import com.nsmm.esg.scopeservice.entity.FuelData;
import com.nsmm.esg.scopeservice.repository.FuelDataRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScopeEmissionService {

    private final ScopeEmissionRepository scopeEmissionRepository;
    private final FuelDataRepository fuelDataRepository;

    // =========== CREATE ===========
    @Transactional
    public ScopeEmissionResponse createEmission(Long memberId, String companyId,
                                                EmissionActivityType activityType,
                                                ScopeEmissionRequest dto) {
        // 1. FuelData 기준정보 불러오기 (fuelId로)
        FuelData fuelData = fuelDataRepository.findById(dto.getFuelId())
                .orElseThrow(() -> new EntityNotFoundException("FuelData Not Found"));

        // 2. 계산 (activityType별 공식 분기)
        ScopeEmission emission = calculateAndBuildEmission(memberId, companyId, activityType, dto, fuelData);

        // 3. 저장
        ScopeEmission saved = scopeEmissionRepository.save(emission);

        // 4. 응답 변환
        return toResponseDto(saved);
    }

    // =========== READ ===========
    @Transactional(readOnly = true)
    public ScopeEmissionResponse getEmission(Long memberId, Long id) {
        ScopeEmission emission = scopeEmissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ScopeEmission Not Found"));

        if (!emission.getMemberId().equals(memberId)) throw new SecurityException("권한 없음");
        return toResponseDto(emission);
    }

    @Transactional(readOnly = true)
    public List<ScopeEmissionResponse> getEmissions(Long memberId, String companyId,
                                                    EmissionActivityType activityType,
                                                    Integer reportingYear, Integer reportingMonth) {
        List<ScopeEmission> list = scopeEmissionRepository
                .findByMemberIdAndCompanyIdAndEmissionActivityTypeAndReportingYearAndReportingMonth(
                        memberId, companyId, activityType, reportingYear, reportingMonth
                );
        return list.stream().map(this::toResponseDto).collect(Collectors.toList());
    }


    // =========== UPDATE ===========
    @Transactional
    public ScopeEmissionResponse updateEmission(Long memberId, Long id, ScopeEmissionRequest dto) {
        ScopeEmission emission = scopeEmissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ScopeEmission Not Found"));

        if (!emission.getMemberId().equals(memberId)) throw new SecurityException("권한 없음");

        // FuelData 갱신
        FuelData fuelData = fuelDataRepository.findById(dto.getFuelId())
                .orElseThrow(() -> new EntityNotFoundException("FuelData Not Found"));

        // 계산 재적용
        ScopeEmission updated = calculateAndBuildEmission(memberId, emission.getCompanyId(),
                emission.getEmissionActivityType(), dto, fuelData);

        updated = updated.withUpdatedUsage(dto.getFuelUsage(), dto.getNotes());
        updated = scopeEmissionRepository.save(updated);

        return toResponseDto(updated);
    }

    // =========== DELETE ===========
    @Transactional
    public void deleteEmission(Long memberId, Long id) {
        ScopeEmission emission = scopeEmissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ScopeEmission Not Found"));
        if (!emission.getMemberId().equals(memberId)) throw new SecurityException("권한 없음");
        scopeEmissionRepository.delete(emission);
    }

    // =================== 계산/엔티티 변환 ===================
    private ScopeEmission calculateAndBuildEmission(Long memberId, String companyId,
                                                    EmissionActivityType activityType,
                                                    ScopeEmissionRequest dto,
                                                    FuelData fuelData) {
        // 공식 분기
        switch (activityType) {
            case STATIONARY_COMBUSTION:
                return calculateStationaryEmission(memberId, companyId, activityType, dto, fuelData);
            case MOBILE_COMBUSTION:
                return calculateMobileEmission(memberId, companyId, activityType, dto, fuelData);
            case ELECTRICITY:
                return calculateElectricityEmission(memberId, companyId, activityType, dto, fuelData);
            case STEAM:
                return calculateSteamEmission(memberId, companyId, activityType, dto, fuelData);
            default:
                throw new IllegalArgumentException("Unknown activityType: " + activityType);
        }
    }

    // ------------ 1. 고정연소 공식 ------------
    private ScopeEmission calculateStationaryEmission(Long memberId, String companyId,
                                                      EmissionActivityType activityType,
                                                      ScopeEmissionRequest dto, FuelData fuelData) {
        BigDecimal ncv = fuelData.getNcv();
        BigDecimal co2Factor = fuelData.getCo2Factor();
        // CH4/N2O 계수: 용도(PurposeCategory)에 따라 선택
        BigDecimal ch4Factor = getCh4FactorByPurpose(fuelData, dto.getPurposeCategory());
        BigDecimal n2oFactor = getN2oFactorByPurpose(fuelData, dto.getPurposeCategory());
        // 계산 공식
        // CO2 = 사용량 * NCV * CO2계수 * 1e-6
        // CH4 = 사용량 * NCV * CH4계수 * 21 * 1e-6
        // N2O = 사용량 * NCV * N2O계수 * 310 * 1e-6
        BigDecimal fuelUsage = dto.getFuelUsage();
        BigDecimal oneMillionth = new BigDecimal("0.000001");
        BigDecimal co2Emission = fuelUsage.multiply(ncv).multiply(co2Factor).multiply(BigDecimal.ONE).multiply(oneMillionth);
        BigDecimal ch4Emission = fuelUsage.multiply(ncv).multiply(ch4Factor).multiply(BigDecimal.valueOf(21)).multiply(oneMillionth);
        BigDecimal n2oEmission = fuelUsage.multiply(ncv).multiply(n2oFactor).multiply(BigDecimal.valueOf(310)).multiply(oneMillionth);
        BigDecimal totalEmission = co2Emission.add(ch4Emission).add(n2oEmission);

        return ScopeEmission.createWithCalculatedEmissions(
                memberId, companyId, dto.getReportingYear(), dto.getReportingMonth(),
                activityType, dto.getScopeType(), dto.getFuelId(), fuelData.getFuelName(),
                fuelUsage, dto.getUsageUnit(), dto.getPurposeCategory(), null,
                ncv, co2Factor, ch4Factor, n2oFactor, dto.getNotes()
        );
    }

    // ------------ 2. 이동연소 공식 ------------
    private ScopeEmission calculateMobileEmission(Long memberId, String companyId,
                                                  EmissionActivityType activityType,
                                                  ScopeEmissionRequest dto, FuelData fuelData) {
        BigDecimal ncv = fuelData.getNcv();
        BigDecimal co2Factor = fuelData.getMobileCo2Factor();
        BigDecimal ch4Factor = fuelData.getMobileCh4Factor();
        BigDecimal n2oFactor = fuelData.getMobileN2oFactor();
        BigDecimal fuelUsage = dto.getFuelUsage();
        BigDecimal oneMillionth = new BigDecimal("0.000001");
        BigDecimal co2Emission = fuelUsage.multiply(ncv).multiply(co2Factor).multiply(BigDecimal.ONE).multiply(oneMillionth);
        BigDecimal ch4Emission = fuelUsage.multiply(ncv).multiply(ch4Factor).multiply(BigDecimal.valueOf(21)).multiply(oneMillionth);
        BigDecimal n2oEmission = fuelUsage.multiply(ncv).multiply(n2oFactor).multiply(BigDecimal.valueOf(310)).multiply(oneMillionth);
        BigDecimal totalEmission = co2Emission.add(ch4Emission).add(n2oEmission);

        return ScopeEmission.createWithCalculatedEmissions(
                memberId, companyId, dto.getReportingYear(), dto.getReportingMonth(),
                activityType, dto.getScopeType(), dto.getFuelId(), fuelData.getFuelName(),
                fuelUsage, dto.getUsageUnit(), dto.getPurposeCategory(), null,
                ncv, co2Factor, ch4Factor, n2oFactor, dto.getNotes()
        );
    }

    // ------------ 3. 전력 사용 공식 ------------
    private ScopeEmission calculateElectricityEmission(Long memberId, String companyId,
                                                       EmissionActivityType activityType,
                                                       ScopeEmissionRequest dto, FuelData fuelData) {
        BigDecimal usage = dto.getFuelUsage(); // kWh
        BigDecimal co2Factor = new BigDecimal("0.4653"); // 공식 배출계수
        BigDecimal totalEmission = usage.multiply(co2Factor).multiply(new BigDecimal("0.001"));
        // CO2만 계산(다른 가스는 0)
        return ScopeEmission.createWithCalculatedEmissions(
                memberId, companyId, dto.getReportingYear(), dto.getReportingMonth(),
                activityType, dto.getScopeType(), dto.getFuelId(), fuelData.getFuelName(),
                usage, dto.getUsageUnit(), null, null,
                null, co2Factor, BigDecimal.ZERO, BigDecimal.ZERO, dto.getNotes()
        ).withUpdatedUsage(usage, dto.getNotes());
    }

    // ------------ 4. 스팀 사용 공식 ------------
    private ScopeEmission calculateSteamEmission(Long memberId, String companyId,
                                                 EmissionActivityType activityType,
                                                 ScopeEmissionRequest dto, FuelData fuelData) {
        BigDecimal usage = dto.getFuelUsage(); // GJ
        // 스팀 타입 별 배출계수
        BigDecimal co2Factor = getSteamEmissionFactorByType(dto.getSteamType());
        BigDecimal totalEmission = usage.multiply(co2Factor);
        return ScopeEmission.createWithCalculatedEmissions(
                memberId, companyId, dto.getReportingYear(), dto.getReportingMonth(),
                activityType, dto.getScopeType(), dto.getFuelId(), fuelData.getFuelName(),
                usage, dto.getUsageUnit(), null, dto.getSteamType(),
                null, co2Factor, BigDecimal.ZERO, BigDecimal.ZERO, dto.getNotes()
        ).withUpdatedUsage(usage, dto.getNotes());
    }

    // ========== 유틸: 용도별 계수 추출 ==========
    private BigDecimal getCh4FactorByPurpose(FuelData fuelData, com.nsmm.esg.scopeservice.entity.enums.PurposeCategory purpose) {
        switch (purpose) {
            case ENERGY:        return fuelData.getCh4FactorEnergy();
            case MANUFACTURING: return fuelData.getCh4FactorManufacturing();
            case COMMERCIAL:    return fuelData.getCh4FactorCommercial();
            case DOMESTIC:      return fuelData.getCh4FactorDomestic();
            default:            throw new IllegalArgumentException("Unknown purpose: " + purpose);
        }
    }
    private BigDecimal getN2oFactorByPurpose(FuelData fuelData, com.nsmm.esg.scopeservice.entity.enums.PurposeCategory purpose) {
        switch (purpose) {
            case ENERGY:        return fuelData.getN2oFactorEnergy();
            case MANUFACTURING: return fuelData.getN2oFactorManufacturing();
            case COMMERCIAL:    return fuelData.getN2oFactorCommercial();
            case DOMESTIC:      return fuelData.getN2oFactorDomestic();
            default:            throw new IllegalArgumentException("Unknown purpose: " + purpose);
        }
    }
    private BigDecimal getSteamEmissionFactorByType(String steamType) {
        // "A", "B", "C" 타입별 배출계수
        switch (steamType) {
            case "A": return new BigDecimal("56.452");
            case "B": return new BigDecimal("60.974");
            case "C": return new BigDecimal("59.685");
            default: throw new IllegalArgumentException("Unknown steamType: " + steamType);
        }
    }

    // ========== Entity → Response DTO 변환 ==========
    private ScopeEmissionResponse toResponseDto(ScopeEmission e) {
        return ScopeEmissionResponse.builder()
                .id(e.getId())
                .memberId(e.getMemberId())
                .companyId(e.getCompanyId())
                .reportingYear(e.getReportingYear())
                .reportingMonth(e.getReportingMonth())
                .emissionActivityType(e.getEmissionActivityType())
                .scopeType(e.getScopeType())
                .fuelId(e.getFuelId())
                .fuelName(e.getFuelName())
                .fuelUsage(e.getFuelUsage())
                .usageUnit(e.getUsageUnit())
                .purposeCategory(e.getPurposeCategory())
                .steamType(e.getSteamType())
                .usedNcv(e.getUsedNcv())
                .usedCo2Factor(e.getUsedCo2Factor())
                .usedCh4Factor(e.getUsedCh4Factor())
                .usedN2oFactor(e.getUsedN2oFactor())
                .co2Emission(e.getCo2Emission())
                .ch4Emission(e.getCh4Emission())
                .n2oEmission(e.getN2oEmission())
                .totalEmission(e.getTotalEmission())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .notes(e.getNotes())
                .build();
    }
}