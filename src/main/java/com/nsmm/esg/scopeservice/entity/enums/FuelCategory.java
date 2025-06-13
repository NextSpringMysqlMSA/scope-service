package com.nsmm.esg.scopeservice.entity.enums;

/**
 * 연료 카테고리 분류
 * fuel-data.ts의 category 필드와 매핑
 */
public enum FuelCategory {
    /**
     * 액체연료 (석유계)
     * 휘발유, 경유, 원유, 나프타 등
     */
    LIQUID_PETROLEUM,
    
    /**
     * 고체연료 (석유계)
     * 석유코크스, 프로판, 부탄, 무연탄, 유연탄 등
     */
    SOLID_PETROLEUM,
    
    /**
     * 기체연료 (석유계)
     * LPG, LNG, 천연가스, 도시가스 등
     */
    GASEOUS_PETROLEUM,
    
    /**
     * 에너지 (전력, 스팀)
     * 전기, 스팀 등
     */
    ENERGY
}
