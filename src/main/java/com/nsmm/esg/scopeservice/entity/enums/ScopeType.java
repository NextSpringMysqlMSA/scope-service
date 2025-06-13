package com.nsmm.esg.scopeservice.entity.enums;

/**
 * 온실가스 배출 범위 (Scope) 타입
 */
public enum ScopeType {
    /**
     * Scope 1: 직접 온실가스 배출
     * 고정연소, 이동연소
     */
    SCOPE1,
    
    /**
     * Scope 2: 간접 온실가스 배출 (에너지)
     * 전력, 스팀 사용
     */
    SCOPE2,
    
    /**
     * Scope 3: 기타 간접 온실가스 배출
     * 현재 미구현
     */
    SCOPE3
}
