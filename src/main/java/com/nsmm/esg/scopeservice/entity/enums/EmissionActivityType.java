package com.nsmm.esg.scopeservice.entity.enums;

/**
 * 배출 활동 유형
 * Scope 1: 직접 배출 (고정연소, 이동연소)
 * Scope 2: 간접 배출 (전력, 스팀)
 */
public enum EmissionActivityType {
    /**
     * 고정연소 (Scope 1)
     * 보일러, 발전기 등 고정 설비에서의 연료 연소
     */
    STATIONARY_COMBUSTION,
    
    /**
     * 이동연소 (Scope 1)
     * 차량, 선박, 항공기 등 이동수단의 연료 연소
     */
    MOBILE_COMBUSTION,
    
    /**
     * 전력사용 (Scope 2)
     * 외부에서 구매한 전력 사용
     */
    ELECTRICITY,
    
    /**
     * 스팀사용 (Scope 2)
     * 외부에서 구매한 스팀 사용
     */
    STEAM
}
