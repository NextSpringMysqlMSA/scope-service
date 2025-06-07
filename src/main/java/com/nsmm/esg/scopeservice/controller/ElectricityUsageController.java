package com.nsmm.esg.scopeservice.controller;

import com.nsmm.esg.scopeservice.dto.ElectricityUsageRequest;
import com.nsmm.esg.scopeservice.dto.ElectricityUsageResponse;
import com.nsmm.esg.scopeservice.service.ElectricityUsageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Scope 2 전력 사용 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/scope/electricity-usage")
public class ElectricityUsageController {

    private final ElectricityUsageService electricityUsageService;

    /**
     * X-MEMBER-ID 헤더에서 회원 ID 추출
     */
    private Long extractMemberId(HttpServletRequest request) {
        String memberIdHeader = request.getHeader("X-MEMBER-ID");
        if (memberIdHeader == null || memberIdHeader.isBlank()) {
            return 1L; // 개발용 기본값
        }
        return Long.parseLong(memberIdHeader);
    }

    /**
     * 전력 사용 데이터 등록
     */
    @PostMapping
    public ResponseEntity<Long> createElectricityUsage(
            @Valid @RequestBody ElectricityUsageRequest request,
            HttpServletRequest httpRequest) {
        
        Long memberId = extractMemberId(httpRequest);
        Long id = electricityUsageService.createElectricityUsage(memberId, request);
        return ResponseEntity.ok(id);
    }

    /**
     * 전력 사용 데이터 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<ElectricityUsageResponse>> getElectricityUsageList(
            HttpServletRequest httpRequest) {
        
        Long memberId = extractMemberId(httpRequest);
        List<ElectricityUsageResponse> responses = electricityUsageService.getElectricityUsageList(memberId);
        return ResponseEntity.ok(responses);
    }

    /**
     * 특정 연도 전력 사용 데이터 조회
     */
    @GetMapping("/year/{year}")
    public ResponseEntity<List<ElectricityUsageResponse>> getElectricityUsageByYear(
            @PathVariable Integer year,
            HttpServletRequest httpRequest) {
        
        Long memberId = extractMemberId(httpRequest);
        List<ElectricityUsageResponse> responses = electricityUsageService.getElectricityUsageByYear(memberId, year);
        return ResponseEntity.ok(responses);
    }

    /**
     * 연도별 총 배출량 조회
     */
    @GetMapping("/total-emission/year/{year}")
    public ResponseEntity<BigDecimal> getTotalEmissionByYear(
            @PathVariable Integer year,
            HttpServletRequest httpRequest) {
        
        Long memberId = extractMemberId(httpRequest);
        BigDecimal totalEmission = electricityUsageService.getTotalEmissionByYear(memberId, year);
        return ResponseEntity.ok(totalEmission);
    }

    /**
     * 연도별 총 사용량 조회
     */
    @GetMapping("/total-usage/year/{year}")
    public ResponseEntity<BigDecimal> getTotalUsageByYear(
            @PathVariable Integer year,
            HttpServletRequest httpRequest) {
        
        Long memberId = extractMemberId(httpRequest);
        BigDecimal totalUsage = electricityUsageService.getTotalUsageByYear(memberId, year);
        return ResponseEntity.ok(totalUsage);
    }

    /**
     * 전력 사용 데이터 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateElectricityUsage(
            @PathVariable Long id,
            @Valid @RequestBody ElectricityUsageRequest request,
            HttpServletRequest httpRequest) {
        
        Long memberId = extractMemberId(httpRequest);
        electricityUsageService.updateElectricityUsage(id, memberId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 전력 사용 데이터 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteElectricityUsage(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        
        Long memberId = extractMemberId(httpRequest);
        electricityUsageService.deleteElectricityUsage(id, memberId);
        return ResponseEntity.ok().build();
    }
}
