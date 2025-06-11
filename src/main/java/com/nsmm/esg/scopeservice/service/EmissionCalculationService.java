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
 * Scope 1, 2의 GHG 배출량 계산을 담당
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmissionCalculationService {

    private final FuelTypeRepository fuelTypeRepository;
    private final CalorificValueRepository calorificValueRepository;
    private final EmissionFactorRepository emissionFactorRepository;

    // 지구온난화지수 (Global Warming Potential)
    private static final BigDecimal CH4_GWP = new BigDecimal("25");
    private static final BigDecimal N2O_GWP = new BigDecimal("298");

    /**
     * Scope 1 연소 배출량 계산
     * 연료 사용량 × 발열량 × 배출계수 × GWP로 배출량을 산정
     */
    public EmissionResult calculateScope1Emission(String fuelId, BigDecimal usage, Integer year) {
        try {
            // 1. fuelId로 연료 타입 조회
            FuelType fuelType = fuelTypeRepository.findByFuelIdAndIsActiveTrue(fuelId)
                    .orElseThrow(() -> new IllegalArgumentException("연료 타입을 찾을 수 없습니다: " + fuelId));

            Long fuelTypeId = fuelType.getId(); // 내부 ID 사용

            // 2. 발열량 조회
            Optional<CalorificValue> calorificValueOpt = calorificValueRepository
                    .findByFuelType_IdAndYearAndIsActiveTrue(fuelTypeId, year);

            BigDecimal calorificValueAmount = calorificValueOpt
                    .map(CalorificValue::getValue)
                    .orElseGet(() -> {
                        log.warn("발열량 정보 없음, 기본값 사용 - 연료: {}, 연도: {}", fuelType.getName(), year);
                        return getDefaultCalorificValue(fuelType.getName());
                    });

            // 3. 배출계수 조회
            Optional<EmissionFactor> emissionFactorOpt = emissionFactorRepository
                    .findByFuelType_IdAndYearAndIsActiveTrue(fuelTypeId, year);

            BigDecimal co2Factor, ch4Factor, n2oFactor;
            if (emissionFactorOpt.isPresent()) {
                EmissionFactor factor = emissionFactorOpt.get();
                co2Factor = factor.getCo2Factor();
                ch4Factor = factor.getCh4Factor();
                n2oFactor = factor.getN2oFactor();
            } else {
                log.warn("배출계수 정보 없음, 기본값 사용 - 연료: {}, 연도: {}", fuelType.getName(), year);
                co2Factor = getDefaultEmissionFactor(fuelType.getName());
                ch4Factor = new BigDecimal("0.001");
                n2oFactor = new BigDecimal("0.0001");
            }

            // 4. 에너지 소비량 계산 (TJ)
            BigDecimal energyConsumption = usage.multiply(calorificValueAmount);

            // 5. 배출량 계산
            BigDecimal co2Emission = energyConsumption.multiply(co2Factor).setScale(4, RoundingMode.HALF_UP);
            BigDecimal ch4Emission = energyConsumption.multiply(ch4Factor).multiply(CH4_GWP).multiply(new BigDecimal("0.001")).setScale(4, RoundingMode.HALF_UP);
            BigDecimal n2oEmission = energyConsumption.multiply(n2oFactor).multiply(N2O_GWP).multiply(new BigDecimal("0.001")).setScale(4, RoundingMode.HALF_UP);
            BigDecimal totalEmission = co2Emission.add(ch4Emission).add(n2oEmission).setScale(4, RoundingMode.HALF_UP);

            log.debug("배출량 계산 완료 - 연료: {}, 사용량: {}, 총 배출량: {}", fuelType.getName(), usage, totalEmission);

            return EmissionResult.builder()
                    .co2Emission(co2Emission)
                    .ch4Emission(ch4Emission)
                    .n2oEmission(n2oEmission)
                    .totalEmission(totalEmission)
                    .build();

        } catch (Exception e) {
            log.error("배출량 계산 실패 - fuelId: {}, error: {}", fuelId, e.getMessage());
            return createZeroEmissionResult();
        }
    }


    /**
     * Scope 1 고정연소 배출량 계산 (fuelId 기반 전체 처리)
     */
    public EmissionResult calculateScope1StationaryEmission(String fuelId, BigDecimal usage, Integer year) {
        try {
            // 1. 연료 타입 조회
            FuelType fuelType = fuelTypeRepository.findByFuelIdAndIsActiveTrue(fuelId)
                    .orElseThrow(() -> new IllegalArgumentException("연료 ID를 찾을 수 없습니다: " + fuelId));

            // 2. 발열량 조회
            Optional<CalorificValue> calorificValueOpt = calorificValueRepository
                    .findByFuelType_IdAndYearAndIsActiveTrue(fuelType.getId(), year);

            BigDecimal calorificValueAmount = calorificValueOpt
                    .map(CalorificValue::getValue)
                    .orElseGet(() -> {
                        log.warn("발열량 정보 없음, 기본값 사용 - 연료: {}, 연도: {}", fuelType.getName(), year);
                        return getDefaultCalorificValue(fuelType.getName());
                    });

            // 3. 배출계수 조회
            Optional<EmissionFactor> emissionFactorOpt = emissionFactorRepository
                    .findByFuelType_IdAndYearAndIsActiveTrue(fuelType.getId(), year);

            BigDecimal co2Factor, ch4Factor, n2oFactor;
            if (emissionFactorOpt.isPresent()) {
                EmissionFactor factor = emissionFactorOpt.get();
                co2Factor = factor.getCo2Factor();
                ch4Factor = factor.getCh4Factor();
                n2oFactor = factor.getN2oFactor();
            } else {
                log.warn("배출계수 정보 없음, 기본값 사용 - 연료: {}, 연도: {}", fuelType.getName(), year);
                co2Factor = getDefaultEmissionFactor(fuelType.getName());
                ch4Factor = new BigDecimal("0.001");
                n2oFactor = new BigDecimal("0.0001");
            }

            // 4. 에너지 소비량 계산 (TJ)
            BigDecimal energyConsumption = usage.multiply(calorificValueAmount);

            // 5. GHG 배출량 계산
            BigDecimal co2Emission = energyConsumption.multiply(co2Factor).setScale(4, RoundingMode.HALF_UP);
            BigDecimal ch4Emission = energyConsumption.multiply(ch4Factor).multiply(CH4_GWP).multiply(new BigDecimal("0.001")).setScale(4, RoundingMode.HALF_UP);
            BigDecimal n2oEmission = energyConsumption.multiply(n2oFactor).multiply(N2O_GWP).multiply(new BigDecimal("0.001")).setScale(4, RoundingMode.HALF_UP);

            BigDecimal totalEmission = co2Emission.add(ch4Emission).add(n2oEmission).setScale(4, RoundingMode.HALF_UP);

            log.debug("고정연소 배출량 계산 완료 - 연료: {}, 사용량: {}, 총 배출량: {}", fuelType.getName(), usage, totalEmission);

            return EmissionResult.builder()
                    .co2Emission(co2Emission)
                    .ch4Emission(ch4Emission)
                    .n2oEmission(n2oEmission)
                    .totalEmission(totalEmission)
                    .build();

        } catch (Exception e) {
            log.error("고정연소 배출량 계산 실패 - fuelId: {}, error: {}", fuelId, e.getMessage());
            return createZeroEmissionResult();
        }
    }



    /**
     * Scope 1 고정연소 배출량 계산 (연도 기본값)
     */
    public BigDecimal calculateStationaryEmission(String fuelId, BigDecimal usage) {
        int currentYear = java.time.LocalDate.now().getYear();
        return calculateScope1StationaryEmission(fuelId, usage, currentYear).getTotalEmission();
    }

    /**
     * Scope 1 이동연소 배출량 계산 (fuelId 기준)
     */
    public EmissionResult calculateScope1MobileEmission(String fuelId, BigDecimal distance, Integer year) {
        try {
            FuelType fuelType = (FuelType) fuelTypeRepository.findByFuelId(fuelId)
                    .orElseThrow(() -> new IllegalArgumentException("연료 ID를 찾을 수 없습니다: " + fuelId));
            return calculateScope1Emission(fuelType.getFuelId(), distance, year);
        } catch (Exception e) {
            log.error("이동연소 배출량 계산 실패: {}", e.getMessage());
            return createZeroEmissionResult();
        }
    }

    /**
     * Scope 1 이동연소 배출량 계산 (연도 기본값)
     */
    public BigDecimal calculateMobileEmission(String fuelId, BigDecimal distance) {
        int currentYear = java.time.LocalDate.now().getYear();
        return calculateScope1MobileEmission(fuelId, distance, currentYear).getTotalEmission();
    }

    /**
     * Scope 2 전력 사용 배출량 계산
     */
    public BigDecimal calculateElectricityEmission(BigDecimal usage, Boolean isRenewable) {
        try {
            if (Boolean.TRUE.equals(isRenewable)) {
                return BigDecimal.ZERO;
            }
            BigDecimal factor = new BigDecimal("0.4653"); // kgCO2/kWh
            return usage.multiply(factor).divide(new BigDecimal("1000"), 4, RoundingMode.HALF_UP);
        } catch (Exception e) {
            log.error("전력 배출량 계산 실패: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * Scope 2 스팀 사용 배출량 계산
     */
    public BigDecimal calculateSteamEmission(BigDecimal usage, String steamType) {
        try {
            BigDecimal steamFactor;
            switch (steamType != null ? steamType.toLowerCase() : "") {
                case "고압스팀":
                    steamFactor = new BigDecimal("0.073");
                    break;
                case "중압스팀":
                    steamFactor = new BigDecimal("0.065");
                    break;
                case "저압스팀":
                    steamFactor = new BigDecimal("0.058");
                    break;
                default:
                    steamFactor = new BigDecimal("0.065");
                    break;
            }
            return usage.multiply(steamFactor).setScale(4, RoundingMode.HALF_UP);
        } catch (Exception e) {
            log.error("스팀 배출량 계산 실패: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * 계산 결과 DTO 클래스
     */
    @lombok.Getter
    @lombok.Builder
    @lombok.AllArgsConstructor
    public static class EmissionResult {
        private BigDecimal co2Emission;
        private BigDecimal ch4Emission;
        private BigDecimal n2oEmission;
        private BigDecimal totalEmission;

        public BigDecimal getTotalCo2Equivalent() {
            return totalEmission;
        }
    }

    /**
     * 예외 상황 시 0으로 초기화된 결과 반환
     */
    private EmissionResult createZeroEmissionResult() {
        return EmissionResult.builder()
                .co2Emission(BigDecimal.ZERO)
                .ch4Emission(BigDecimal.ZERO)
                .n2oEmission(BigDecimal.ZERO)
                .totalEmission(BigDecimal.ZERO)
                .build();
    }

    /**
     * 연료 이름 기반 기본 CO2 배출계수 반환
     */
    private BigDecimal getDefaultEmissionFactor(String fuelName) {
        if (fuelName == null) return new BigDecimal("2.3");

        String lower = fuelName.toLowerCase();
        if (lower.contains("가솔린")) return new BigDecimal("2.28");
        if (lower.contains("경유") || lower.contains("디젤")) return new BigDecimal("2.58");
        if (lower.contains("lpg")) return new BigDecimal("1.87");
        if (lower.contains("lng")) return new BigDecimal("2.75");
        if (lower.contains("도시가스")) return new BigDecimal("2.23");
        if (lower.contains("중유")) return new BigDecimal("3.17");

        return new BigDecimal("2.3");
    }

    /**
     * 연료 이름 기반 기본 발열량 반환
     */
    private BigDecimal getDefaultCalorificValue(String fuelName) {
        if (fuelName == null) return new BigDecimal("10.0");

        String lower = fuelName.toLowerCase();
        if (lower.contains("가솔린")) return new BigDecimal("31.0");
        if (lower.contains("경유") || lower.contains("디젤")) return new BigDecimal("35.3");
        if (lower.contains("lpg")) return new BigDecimal("25.3");
        if (lower.contains("lng")) return new BigDecimal("54.0");
        if (lower.contains("도시가스")) return new BigDecimal("43.0");
        if (lower.contains("중유")) return new BigDecimal("41.0");
        if (lower.contains("무연탄")) return new BigDecimal("25.8");
        if (lower.contains("유연탄")) return new BigDecimal("26.6");

        return new BigDecimal("10.0");
    }
}
