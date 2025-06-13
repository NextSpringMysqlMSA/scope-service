package com.nsmm.esg.scopeservice.repository;

import com.nsmm.esg.scopeservice.entity.FuelData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuelDataRepository extends JpaRepository<FuelData, String> {
    // 연료 기준정보는 fuelId(PK)로 단건 조회만 있으면 충분
    // 필요시 연료명, 카테고리별, 활동유형별 등 확장 가능
}
