package com.nsmm.esg.scopeservice.service;

import com.nsmm.esg.scopeservice.dto.ElectricityUsageRequest;
import com.nsmm.esg.scopeservice.dto.ElectricityUsageResponse;
import com.nsmm.esg.scopeservice.entity.ElectricityUsage;
import com.nsmm.esg.scopeservice.repository.ElectricityUsageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Scope 2 전력 사용 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ElectricityUsageService {

    private final ElectricityUsageRepository electricityUsageRepository;
    private final EmissionCalculationService calculationService;

    /**
     * 전력 사용 데이터 저장
     */
    @Transactional
    public Long createElectricityUsage(Long memberId, ElectricityUsageRequest request) {
        try {
            // 1. 배출량 계산
            BigDecimal totalEmission = calculationService.calculateElectricityEmission(
                    request.getUsage(), request.getEmissionFactor());

            // 2. 엔티티 생성 및 저장
            ElectricityUsage entity = request.toEntity(memberId);
            entity = ElectricityUsage.builder()
                    .memberId(entity.getMemberId())
                    .companyId(entity.getCompanyId())
                    .year(entity.getYear())
                    .month(entity.getMonth())
                    .facilityName(entity.getFacilityName())
                    .supplier(entity.getSupplier())
                    .usage(entity.getUsage())
                    .emissionFactor(entity.getEmissionFactor())
                    .totalEmission(totalEmission)
                    .isRenewable(entity.getIsRenewable())
                    .notes(entity.getNotes())
                    .build();

            ElectricityUsage savedEntity = electricityUsageRepository.save(entity);
            log.info("전력 사용 데이터 저장 완료 - ID: {}, 배출량: {} tCO2eq", 
                    savedEntity.getId(), savedEntity.getTotalEmission());

            return savedEntity.getId();

        } catch (Exception e) {
            log.error("전력 사용 데이터 저장 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("전력 사용 데이터 저장 실패", e);
        }
    }

    /**
     * 회원별 전력 사용 데이터 목록 조회
     */
    public List<ElectricityUsageResponse> getElectricityUsageList(Long memberId) {
        return electricityUsageRepository.findByMemberIdOrderByYearDescMonthDesc(memberId).stream()
                .map(ElectricityUsageResponse::fromEntity)
                .toList();
    }

    /**
     * 회원별 특정 연도 전력 사용 데이터 조회
     */
    public List<ElectricityUsageResponse> getElectricityUsageByYear(Long memberId, Integer year) {
        return electricityUsageRepository.findByMemberIdAndYearOrderByMonthAsc(memberId, year).stream()
                .map(ElectricityUsageResponse::fromEntity)
                .toList();
    }

    /**
     * 회원별 연도별 전력 총 배출량 조회
     */
    public BigDecimal getTotalEmissionByYear(Long memberId, Integer year) {
        BigDecimal totalEmission = electricityUsageRepository.sumTotalEmissionByMemberIdAndYear(memberId, year);
        return totalEmission != null ? totalEmission : BigDecimal.ZERO;
    }

    /**
     * 회원별 연도별 전력 총 사용량 조회
     */
    public BigDecimal getTotalUsageByYear(Long memberId, Integer year) {
        BigDecimal totalUsage = electricityUsageRepository.sumUsageByMemberIdAndYear(memberId, year);
        return totalUsage != null ? totalUsage : BigDecimal.ZERO;
    }

    /**
     * 전력 사용 데이터 수정
     */
    @Transactional
    public void updateElectricityUsage(Long id, Long memberId, ElectricityUsageRequest request) {
        try {
            // 1. 기존 데이터 조회
            ElectricityUsage entity = electricityUsageRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("전력 사용 데이터를 찾을 수 없습니다: " + id));

            // 2. 권한 확인
            if (!entity.getMemberId().equals(memberId)) {
                throw new IllegalArgumentException("해당 데이터에 대한 접근 권한이 없습니다.");
            }

            // 3. 배출량 재계산
            BigDecimal totalEmission = calculationService.calculateElectricityEmission(
                    request.getUsage(), request.getEmissionFactor());

            // 4. 엔티티 업데이트
            ElectricityUsage updatedEntity = ElectricityUsage.builder()
                    .id(entity.getId())
                    .memberId(entity.getMemberId())
                    .companyId(request.getCompanyId())
                    .year(request.getYear())
                    .month(request.getMonth())
                    .facilityName(request.getFacilityName())
                    .supplier(request.getSupplier())
                    .usage(request.getUsage())
                    .emissionFactor(request.getEmissionFactor())
                    .totalEmission(totalEmission)
                    .isRenewable(request.getIsRenewable() != null ? request.getIsRenewable() : false)
                    .notes(request.getNotes())
                    .createdAt(entity.getCreatedAt())
                    .build();

            electricityUsageRepository.save(updatedEntity);
            log.info("전력 사용 데이터 수정 완료 - ID: {}", id);

        } catch (Exception e) {
            log.error("전력 사용 데이터 수정 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("전력 사용 데이터 수정 실패", e);
        }
    }

    /**
     * 전력 사용 데이터 삭제
     */
    @Transactional
    public void deleteElectricityUsage(Long id, Long memberId) {
        try {
            ElectricityUsage entity = electricityUsageRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("전력 사용 데이터를 찾을 수 없습니다: " + id));

            if (!entity.getMemberId().equals(memberId)) {
                throw new IllegalArgumentException("해당 데이터에 대한 접근 권한이 없습니다.");
            }

            electricityUsageRepository.delete(entity);
            log.info("전력 사용 데이터 삭제 완료 - ID: {}", id);

        } catch (Exception e) {
            log.error("전력 사용 데이터 삭제 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("전력 사용 데이터 삭제 실패", e);
        }
    }
}
