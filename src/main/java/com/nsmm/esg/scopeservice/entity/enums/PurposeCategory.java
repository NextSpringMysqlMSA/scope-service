package com.nsmm.esg.scopeservice.entity.enums;

/**
 * 용도 구분 카테고리
 * 고정연소 및 이동연소에서 CH4/N2O 배출계수 선택 시 사용
 */
public enum PurposeCategory {
    /**
     * 에너지산업
     */
    ENERGY,
    
    /**
     * 제조업/건설업
     */
    MANUFACTURING,
    
    /**
     * 상업/공공
     */
    COMMERCIAL,
    
    /**
     * 가정/기타
     */
    DOMESTIC
}
