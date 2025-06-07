package com.nsmm.esg.scopeservice.service;

import com.nsmm.esg.scopeservice.dto.SteamUsageRequest;
import com.nsmm.esg.scopeservice.dto.SteamUsageResponse;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SteamUsageService {

    private final SteamUsageRepository steamUsageRepository;
    
    // 스팀 배출계수 (한국 전력거래소 기준)
    private static final BigDecimal STEAM_EMISSION_FACTOR = new BigDecimal("0.073"); // tCO2/GJ

    @Transactional
    public SteamUsageResponse create(SteamUsageRequest request) {
        log.info("Creating steam usage record for company: {}", request.getCompanyId());

        // CO2 배출량 계산 (스팀 사용량 × 배출계수)
        BigDecimal emission = request.getSteamUsage().multiply(STEAM_EMISSION_FACTOR);

        SteamUsage steamUsage = SteamUsage.builder()
                .companyId(request.getCompanyId())
                .reportingYear(request.getReportingYear())
                .facilityName(request.getFacilityName())
                .steamUsage(request.getSteamUsage())
                .unit(request.getUnit())
                .co2Emission(emission)
                .calculatedAt(LocalDateTime.now())
                .createdBy(request.getCreatedBy())
                .build();

        SteamUsage saved = steamUsageRepository.save(steamUsage);
        log.info("Steam usage record created with ID: {}", saved.getId());

        return convertToResponse(saved);
    }

    public SteamUsageResponse findById(Long id) {
        SteamUsage steamUsage = steamUsageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("스팀 사용량 기록을 찾을 수 없습니다: " + id));
        return convertToResponse(steamUsage);
    }

    public Page<SteamUsageResponse> findByCompanyId(Long companyId, Pageable pageable) {
        Page<SteamUsage> steamUsages = steamUsageRepository.findByCompanyIdOrderByCreatedAtDesc(companyId, pageable);
        return steamUsages.map(this::convertToResponse);
    }

    public List<SteamUsageResponse> findByCompanyIdAndYear(Long companyId, Integer year) {
        List<SteamUsage> steamUsages = steamUsageRepository.findByCompanyIdAndReportingYear(companyId, year);
        return steamUsages.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public SteamUsageResponse update(Long id, SteamUsageRequest request) {
        log.info("Updating steam usage record: {}", id);

        SteamUsage existingRecord = steamUsageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("스팀 사용량 기록을 찾을 수 없습니다: " + id));

        // 배출량 재계산
        BigDecimal emission = request.getSteamUsage().multiply(STEAM_EMISSION_FACTOR);

        // 업데이트
        existingRecord.setReportingYear(request.getReportingYear());
        existingRecord.setFacilityName(request.getFacilityName());
        existingRecord.setSteamUsage(request.getSteamUsage());
        existingRecord.setUnit(request.getUnit());
        existingRecord.setCo2Emission(emission);
        existingRecord.setCalculatedAt(LocalDateTime.now());
        existingRecord.setUpdatedBy(request.getCreatedBy());

        SteamUsage updated = steamUsageRepository.save(existingRecord);
        log.info("Steam usage record updated: {}", id);

        return convertToResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting steam usage record: {}", id);

        if (!steamUsageRepository.existsById(id)) {
            throw new IllegalArgumentException("스팀 사용량 기록을 찾을 수 없습니다: " + id);
        }

        steamUsageRepository.deleteById(id);
        log.info("Steam usage record deleted: {}", id);
    }

    public BigDecimal getTotalEmissionByCompanyAndYear(Long companyId, Integer year) {
        return steamUsageRepository.sumCo2EmissionByCompanyIdAndYear(companyId, year)
                .orElse(BigDecimal.ZERO);
    }

    public List<Object[]> getEmissionSummaryByFacility(Long companyId, Integer year) {
        return steamUsageRepository.sumEmissionByFacility(companyId, year);
    }

    private SteamUsageResponse convertToResponse(SteamUsage steamUsage) {
        return SteamUsageResponse.builder()
                .id(steamUsage.getId())
                .companyId(steamUsage.getCompanyId())
                .reportingYear(steamUsage.getReportingYear())
                .facilityName(steamUsage.getFacilityName())
                .steamUsage(steamUsage.getSteamUsage())
                .unit(steamUsage.getUnit())
                .co2Emission(steamUsage.getCo2Emission())
                .calculatedAt(steamUsage.getCalculatedAt())
                .createdAt(steamUsage.getCreatedAt())
                .updatedAt(steamUsage.getUpdatedAt())
                .createdBy(steamUsage.getCreatedBy())
                .updatedBy(steamUsage.getUpdatedBy())
                .build();
    }
}
