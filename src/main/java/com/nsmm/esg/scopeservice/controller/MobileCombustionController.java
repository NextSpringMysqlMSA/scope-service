package com.nsmm.esg.scopeservice.controller;

import com.nsmm.esg.scopeservice.dto.MobileCombustionRequest;
import com.nsmm.esg.scopeservice.dto.MobileCombustionResponse;
import com.nsmm.esg.scopeservice.service.MobileCombustionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/mobile-combustion")
@RequiredArgsConstructor
@Validated
@Tag(name = "Mobile Combustion", description = "모바일 연소 배출량 관리 API")
public class MobileCombustionController {

    private final MobileCombustionService mobileCombustionService;

    @PostMapping
    @Operation(summary = "모바일 연소 배출량 기록 생성", description = "새로운 모바일 연소 배출량 기록을 생성합니다.")
    public ResponseEntity<MobileCombustionResponse> createMobileCombustion(@Valid @RequestBody MobileCombustionRequest request) {
        log.info("Creating mobile combustion record for company: {}", request.getCompanyId());
        MobileCombustionResponse response = mobileCombustionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "모바일 연소 배출량 기록 조회", description = "ID로 모바일 연소 배출량 기록을 조회합니다.")
    public ResponseEntity<MobileCombustionResponse> getMobileCombustion(
            @Parameter(description = "모바일 연소 기록 ID") @PathVariable Long id) {
        log.info("Getting mobile combustion record: {}", id);
        MobileCombustionResponse response = mobileCombustionService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "회사별 모바일 연소 배출량 목록 조회", description = "특정 회사의 모바일 연소 배출량 기록 목록을 페이지네이션으로 조회합니다.")
    public ResponseEntity<Page<MobileCombustionResponse>> getMobileCombustionsByCompany(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        log.info("Getting mobile combustion records for company: {}, page: {}, size: {}", companyId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<MobileCombustionResponse> responses = mobileCombustionService.findByCompanyId(companyId, pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/company/{companyId}/year/{year}")
    @Operation(summary = "회사별 연도별 모바일 연소 배출량 목록 조회", description = "특정 회사의 특정 연도 모바일 연소 배출량 기록을 조회합니다.")
    public ResponseEntity<List<MobileCombustionResponse>> getMobileCombustionsByCompanyAndYear(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @Parameter(description = "보고연도") @PathVariable Integer year) {
        log.info("Getting mobile combustion records for company: {} and year: {}", companyId, year);
        List<MobileCombustionResponse> responses = mobileCombustionService.findByCompanyIdAndYear(companyId, year);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @Operation(summary = "모바일 연소 배출량 기록 수정", description = "모바일 연소 배출량 기록을 수정합니다.")
    public ResponseEntity<MobileCombustionResponse> updateMobileCombustion(
            @Parameter(description = "모바일 연소 기록 ID") @PathVariable Long id,
            @Valid @RequestBody MobileCombustionRequest request) {
        log.info("Updating mobile combustion record: {}", id);
        MobileCombustionResponse response = mobileCombustionService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "모바일 연소 배출량 기록 삭제", description = "모바일 연소 배출량 기록을 삭제합니다.")
    public ResponseEntity<Void> deleteMobileCombustion(
            @Parameter(description = "모바일 연소 기록 ID") @PathVariable Long id) {
        log.info("Deleting mobile combustion record: {}", id);
        mobileCombustionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/company/{companyId}/year/{year}/total")
    @Operation(summary = "회사별 연도별 모바일 연소 총 배출량 조회", description = "특정 회사의 특정 연도 모바일 연소 총 배출량을 조회합니다.")
    public ResponseEntity<BigDecimal> getTotalEmissionByCompanyAndYear(
            @Parameter(description = "회사 ID") @PathVariable @Positive Long companyId,
            @Parameter(description = "보고연도") @PathVariable Integer year) {
        log.info("Getting total mobile combustion emission for company: {} and year: {}", companyId, year);
        BigDecimal totalEmission = mobileCombustionService.getTotalEmissionByCompanyAndYear(companyId, year);
        return ResponseEntity.ok(totalEmission);
    }

    @GetMapping("/company/{companyId}/year/{year}/summary")
    @Operation(summary = "회사별 연도별 차량 유형별 배출량 요약", description = "특정 회사의 특정 연도 차량 유형별 배출량 요약을 조회합니다.")
    public ResponseEntity<List<Object[]>> getEmissionSummaryByVehicleType(
            @Parameter(description = "회사 ID") @PathVariable @Positive Long companyId,
            @Parameter(description = "보고연도") @PathVariable Integer year) {
        log.info("Getting mobile combustion emission summary by vehicle type for company: {} and year: {}", companyId, year);
        List<Object[]> summary = mobileCombustionService.getEmissionSummaryByVehicleType(companyId, year);
        return ResponseEntity.ok(summary);
    }
}
