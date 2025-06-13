package com.nsmm.esg.scopeservice.repository;

import com.nsmm.esg.scopeservice.entity.ScopeEmission;
import com.nsmm.esg.scopeservice.entity.enums.EmissionActivityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScopeEmissionRepository extends JpaRepository<ScopeEmission, Long> {

    // 1. 회원 + 회사 + 활동유형 + 연도 + 월
    List<ScopeEmission> findByMemberIdAndCompanyIdAndActivityTypeAndPeriod(
            Long memberId,
            String companyId,
            EmissionActivityType emissionActivityType,
            Integer reportingYear,
            Integer reportingMonth
    );

    // 2. 회원 + 회사 + 활동유형
    List<ScopeEmission> findByMemberIdAndCompanyIdAndEmissionActivityType(
            Long memberId,
            String companyId,
            EmissionActivityType emissionActivityType
    );

    // 3. 회원 + 회사 + 연도
    List<ScopeEmission> findByMemberIdAndCompanyIdAndReportingYear(
            Long memberId,
            String companyId,
            Integer reportingYear
    );

    // 4. 회원 + 회사
    List<ScopeEmission> findByMemberIdAndCompanyId(
            Long memberId,
            String companyId
    );

    // 5. 회원만
    List<ScopeEmission> findByMemberId(Long memberId);
}
