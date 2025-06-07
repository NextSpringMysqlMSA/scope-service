package com.nsmm.esg.scopeservice.controller;

import com.nsmm.esg.scopeservice.dto.StationaryCombustionRequest;
import com.nsmm.esg.scopeservice.dto.StationaryCombustionResponse;
import com.nsmm.esg.scopeservice.service.StationaryCombustionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Scope 1 고정연소 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/scope/stationary-combustion")
public class StationaryCombustionController {

    private final StationaryCombustionService stationaryCombustionService;

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
     * 고정연소 데이터 등록
     */
    @PostMapping
    public ResponseEntity<Long> createStationaryCombustion(
            @Valid @RequestBody StationaryCombustionRequest request,
            HttpServletRequest httpRequest) {
        
        Long memberId = extractMemberId(httpRequest);
        Long id = stationaryCombustionService.createStationaryCombustion(memberId, request);
        return ResponseEntity.ok(id);
    }

    /**
     * 고정연소 데이터 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<StationaryCombustionResponse>> getStationaryCombustionList(
            HttpServletRequest httpRequest) {
        
        Long memberId = extractMemberId(httpRequest);
        List<StationaryCombustionResponse> responses = stationaryCombustionService.getStationaryCombustionList(memberId);
        return ResponseEntity.ok(responses);
    }

    /**
     * 특정 연도 고정연소 데이터 조회
     */
    @GetMapping("/year/{year}")
    public ResponseEntity<List<StationaryCombustionResponse>> getStationaryCombustionByYear(
            @PathVariable Integer year,
            HttpServletRequest httpRequest) {
        
        Long memberId = extractMemberId(httpRequest);
        List<StationaryCombustionResponse> responses = stationaryCombustionService.getStationaryCombustionByYear(memberId, year);
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
        BigDecimal totalEmission = stationaryCombustionService.getTotalEmissionByYear(memberId, year);
        return ResponseEntity.ok(totalEmission);
    }

    /**
     * 고정연소 데이터 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateStationaryCombustion(
            @PathVariable Long id,
            @Valid @RequestBody StationaryCombustionRequest request,
            HttpServletRequest httpRequest) {
        
        Long memberId = extractMemberId(httpRequest);
        stationaryCombustionService.updateStationaryCombustion(id, memberId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 고정연소 데이터 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStationaryCombustion(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        
        Long memberId = extractMemberId(httpRequest);
        stationaryCombustionService.deleteStationaryCombustion(id, memberId);
        return ResponseEntity.ok().build();
    }
}
