package com.nsmm.esg.scopeservice.repository;

import com.nsmm.esg.scopeservice.entity.FuelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FuelTypeRepository extends JpaRepository<FuelType, Long> {

    /**
     * 활성화된 연료 타입 조회
     */
    List<FuelType> findByIsActiveTrue();

    /**
     * 카테고리별 연료 타입 조회
     */
    List<FuelType> findByCategoryAndIsActiveTrue(String category);

    /**
     * 연료명으로 조회
     */
    Optional<FuelType> findByNameAndIsActiveTrue(String name);

    /**
     * 카테고리별 연료 타입 수 조회
     */
    @Query("SELECT COUNT(f) FROM FuelType f WHERE f.category = :category AND f.isActive = true")
    Long countByCategoryAndIsActiveTrue(@Param("category") String category);

    /**
     * 이름순으로 모든 연료 타입 조회
     */
    List<FuelType> findAllByOrderByName();

    /**
     * 카테고리별 이름순 연료 타입 조회
     */
    List<FuelType> findByCategoryOrderByName(String category);

    /**
     * 연료명 존재 여부 확인
     */
    boolean existsByName(String name);

    /**
     * 고유 카테고리 목록 조회
     */
    @Query("SELECT DISTINCT f.category FROM FuelType f WHERE f.isActive = true ORDER BY f.category")
    List<String> findDistinctCategories();
}
