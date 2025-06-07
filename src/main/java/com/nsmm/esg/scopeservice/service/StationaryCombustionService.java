package com.nsmm.esg.scopeservice.service;

import com.nsmm.esg.scopeservice.dto.StationaryCombustionRequest;
import com.nsmm.esg.scopeservice.dto.StationaryCombustionResponse;
import com.nsmm.esg.scopeservice.entity.FuelType;
import com.nsmm.esg.scopeservice.entity.StationaryCombustion;
import com.nsmm.esg.scopeservice.repository.FuelTypeRepository;
import com.nsmm.esg.scopeservice.repository.StationaryCombustionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Scope 1 고정연소 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StationaryCombustionService {

    private final StationaryCombustionRepository stationaryCombustionRepository;
    private final FuelTypeRepository fuelTypeRepository;
    private final EmissionCalculationService calculationService;

    /**
     * 고정연소 데이터 저장
     */
    @Transactional
    public Long createStationaryCombustion(Long memberId, StationaryCombustionRequest request) {
        try {
            // 1. 연료 타입 조회
            FuelType fuelType = fuelTypeRepository.findById(request.getFuelTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("연료 타입을 찾을 수 없습니다: " + request.getFuelTypeId()));

            // 2. 배출량 계산
            EmissionCalculationService.EmissionResult emissionResult = 
                    calculationService.calculateScope1Emission(request.getFuelTypeId(), request.getUsage(), request.getYear());

            // 3. 엔티티 생성 및 저장
            StationaryCombustion entity = request.toEntity(memberId);
            entity = StationaryCombustion.builder()
                    .memberId(entity.getMemberId())
                    .companyId(entity.getCompanyId())
                    .year(entity.getYear())
                    .month(entity.getMonth())
                    .fuelType(fuelType)
                    .facilityName(entity.getFacilityName())
                    .facilityType(entity.getFacilityType())
                    .usage(entity.getUsage())
                    .co2Emission(emissionResult.getCo2Emission())
                    .ch4Emission(emissionResult.getCh4Emission())
                    .n2oEmission(emissionResult.getN2oEmission())
                    .totalEmission(emissionResult.getTotalEmission())
                    .notes(entity.getNotes())
                    .build();

            StationaryCombustion savedEntity = stationaryCombustionRepository.save(entity);
            log.info("고정연소 데이터 저장 완료 - ID: {}, 배출량: {} tCO2eq", 
                    savedEntity.getId(), savedEntity.getTotalEmission());

            return savedEntity.getId();

        } catch (Exception e) {
            log.error("고정연소 데이터 저장 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("고정연소 데이터 저장 실패", e);
        }
    }

    /**
     * 회원별 고정연소 데이터 목록 조회
     */
    public List<StationaryCombustionResponse> getStationaryCombustionList(Long memberId) {
        return stationaryCombustionRepository.findByMemberIdOrderByYearDescMonthDesc(memberId).stream()
                .map(StationaryCombustionResponse::fromEntity)
                .toList();
    }

    /**
     * 회원별 특정 연도 고정연소 데이터 조회
     */
    public List<StationaryCombustionResponse> getStationaryCombustionByYear(Long memberId, Integer year) {
        return stationaryCombustionRepository.findByMemberIdAndYearOrderByMonthAsc(memberId, year).stream()
                .map(StationaryCombustionResponse::fromEntity)
                .toList();
    }

    /**
     * 회원별 연도별 고정연소 총 배출량 조회
     */
    public BigDecimal getTotalEmissionByYear(Long memberId, Integer year) {
        BigDecimal totalEmission = stationaryCombustionRepository.sumTotalEmissionByMemberIdAndYear(memberId, year);
        return totalEmission != null ? totalEmission : BigDecimal.ZERO;
    }

    /**
     * 고정연소 데이터 수정
     */
    @Transactional
    public void updateStationaryCombustion(Long id, Long memberId, StationaryCombustionRequest request) {
        try {
            // 1. 기존 데이터 조회
            StationaryCombustion entity = stationaryCombustionRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("고정연소 데이터를 찾을 수 없습니다: " + id));

            // 2. 권한 확인
            if (!entity.getMemberId().equals(memberId)) {
                throw new IllegalArgumentException("해당 데이터에 대한 접근 권한이 없습니다.");
            }

            // 3. 연료 타입 조회
            FuelType fuelType = fuelTypeRepository.findById(request.getFuelTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("연료 타입을 찾을 수 없습니다: " + request.getFuelTypeId()));

            // 4. 배출량 재계산
            EmissionCalculationService.EmissionResult emissionResult = 
                    calculationService.calculateScope1Emission(request.getFuelTypeId(), request.getUsage(), request.getYear());

            // 5. 엔티티 업데이트
            StationaryCombustion updatedEntity = StationaryCombustion.builder()
                    .id(entity.getId())
                    .memberId(entity.getMemberId())
                    .companyId(request.getCompanyId())
                    .year(request.getYear())
                    .month(request.getMonth())
                    .fuelType(fuelType)
                    .facilityName(request.getFacilityName())
                    .facilityType(request.getFacilityType())
                    .usage(request.getUsage())
                    .co2Emission(emissionResult.getCo2Emission())
                    .ch4Emission(emissionResult.getCh4Emission())
                    .n2oEmission(emissionResult.getN2oEmission())
                    .totalEmission(emissionResult.getTotalEmission())
                    .notes(request.getNotes())
                    .createdAt(entity.getCreatedAt())
                    .build();

            stationaryCombustionRepository.save(updatedEntity);
            log.info("고정연소 데이터 수정 완료 - ID: {}", id);

        } catch (Exception e) {
            log.error("고정연소 데이터 수정 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("고정연소 데이터 수정 실패", e);
        }
    }

    /**
     * 고정연소 데이터 삭제
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
}
