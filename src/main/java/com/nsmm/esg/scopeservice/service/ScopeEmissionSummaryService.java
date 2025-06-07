package com.nsmm.esg.scopeservice.service;

import com.nsmm.esg.scopeservice.dto.ScopeEmissionSummaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScopeEmissionSummaryService {

    private final StationaryCombustionService stationaryCombustionService;
    private final MobileCombustionService mobileCombustionService;
    private final ElectricityUsageService electricityUsageService;
    private final SteamUsageService steamUsageService;

    public ScopeEmissionSummaryResponse getScopeEmissionSummary(Long companyId, Integer year) {
        log.info("Getting scope emission summary for company: {} and year: {}", companyId, year);

        // Scope 1 배출량 조회
        BigDecimal stationaryEmission = stationaryCombustionService.getTotalEmissionByCompanyAndYear(companyId, year);
        BigDecimal mobileEmission = mobileCombustionService.getTotalEmissionByCompanyAndYear(companyId, year);
        BigDecimal scope1Total = stationaryEmission.add(mobileEmission);

        // Scope 2 배출량 조회
        BigDecimal electricityEmission = electricityUsageService.getTotalEmissionByCompanyAndYear(companyId, year);
        BigDecimal steamEmission = steamUsageService.getTotalEmissionByCompanyAndYear(companyId, year);
        BigDecimal scope2Total = electricityEmission.add(steamEmission);

        // 전체 배출량
        BigDecimal totalEmission = scope1Total.add(scope2Total);

        // 상세 분석 데이터 생성
        Map<String, Object> scope1Details = createScope1Details(companyId, year, stationaryEmission, mobileEmission);
        Map<String, Object> scope2Details = createScope2Details(companyId, year, electricityEmission, steamEmission);

        return ScopeEmissionSummaryResponse.builder()
                .companyId(companyId)
                .reportingYear(year)
                .scope1TotalEmission(scope1Total)
                .scope2TotalEmission(scope2Total)
                .totalEmission(totalEmission)
                .scope1Details(scope1Details)
                .scope2Details(scope2Details)
                .calculatedAt(LocalDateTime.now())
                .build();
    }

    public List<ScopeEmissionSummaryResponse> getMultiYearSummary(Long companyId, List<Integer> years) {
        log.info("Getting multi-year scope emission summary for company: {} and years: {}", companyId, years);

        List<ScopeEmissionSummaryResponse> summaries = new ArrayList<>();
        for (Integer year : years) {
            summaries.add(getScopeEmissionSummary(companyId, year));
        }
        return summaries;
    }

    public Map<String, BigDecimal> getEmissionTrendByScope(Long companyId, List<Integer> years) {
        log.info("Getting emission trend by scope for company: {} and years: {}", companyId, years);

        Map<String, BigDecimal> trendData = new HashMap<>();
        
        for (Integer year : years) {
            ScopeEmissionSummaryResponse summary = getScopeEmissionSummary(companyId, year);
            trendData.put(year + "_scope1", summary.getScope1TotalEmission());
            trendData.put(year + "_scope2", summary.getScope2TotalEmission());
            trendData.put(year + "_total", summary.getTotalEmission());
        }
        
        return trendData;
    }

    private Map<String, Object> createScope1Details(Long companyId, Integer year, 
                                                   BigDecimal stationaryEmission, BigDecimal mobileEmission) {
        Map<String, Object> details = new HashMap<>();
        details.put("stationaryEmission", stationaryEmission);
        details.put("mobileEmission", mobileEmission);
        
        // 시설별 고정연소 요약
        List<Object[]> stationarySummary = stationaryCombustionService.getEmissionSummaryByFacility(companyId, year);
        details.put("stationaryByFacility", stationarySummary);
        
        // 차량 유형별 모바일 연소 요약
        List<Object[]> mobileSummary = mobileCombustionService.getEmissionSummaryByVehicleType(companyId, year);
        details.put("mobileByVehicleType", mobileSummary);
        
        return details;
    }

    private Map<String, Object> createScope2Details(Long companyId, Integer year,
                                                   BigDecimal electricityEmission, BigDecimal steamEmission) {
        Map<String, Object> details = new HashMap<>();
        details.put("electricityEmission", electricityEmission);
        details.put("steamEmission", steamEmission);
        
        // 시설별 전력 사용량 요약
        List<Object[]> electricitySummary = electricityUsageService.getEmissionSummaryByFacility(companyId, year);
        details.put("electricityByFacility", electricitySummary);
        
        // 시설별 스팀 사용량 요약
        List<Object[]> steamSummary = steamUsageService.getEmissionSummaryByFacility(companyId, year);
        details.put("steamByFacility", steamSummary);
        
        return details;
    }
}
