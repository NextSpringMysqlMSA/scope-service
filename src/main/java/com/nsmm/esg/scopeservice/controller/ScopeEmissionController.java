package com.nsmm.esg.scopeservice.controller;

import com.nsmm.esg.scopeservice.dto.request.ScopeEmissionRequest;
import com.nsmm.esg.scopeservice.dto.response.ScopeEmissionResponse;
import com.nsmm.esg.scopeservice.entity.enums.EmissionActivityType;
import com.nsmm.esg.scopeservice.service.ScopeEmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scope-emissions")
@RequiredArgsConstructor
public class ScopeEmissionController {

    private final ScopeEmissionService scopeEmissionService;

    // ===== CREATE (등록) =====
    @PostMapping("/{activityType}")
    public ResponseEntity<ScopeEmissionResponse> createEmission(
            @RequestHeader("X-MEMBER-ID") Long memberId, // Gateway에서 헤더로 전달
            @PathVariable EmissionActivityType activityType, // 활동유형(고정/이동/전력/스팀)
            @RequestParam("companyId") String companyId, // 회사 UUID(프론트 파라미터)
            @RequestBody ScopeEmissionRequest requestDto // 모달 입력값
    ) {
        ScopeEmissionResponse response = scopeEmissionService.createEmission(
                memberId, companyId, activityType, requestDto
        );
        return ResponseEntity.ok(response);
    }

    // ===== READ (단일 조회) =====
    @GetMapping("/{id}")
    public ResponseEntity<ScopeEmissionResponse> getEmission(
            @RequestHeader("X-MEMBER-ID") Long memberId,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(scopeEmissionService.getEmission(memberId, id));
    }

    // ===== READ (목록조회 - 회사별/연도/월별 등 필터) =====
    @GetMapping
    public ResponseEntity<List<ScopeEmissionResponse>> getEmissions(
            @RequestHeader("X-MEMBER-ID") Long memberId,
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) EmissionActivityType activityType,
            @RequestParam(required = false) Integer reportingYear,
            @RequestParam(required = false) Integer reportingMonth
    ) {
        List<ScopeEmissionResponse> result = scopeEmissionService.getEmissions(
                memberId, companyId, activityType, reportingYear, reportingMonth
        );
        return ResponseEntity.ok(result);
    }

    // ===== UPDATE (수정) =====
    @PutMapping("/{id}")
    public ResponseEntity<ScopeEmissionResponse> updateEmission(
            @RequestHeader("X-MEMBER-ID") Long memberId,
            @PathVariable Long id,
            @RequestBody ScopeEmissionRequest requestDto
    ) {
        ScopeEmissionResponse response = scopeEmissionService.updateEmission(memberId, id, requestDto);
        return ResponseEntity.ok(response);
    }

    // ===== DELETE (삭제) =====
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmission(
            @RequestHeader("X-MEMBER-ID") Long memberId,
            @PathVariable Long id
    ) {
        scopeEmissionService.deleteEmission(memberId, id);
        return ResponseEntity.noContent().build();
    }
}
