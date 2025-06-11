package com.nsmm.esg.scopeservice.service;

import com.nsmm.esg.scopeservice.entity.CalorificValue;
import com.nsmm.esg.scopeservice.entity.EmissionFactor;
import com.nsmm.esg.scopeservice.entity.FuelType;
import com.nsmm.esg.scopeservice.repository.CalorificValueRepository;
import com.nsmm.esg.scopeservice.repository.EmissionFactorRepository;
import com.nsmm.esg.scopeservice.repository.FuelTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * 배출량 계산 서비스
 * Scope 1, 2 배출량 계산을 위한 핵심 로직
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmissionCalculationService {

    private final FuelTypeRepository fuelTypeRepository;
    private final CalorificValueRepository calorificValueRepository;
    private final EmissionFactorRepository emissionFactorRepository;

    // GWP 값 (Global Warming Potential)
    private static final BigDecimal CH4_GWP = new BigDecimal("25");    // CH4의 CO2 환산계수
    private static final BigDecimal N2O_GWP = new BigDecimal("298");   // N2O의 CO2 환산계수

    /**
     * Scope 1 연소 배출량 계산
     * 계산식: 연료사용량 × 발열량 × 배출계수 × GWP
     */
    public EmissionResult calculateScope1Emission(Long fuelTypeId, BigDecimal usage, Integer year) {
        try {
            // 1. 연료 타입 조회
            FuelType fuelType = fuelTypeRepository.findById(fuelTypeId)
                    .orElseThrow(() -> new IllegalArgumentException("연료 타입을 찾을 수 없습니다: " + fuelTypeId));

            // 2. 발열량 조회
            CalorificValue calorificValue = calorificValueRepository.findByFuelTypeIdAndYear(fuelTypeId, year)
                    .orElseThrow(() -> new IllegalArgumentException("발열량 정보를 찾을 수 없습니다: " + fuelType.getName() + ", " + year));

            // 3. 배출계수 조회
            EmissionFactor emissionFactor = emissionFactorRepository.findByFuelTypeIdAndYear(fuelTypeId, year)
                    .orElseThrow(() -> new IllegalArgumentException("배출계수 정보를 찾을 수 없습니다: " + fuelType.getName() + ", " + year));

            // 4. 배출량 계산
            // 연료사용량 × 발열량 = 에너지 소비량 (TJ)
            BigDecimal energyConsumption = usage.multiply(calorificValue.getValue());

            // CO2 배출량 (tCO2) = 에너지 소비량 × CO2 배출계수
            BigDecimal co2Emission = energyConsumption.multiply(emissionFactor.getCo2Factor())
                    .setScale(4, RoundingMode.HALF_UP);

            // CH4 배출량 (tCO2eq) = 에너지 소비량 × CH4 배출계수 × GWP × 0.001 (kg -> ton)
            BigDecimal ch4Emission = energyConsumption.multiply(emissionFactor.getCh4Factor())
                    .multiply(CH4_GWP)
                    .multiply(new BigDecimal("0.001"))
                    .setScale(4, RoundingMode.HALF_UP);

            // N2O 배출량 (tCO2eq) = 에너지 소비량 × N2O 배출계수 × GWP × 0.001 (kg -> ton)
            BigDecimal n2oEmission = energyConsumption.multiply(emissionFactor.getN2oFactor())
                    .multiply(N2O_GWP)
                    .multiply(new BigDecimal("0.001"))
                    .setScale(4, RoundingMode.HALF_UP);

            // 총 배출량 = CO2 + CH4 + N2O
            BigDecimal totalEmission = co2Emission.add(ch4Emission).add(n2oEmission)
                    .setScale(4, RoundingMode.HALF_UP);

            log.debug("배출량 계산 완료 - 연료: {}, 사용량: {}, 총 배출량: {}", 
                    fuelType.getName(), usage, totalEmission);

            return EmissionResult.builder()
                    .co2Emission(co2Emission)
                    .ch4Emission(ch4Emission)
                    .n2oEmission(n2oEmission)
                    .totalEmission(totalEmission)
                    .build();

        } catch (Exception e) {
            log.error("배출량 계산 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("배출량 계산 실패", e);
        }
    }

    /**
     * Scope 1 고정연소 배출량 계산 (새 구조용)
     */
    public BigDecimal calculateStationaryEmission(String fuelId, BigDecimal usage) {
        try {
            // 기본 배출계수 사용 (실제로는 DB에서 조회해야 함)
            BigDecimal co2Factor = new BigDecimal("2.3"); // 기본값
            return usage.multiply(co2Factor).setScale(4, RoundingMode.HALF_UP);
        } catch (Exception e) {
            log.error("고정연소 배출량 계산 실패: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * Scope 1 이동연소 배출량 계산 (새 구조용)
     */
    public BigDecimal calculateMobileEmission(String fuelId, BigDecimal distance) {
        try {
            // 기본 배출계수 사용 (실제로는 DB에서 조회해야 함)
            BigDecimal co2Factor = new BigDecimal("0.21"); // kg CO2/km
            return distance.multiply(co2Factor).divide(new BigDecimal("1000"), 4, RoundingMode.HALF_UP); // kg -> ton
        } catch (Exception e) {
            log.error("이동연소 배출량 계산 실패: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * Scope 2 전력 배출량 계산 (재생에너지 고려)
     */
    public BigDecimal calculateElectricityEmission(BigDecimal usage, Boolean isRenewable) {
        try {
            if (Boolean.TRUE.equals(isRenewable)) {
                return BigDecimal.ZERO; // 재생에너지는 배출량 0
            }
            
            // 한국 전력 배출계수 (2022년 기준)
            BigDecimal electricityFactor = new BigDecimal("0.4653"); // kg CO2/kWh
            return usage.multiply(electricityFactor).divide(new BigDecimal("1000"), 4, RoundingMode.HALF_UP); // kg -> ton
        } catch (Exception e) {
            log.error("전력 배출량 계산 실패: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * Scope 2 스팀 배출량 계산 (스팀 타입별)
     */
    public BigDecimal calculateSteamEmission(BigDecimal usage, String steamType) {
        try {
            BigDecimal steamFactor;
            
            // 스팀 타입별 배출계수 설정
            switch (steamType != null ? steamType.toLowerCase() : "default") {
                case "고압스팀":
                    steamFactor = new BigDecimal("0.073"); // tCO2/GJ
                    break;
                case "중압스팀":
                    steamFactor = new BigDecimal("0.065"); // tCO2/GJ
                    break;
                case "저압스팀":
                    steamFactor = new BigDecimal("0.058"); // tCO2/GJ
                    break;
                default:
                    steamFactor = new BigDecimal("0.065"); // 기본값
                    break;
            }
            
            return usage.multiply(steamFactor).setScale(4, RoundingMode.HALF_UP);
        } catch (Exception e) {
            log.error("스팀 배출량 계산 실패: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * 배출량 계산 결과 DTO
     */
    @lombok.Getter
    @lombok.Builder
    @lombok.AllArgsConstructor
    public static class EmissionResult {
        private BigDecimal co2Emission;
        private BigDecimal ch4Emission;
        private BigDecimal n2oEmission;
        private BigDecimal totalEmission;
    }
}
