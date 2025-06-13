package com.nsmm.esg.scopeservice.entity.enums;

/**
 * 연료 하위 카테고리 타입
 * fuel-data.ts의 subcategoryType 필드와 매핑
 */
public enum SubcategoryType {
    /**
     * 액체연료
     * 휘발유, 경유, 원유, 나프타 등
     */
    LIQUID,
    
    /**
     * 고체연료
     * 석유코크스, 프로판, 부탄, 무연탄, 유연탄 등
     */
    SOLID,
    
    /**
     * 기체연료
     * LPG, LNG, 천연가스, 도시가스 등
     */
    GAS,
    
    /**
     * 전력
     */
    ELECTRICITY,
    
    /**
     * 스팀
     */
    STEAM
}
