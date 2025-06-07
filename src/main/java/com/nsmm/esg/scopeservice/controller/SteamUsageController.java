package com.nsmm.esg.scopeservice.controller;

import com.nsmm.esg.scopeservice.dto.SteamUsageRequest;
import com.nsmm.esg.scopeservice.dto.SteamUsageResponse;
import com.nsmm.esg.scopeservice.service.SteamUsageService;
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
@RequestMapping("/api/steam-usage")
@RequiredArgsConstructor
@Validated
@Tag(name = "Steam Usage", description = "스팀 사용량 및 배출량 관리 API")
public class SteamUsageController {

    private final SteamUsageService steamUsageService;

    @PostMapping
    @Operation(summary = "스팀 사용량 기록 생성", description = "새로운 스팀 사용량 기록을 생성합니다.")
    public ResponseEntity<SteamUsageResponse> createSteamUsage(@Valid @RequestBody SteamUsageRequest request) {
        log.info("Creating steam usage record for company: {}", request.getCompanyId());
        SteamUsageResponse response = steamUsageService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "스팀 사용량 기록 조회", description = "ID로 스팀 사용량 기록을 조회합니다.")
    public ResponseEntity<SteamUsageResponse> getSteamUsage(
            @Parameter(description = "스팀 사용량 기록 ID") @PathVariable Long id) {
        log.info("Getting steam usage record: {}", id);
        SteamUsageResponse response = steamUsageService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/company/{companyId}")
    @Operation(summary = "회사별 스팀 사용량 목록 조회", description = "특정 회사의 스팀 사용량 기록 목록을 페이지네이션으로 조회합니다.")
    public ResponseEntity<Page<SteamUsageResponse>> getSteamUsagesByCompany(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        log.info("Getting steam usage records for company: {}, page: {}, size: {}", companyId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<SteamUsageResponse> responses = steamUsageService.findByCompanyId(companyId, pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/company/{companyId}/year/{year}")
    @Operation(summary = "회사별 연도별 스팀 사용량 목록 조회", description = "특정 회사의 특정 연도 스팀 사용량 기록을 조회합니다.")
    public ResponseEntity<List<SteamUsageResponse>> getSteamUsagesByCompanyAndYear(
            @Parameter(description = "회사 ID") @PathVariable Long companyId,
            @Parameter(description = "보고연도") @PathVariable Integer year) {
        log.info("Getting steam usage records for company: {} and year: {}", companyId, year);
        List<SteamUsageResponse> responses = steamUsageService.findByCompanyIdAndYear(companyId, year);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @Operation(summary = "스팀 사용량 기록 수정", description = "스팀 사용량 기록을 수정합니다.")
    public ResponseEntity<SteamUsageResponse> updateSteamUsage(
            @Parameter(description = "스팀 사용량 기록 ID") @PathVariable Long id,
            @Valid @RequestBody SteamUsageRequest request) {
        log.info("Updating steam usage record: {}", id);
        SteamUsageResponse response = steamUsageService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "스팀 사용량 기록 삭제", description = "스팀 사용량 기록을 삭제합니다.")
    public ResponseEntity<Void> deleteSteamUsage(
            @Parameter(description = "스팀 사용량 기록 ID") @PathVariable Long id) {
        log.info("Deleting steam usage record: {}", id);
        steamUsageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/company/{companyId}/year/{year}/total")
    @Operation(summary = "회사별 연도별 스팀 사용 총 배출량 조회", description = "특정 회사의 특정 연도 스팀 사용 총 배출량을 조회합니다.")
    public ResponseEntity<BigDecimal> getTotalEmissionByCompanyAndYear(
            @Parameter(description = "회사 ID") @PathVariable @Positive Long companyId,
            @Parameter(description = "보고연도") @PathVariable Integer year) {
        log.info("Getting total steam usage emission for company: {} and year: {}", companyId, year);
        BigDecimal totalEmission = steamUsageService.getTotalEmissionByCompanyAndYear(companyId, year);
        return ResponseEntity.ok(totalEmission);
    }

    @GetMapping("/company/{companyId}/year/{year}/summary")
    @Operation(summary = "회사별 연도별 시설별 배출량 요약", description = "특정 회사의 특정 연도 시설별 배출량 요약을 조회합니다.")
    public ResponseEntity<List<Object[]>> getEmissionSummaryByFacility(
            @Parameter(description = "회사 ID") @PathVariable @Positive Long companyId,
            @Parameter(description = "보고연도") @PathVariable Integer year) {
        log.info("Getting steam usage emission summary by facility for company: {} and year: {}", companyId, year);
        List<Object[]> summary = steamUsageService.getEmissionSummaryByFacility(companyId, year);
        return ResponseEntity.ok(summary);
    }
}
