//package com.nsmm.esg.scopeservice.controller;
//
//import com.nsmm.esg.scopeservice.dto.MobileCombustionRequest;
//import com.nsmm.esg.scopeservice.dto.MobileCombustionResponse;
//import com.nsmm.esg.scopeservice.dto.ScopeEmissionSummaryResponse;
//import com.nsmm.esg.scopeservice.service.MobileCombustionService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Map;
//
///**
// * Scope 1 이동연소 컨트롤러
// * 프론트엔드 ScopeModal과 scope.ts 서비스에서 사용하는 모든 API를 제공합니다.
// * 협력사별 조회, 연도별 필터링, 연간 배출량 집계 차트용 API를 포함합니다.
// */
//@Tag(name = "MobileCombustion", description = "Scope 1 이동연소 배출량 관리 API")
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/v1/scope/mobile-combustion")
//public class MobileCombustionController {
//
//    private final MobileCombustionService mobileCombustionService;
//
//    /**
//     * X-MEMBER-ID 헤더에서 회원 ID 추출
//     */
//    private Long extractMemberId(HttpServletRequest request) {
//        String memberIdHeader = request.getHeader("X-MEMBER-ID");
//        if (memberIdHeader == null || memberIdHeader.isBlank()) {
//            return 1L; // 개발용 기본값
//        }
//        return Long.parseLong(memberIdHeader);
//    }
//
//    // =============================================================================
//    // 핵심 CRUD API - ScopeModal에서 사용
//    // =============================================================================
//
//    @Operation(summary = "이동연소 데이터 생성", description = "ScopeModal에서 전송된 이동연소 데이터를 생성하고 배출량을 계산합니다.")
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "이동연소 데이터 생성 성공"),
//        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
//        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
//    })
//    @PostMapping
//    public ResponseEntity<MobileCombustionResponse> createMobileCombustion(
//            @Parameter(description = "이동연소 요청 데이터", required = true)
//            @Valid @RequestBody MobileCombustionRequest request,
//            HttpServletRequest httpRequest) {
//
//        Long memberId = extractMemberId(httpRequest);
//        MobileCombustionResponse response = mobileCombustionService.createMobileCombustion(memberId, request);
//        return ResponseEntity.ok(response);
//    }
//
//    @Operation(summary = "이동연소 데이터 수정", description = "기존 이동연소 데이터를 수정하고 배출량을 재계산합니다.")
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "수정 성공"),
//        @ApiResponse(responseCode = "404", description = "데이터를 찾을 수 없음"),
//        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
//    })
//    @PutMapping("/{id}")
//    public ResponseEntity<MobileCombustionResponse> updateMobileCombustion(
//            @Parameter(description = "이동연소 데이터 ID", required = true, example = "1")
//            @PathVariable Long id,
//            @Parameter(description = "수정할 이동연소 요청 데이터", required = true)
//            @Valid @RequestBody MobileCombustionRequest request,
//            HttpServletRequest httpRequest) {
//
//        Long memberId = extractMemberId(httpRequest);
//        MobileCombustionResponse response = mobileCombustionService.updateMobileCombustion(id, memberId, request);
//        return ResponseEntity.ok(response);
//    }
//
//    @Operation(summary = "이동연소 데이터 삭제", description = "특정 이동연소 데이터를 삭제합니다.")
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "204", description = "삭제 성공"),
//        @ApiResponse(responseCode = "404", description = "데이터를 찾을 수 없음")
//    })
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteMobileCombustion(
//            @Parameter(description = "이동연소 데이터 ID", required = true, example = "1")
//            @PathVariable Long id,
//            HttpServletRequest httpRequest) {
//
//        Long memberId = extractMemberId(httpRequest);
//        mobileCombustionService.deleteMobileCombustion(id, memberId);
//        return ResponseEntity.noContent().build();
//    }
//
//    @Operation(summary = "이동연소 데이터 상세 조회", description = "특정 이동연소 데이터의 상세 정보를 조회합니다.")
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "조회 성공"),
//        @ApiResponse(responseCode = "404", description = "데이터를 찾을 수 없음")
//    })
//    @GetMapping("/{id}")
//    public ResponseEntity<MobileCombustionResponse> getMobileCombustionById(
//            @Parameter(description = "이동연소 데이터 ID", required = true, example = "1")
//            @PathVariable Long id,
//            HttpServletRequest httpRequest) {
//
//        Long memberId = extractMemberId(httpRequest);
//        MobileCombustionResponse response = mobileCombustionService.getById(id, memberId);
//        return ResponseEntity.ok(response);
//    }
//
//    // =============================================================================
//    // 협력사별 조회 API - scope.ts에서 사용 (핵심)
//    // =============================================================================
//
//    @Operation(summary = "협력사별 연도별 이동연소 데이터 조회",
//               description = "프론트엔드 scope.ts의 fetchMobileCombustionByPartnerAndYear에서 사용하는 핵심 API입니다.")
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "조회 성공"),
//        @ApiResponse(responseCode = "404", description = "데이터를 찾을 수 없음")
//    })
//    @GetMapping("/partner/{partnerCompanyId}/year/{year}")
//    public ResponseEntity<List<MobileCombustionResponse>> getMobileCombustionByPartnerAndYear(
//            @Parameter(description = "협력사 ID (UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
//            @PathVariable String partnerCompanyId,
//            @Parameter(description = "보고 연도", required = true, example = "2024")
//            @PathVariable Integer year,
//            HttpServletRequest httpRequest) {
//
//        Long memberId = extractMemberId(httpRequest);
//        List<MobileCombustionResponse> responses = mobileCombustionService.getByPartnerAndYear(memberId, partnerCompanyId, year);
//        return ResponseEntity.ok(responses);
//    }
//
//    @Operation(summary = "협력사별 이동연소 데이터 전체 조회", description = "특정 협력사의 모든 이동연소 데이터를 조회합니다.")
//    @GetMapping("/partner/{partnerCompanyId}")
//    public ResponseEntity<List<MobileCombustionResponse>> getMobileCombustionByPartner(
//            @Parameter(description = "협력사 ID (UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
//            @PathVariable String partnerCompanyId,
//            HttpServletRequest httpRequest) {
//
//        Long memberId = extractMemberId(httpRequest);
//        List<MobileCombustionResponse> responses = mobileCombustionService.getByPartner(memberId, partnerCompanyId);
//        return ResponseEntity.ok(responses);
//    }
//
//    // =============================================================================
//    // 연간 배출량 집계 및 차트용 API
//    // =============================================================================
//
//    @Operation(summary = "월별 배출량 집계", description = "특정 연도의 월별 이동연소 배출량을 집계합니다. 연간 배출량 차트에 사용됩니다.")
//    @GetMapping("/summary/monthly")
//    public ResponseEntity<List<ScopeEmissionSummaryResponse>> getMonthlyEmissionSummary(
//            @Parameter(description = "보고 연도", required = true, example = "2024")
//            @RequestParam Integer year,
//            @Parameter(description = "협력사 ID (선택사항)", example = "550e8400-e29b-41d4-a716-446655440000")
//            @RequestParam(required = false) String partnerCompanyId,
//            HttpServletRequest httpRequest) {
//
//        Long memberId = extractMemberId(httpRequest);
//        List<ScopeEmissionSummaryResponse> summaries = mobileCombustionService.getMonthlyEmissionSummary(memberId, year, partnerCompanyId);
//        return ResponseEntity.ok(summaries);
//    }
//
//    @Operation(summary = "연료별 배출량 집계", description = "특정 연도의 연료 타입별 이동연소 배출량을 집계합니다.")
//    @GetMapping("/summary/by-fuel")
//    public ResponseEntity<List<ScopeEmissionSummaryResponse>> getEmissionSummaryByFuel(
//            @Parameter(description = "보고 연도", required = true, example = "2024")
//            @RequestParam Integer year,
//            @Parameter(description = "협력사 ID (선택사항)", example = "550e8400-e29b-41d4-a716-446655440000")
//            @RequestParam(required = false) String partnerCompanyId,
//            HttpServletRequest httpRequest) {
//
//        Long memberId = extractMemberId(httpRequest);
//        List<ScopeEmissionSummaryResponse> summaries = mobileCombustionService.getEmissionSummaryByFuel(memberId, year, partnerCompanyId);
//        return ResponseEntity.ok(summaries);
//    }
//
//    @Operation(summary = "시설별 배출량 집계", description = "특정 연도의 시설별 이동연소 배출량을 집계합니다.")
//    @GetMapping("/summary/by-facility")
//    public ResponseEntity<List<ScopeEmissionSummaryResponse>> getEmissionSummaryByFacility(
//            @Parameter(description = "보고 연도", required = true, example = "2024")
//            @RequestParam Integer year,
//            @Parameter(description = "협력사 ID (선택사항)", example = "550e8400-e29b-41d4-a716-446655440000")
//            @RequestParam(required = false) String partnerCompanyId,
//            HttpServletRequest httpRequest) {
//
//        Long memberId = extractMemberId(httpRequest);
//        List<ScopeEmissionSummaryResponse> summaries = mobileCombustionService.getEmissionSummaryByFacility(memberId, year, partnerCompanyId);
//        return ResponseEntity.ok(summaries);
//    }
//
//    @Operation(summary = "협력사별 배출량 집계", description = "특정 연도의 협력사별 이동연소 배출량을 집계합니다. 대시보드 차트에 사용됩니다.")
//    @GetMapping("/summary/by-partner")
//    public ResponseEntity<List<ScopeEmissionSummaryResponse>> getEmissionSummaryByPartner(
//            @Parameter(description = "보고 연도", required = true, example = "2024")
//            @RequestParam Integer year,
//            HttpServletRequest httpRequest) {
//
//        Long memberId = extractMemberId(httpRequest);
//        List<ScopeEmissionSummaryResponse> summaries = mobileCombustionService.getEmissionSummaryByPartner(memberId, year);
//        return ResponseEntity.ok(summaries);
//    }
//
//    @Operation(summary = "차량별 배출량 집계", description = "특정 연도의 차량 타입별 이동연소 배출량을 집계합니다.")
//    @GetMapping("/summary/by-vehicle")
//    public ResponseEntity<List<ScopeEmissionSummaryResponse>> getEmissionSummaryByVehicle(
//            @Parameter(description = "보고 연도", required = true, example = "2024")
//            @RequestParam Integer year,
//            @Parameter(description = "협력사 ID (선택사항)", example = "550e8400-e29b-41d4-a716-446655440000")
//            @RequestParam(required = false) String partnerCompanyId,
//            HttpServletRequest httpRequest) {
//
//        Long memberId = extractMemberId(httpRequest);
//        List<ScopeEmissionSummaryResponse> summaries = mobileCombustionService.getEmissionSummaryByVehicle(memberId, year, partnerCompanyId);
//        return ResponseEntity.ok(summaries);
//    }
//
//    @Operation(summary = "연도별 총 배출량 조회", description = "특정 연도의 총 이동연소 배출량을 조회합니다.")
//    @GetMapping("/total-emission/year/{year}")
//    public ResponseEntity<BigDecimal> getTotalEmissionByYear(
//            @Parameter(description = "보고 연도", required = true, example = "2024")
//            @PathVariable Integer year,
//            @Parameter(description = "협력사 ID (선택사항)", example = "550e8400-e29b-41d4-a716-446655440000")
//            @RequestParam(required = false) String partnerCompanyId,
//            HttpServletRequest httpRequest) {
//
//        Long memberId = extractMemberId(httpRequest);
//        BigDecimal totalEmission = mobileCombustionService.getTotalEmissionByYear(memberId, year, partnerCompanyId);
//        return ResponseEntity.ok(totalEmission);
//    }
//
//    // =============================================================================
//    // 기본 조회 API
//    // =============================================================================
//
//    @Operation(summary = "이동연소 데이터 전체 목록 조회", description = "회원의 모든 이동연소 데이터를 조회합니다.")
//    @GetMapping
//    public ResponseEntity<List<MobileCombustionResponse>> getAllMobileCombustion(
//            HttpServletRequest httpRequest) {
//
//        Long memberId = extractMemberId(httpRequest);
//        List<MobileCombustionResponse> responses = mobileCombustionService.getAllByMember(memberId);
//        return ResponseEntity.ok(responses);
//    }
//
//    @Operation(summary = "연도별 이동연소 데이터 조회", description = "특정 연도의 모든 이동연소 데이터를 조회합니다.")
//    @GetMapping("/year/{year}")
//    public ResponseEntity<List<MobileCombustionResponse>> getMobileCombustionByYear(
//            @Parameter(description = "보고 연도", required = true, example = "2024")
//            @PathVariable Integer year,
//            HttpServletRequest httpRequest) {
//
//        Long memberId = extractMemberId(httpRequest);
//        List<MobileCombustionResponse> responses = mobileCombustionService.getByYear(memberId, year);
//        return ResponseEntity.ok(responses);
//    }
//
//    // =============================================================================
//    // 대시보드 통계 API
//    // =============================================================================
//
//    @Operation(summary = "대시보드용 이동연소 통계", description = "대시보드에서 사용할 이동연소 관련 통계 정보를 제공합니다.")
//    @GetMapping("/dashboard/stats")
//    public ResponseEntity<Map<String, Object>> getDashboardStats(
//            @Parameter(description = "보고 연도", required = true, example = "2024")
//            @RequestParam Integer year,
//            HttpServletRequest httpRequest) {
//
//        Long memberId = extractMemberId(httpRequest);
//        Map<String, Object> stats = mobileCombustionService.getDashboardStats(memberId, year);
//        return ResponseEntity.ok(stats);
//    }
//}
