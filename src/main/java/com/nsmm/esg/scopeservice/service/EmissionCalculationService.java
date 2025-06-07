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
     * Scope 2 전력 배출량 계산
     * 계산식: 전력사용량(kWh) × 배출계수(tCO2/MWh) ÷ 1000
     */
    public BigDecimal calculateElectricityEmission(BigDecimal usage, BigDecimal emissionFactor) {
        try {
            // kWh를 MWh로 변환하여 계산
            BigDecimal mwhUsage = usage.divide(new BigDecimal("1000"), 4, RoundingMode.HALF_UP);
            BigDecimal emission = mwhUsage.multiply(emissionFactor)
                    .setScale(4, RoundingMode.HALF_UP);

            log.debug("전력 배출량 계산 완료 - 사용량: {} kWh, 배출량: {} tCO2eq", usage, emission);
            return emission;

        } catch (Exception e) {
            log.error("전력 배출량 계산 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("전력 배출량 계산 실패", e);
        }
    }

    /**
     * Scope 2 스팀 배출량 계산
     * 계산식: 스팀사용량(ton) × 배출계수(tCO2/ton)
     */
    public BigDecimal calculateSteamEmission(BigDecimal usage, BigDecimal emissionFactor) {
        try {
            BigDecimal emission = usage.multiply(emissionFactor)
                    .setScale(4, RoundingMode.HALF_UP);

            log.debug("스팀 배출량 계산 완료 - 사용량: {} ton, 배출량: {} tCO2eq", usage, emission);
            return emission;

        } catch (Exception e) {
            log.error("스팀 배출량 계산 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("스팀 배출량 계산 실패", e);
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
