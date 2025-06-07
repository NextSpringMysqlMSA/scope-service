package com.nsmm.esg.scopeservice.controller;

import com.nsmm.esg.scopeservice.dto.ScopeEmissionSummaryResponse;
import com.nsmm.esg.scopeservice.service.ScopeEmissionSummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/scope-summary")
@RequiredArgsConstructor
@Validated
@Tag(name = "Scope Emission Summary", description = "Scope 1, 2 배출량 통합 요약 API")
public class ScopeEmissionSummaryController {

    private final ScopeEmissionSummaryService scopeEmissionSummaryService;

    @GetMapping("/company/{companyId}/year/{year}")
    @Operation(summary = "회사별 연도별 Scope 배출량 요약", description = "특정 회사의 특정 연도 Scope 1, 2 배출량 통합 요약을 조회합니다.")
    public ResponseEntity<ScopeEmissionSummaryResponse> getScopeEmissionSummary(
            @Parameter(description = "회사 ID") @PathVariable @Positive Long companyId,
            @Parameter(description = "보고연도") @PathVariable Integer year) {
        log.info("Getting scope emission summary for company: {} and year: {}", companyId, year);
        ScopeEmissionSummaryResponse summary = scopeEmissionSummaryService.getScopeEmissionSummary(companyId, year);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/company/{companyId}/multi-year")
    @Operation(summary = "회사별 다년도 Scope 배출량 요약", description = "특정 회사의 여러 연도 Scope 배출량 요약을 조회합니다.")
    public ResponseEntity<List<ScopeEmissionSummaryResponse>> getMultiYearSummary(
            @Parameter(description = "회사 ID") @PathVariable @Positive Long companyId,
            @Parameter(description = "보고연도 목록 (예: 2020,2021,2022)") @RequestParam List<Integer> years) {
        log.info("Getting multi-year scope emission summary for company: {} and years: {}", companyId, years);
        List<ScopeEmissionSummaryResponse> summaries = scopeEmissionSummaryService.getMultiYearSummary(companyId, years);
        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/company/{companyId}/trend")
    @Operation(summary = "회사별 Scope 배출량 트렌드", description = "특정 회사의 Scope별 배출량 트렌드 데이터를 조회합니다.")
    public ResponseEntity<Map<String, BigDecimal>> getEmissionTrendByScope(
            @Parameter(description = "회사 ID") @PathVariable @Positive Long companyId,
            @Parameter(description = "보고연도 목록 (예: 2020,2021,2022)") @RequestParam List<Integer> years) {
        log.info("Getting emission trend by scope for company: {} and years: {}", companyId, years);
        Map<String, BigDecimal> trendData = scopeEmissionSummaryService.getEmissionTrendByScope(companyId, years);
        return ResponseEntity.ok(trendData);
    }
}
