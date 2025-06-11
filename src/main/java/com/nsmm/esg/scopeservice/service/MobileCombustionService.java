//package com.nsmm.esg.scopeservice.service;
//
//import com.nsmm.esg.scopeservice.dto.MobileCombustionRequest;
//import com.nsmm.esg.scopeservice.dto.MobileCombustionResponse;
//import com.nsmm.esg.scopeservice.dto.ScopeEmissionSummaryResponse;
//import com.nsmm.esg.scopeservice.entity.MobileCombustion;
//import com.nsmm.esg.scopeservice.repository.MobileCombustionRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class MobileCombustionService {
//
//    private final MobileCombustionRepository mobileCombustionRepository;
//    private final EmissionCalculationService emissionCalculationService;
//
//    @Transactional
//    public MobileCombustionResponse createMobileCombustion(Long memberId, MobileCombustionRequest request) {
//        validateRequest(request);
//
//        MobileCombustion entity = MobileCombustion.builder()
//                .memberId(memberId)
//                .companyId(request.getCompanyId())
//                .reportingYear(request.getReportingYear())
//                .reportingMonth(request.getReportingMonth())
//                .vehicleType(request.getVehicleType())
//                .transportType(request.getTransportType())
//                .fuelId(request.getFuelId())
//                .fuelName(request.getFuelName())
//                .fuelUsage(request.getFuelUsage())
//                .distance(request.getDistance())
//                .unit(request.getUnit())
//                .createdBy(request.getCreatedBy())
//                .notes(request.getNotes())
//                .build();
//
//        calculateAndSetEmissions(entity);
//
//        MobileCombustion saved = mobileCombustionRepository.save(entity);
//        log.info("이동연소 데이터 생성 완료 - ID: {}", saved.getId());
//
//        return convertToResponse(saved);
//    }
//
//    @Transactional
//    public MobileCombustionResponse updateMobileCombustion(Long id, Long memberId, MobileCombustionRequest request) {
//        MobileCombustion entity = mobileCombustionRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("이동연소 데이터를 찾을 수 없습니다: " + id));
//
//        if (!entity.getMemberId().equals(memberId)) {
//            throw new IllegalArgumentException("해당 데이터에 대한 접근 권한이 없습니다.");
//        }
//
//        validateRequest(request);
//        entity.updateFromRequest(request);
//        calculateAndSetEmissions(entity);
//
//        MobileCombustion updated = mobileCombustionRepository.save(entity);
//        log.info("이동연소 데이터 수정 완료 - ID: {}", id);
//
//        return convertToResponse(updated);
//    }
//
//    @Transactional
//    public void deleteMobileCombustion(Long id, Long memberId) {
//        MobileCombustion entity = mobileCombustionRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("이동연소 데이터를 찾을 수 없습니다: " + id));
//
//        if (!entity.getMemberId().equals(memberId)) {
//            throw new IllegalArgumentException("해당 데이터에 대한 접근 권한이 없습니다.");
//        }
//
//        mobileCombustionRepository.delete(entity);
//        log.info("이동연소 데이터 삭제 완료 - ID: {}", id);
//    }
//
//    @Transactional(readOnly = true)
//    public MobileCombustionResponse getById(Long id, Long memberId) {
//        MobileCombustion entity = mobileCombustionRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("이동연소 데이터를 찾을 수 없습니다: " + id));
//
//        if (!entity.getMemberId().equals(memberId)) {
//            throw new IllegalArgumentException("해당 데이터에 대한 접근 권한이 없습니다.");
//        }
//
//        return convertToResponse(entity);
//    }
//
//    private void validateRequest(MobileCombustionRequest request) {
//        if (request.getCompanyId() == null || request.getCompanyId().trim().isEmpty()) {
//            throw new IllegalArgumentException("회사 ID는 필수입니다.");
//        }
//        if (request.getReportingYear() == null) {
//            throw new IllegalArgumentException("보고 연도는 필수입니다.");
//        }
//        if (request.getReportingMonth() == null) {
//            throw new IllegalArgumentException("보고 월은 필수입니다.");
//        }
//        if (request.getFuelUsage() == null || request.getFuelUsage().compareTo(BigDecimal.ZERO) <= 0) {
//            throw new IllegalArgumentException("연료 사용량은 0보다 커야 합니다.");
//        }
//        if (request.getDistance() == null || request.getDistance().compareTo(BigDecimal.ZERO) <= 0) {
//            throw new IllegalArgumentException("이동거리는 0보다 커야 합니다.");
//        }
//    }
//
//    private void calculateAndSetEmissions(MobileCombustion entity) {
//        try {
//            var emissionResult = emissionCalculationService.calculateScope1MobileEmission(
//                    entity.getFuelId(), entity.getDistance(), entity.getReportingYear());
//
//            entity.updateEmissions(
//                    emissionResult.getCo2Emission(),
//                    emissionResult.getCh4Emission(),
//                    emissionResult.getN2oEmission(),
//                    emissionResult.getTotalCo2Equivalent()
//            );
//
//            log.debug("이동연소 배출량 계산 완료 - 연료: {}, 이동거리: {}, 총 배출량: {}",
//                    entity.getFuelName(), entity.getDistance(), emissionResult.getTotalCo2Equivalent());
//
//        } catch (Exception e) {
//            log.warn("배출량 계산 실패, 기본값 사용: {}", e.getMessage());
//            entity.updateEmissions(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
//        }
//    }
//
//    private MobileCombustionResponse convertToResponse(MobileCombustion entity) {
//        return MobileCombustionResponse.builder()
//                .id(entity.getId())
//                .memberId(entity.getMemberId())
//                .companyId(entity.getCompanyId())
//                .reportingYear(entity.getReportingYear())
//                .reportingMonth(entity.getReportingMonth())
//                .vehicleType(entity.getVehicleType())
//                .transportType(entity.getTransportType())
//                .fuelId(entity.getFuelId())
//                .fuelName(entity.getFuelName())
//                .fuelUsage(entity.getFuelUsage())
//                .distance(entity.getDistance())
//                .unit(entity.getUnit())
//                .co2Emission(entity.getCo2Emission())
//                .ch4Emission(entity.getCh4Emission())
//                .n2oEmission(entity.getN2oEmission())
//                .totalCo2Equivalent(entity.getTotalCo2Equivalent())
//                .calculatedAt(entity.getCalculatedAt())
//                .createdBy(entity.getCreatedBy())
//                .notes(entity.getNotes())
//                .createdAt(entity.getCreatedAt())
//                .updatedAt(entity.getUpdatedAt())
//                .build();
//    }
//}
