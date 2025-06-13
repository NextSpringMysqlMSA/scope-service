package com.nsmm.esg.scopeservice.repository;

import com.nsmm.esg.scopeservice.entity.ScopeEmission;
import com.nsmm.esg.scopeservice.entity.enums.EmissionActivityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScopeEmissionRepository extends JpaRepository<ScopeEmission, Long> {

    // 회원 + 회사 + 활동유형 + 연도 + 월로 조회 (정확히 일치하는 emission)
    List<ScopeEmission> findByMemberIdAndCompanyIdAndEmissionActivityTypeAndReportingYearAndReportingMonth(
            Long memberId,
            String companyId,
            EmissionActivityType emissionActivityType,
            Integer reportingYear,
            Integer reportingMonth
    );

    // 회원 + 회사 + 활동유형 + 연도만 입력 시 (월은 전체)
    List<ScopeEmission> findByMemberIdAndCompanyIdAndEmissionActivityTypeAndReportingYear(
            Long memberId,
            String companyId,
            EmissionActivityType emissionActivityType,
            Integer reportingYear
    );

    // 회원 + 회사 + 활동유형만 입력 시 (연도/월은 전체)
    List<ScopeEmission> findByMemberIdAndCompanyIdAndEmissionActivityType(
            Long memberId,
            String companyId,
            EmissionActivityType emissionActivityType
    );

    // 회원 + 회사만 입력 시 (활동유형/연도/월은 전체)
    List<ScopeEmission> findByMemberIdAndCompanyId(
            Long memberId,
            String companyId
    );

    // 회원 전체 emission 조회 (모든 조건 전체)
    List<ScopeEmission> findByMemberId(Long memberId);

    // id 단일 조회 (기본)
    ScopeEmission findByIdAndMemberId(Long id, Long memberId);
}
