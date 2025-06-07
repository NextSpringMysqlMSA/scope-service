package com.nsmm.esg.scopeservice.service;

import com.nsmm.esg.scopeservice.entity.FuelType;
import com.nsmm.esg.scopeservice.entity.CalorificValue;
import com.nsmm.esg.scopeservice.entity.EmissionFactor;
import com.nsmm.esg.scopeservice.repository.FuelTypeRepository;
import com.nsmm.esg.scopeservice.repository.CalorificValueRepository;
import com.nsmm.esg.scopeservice.repository.EmissionFactorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FuelTypeService {

    private final FuelTypeRepository fuelTypeRepository;
    private final CalorificValueRepository calorificValueRepository;
    private final EmissionFactorRepository emissionFactorRepository;

    public List<FuelType> findAll() {
        return fuelTypeRepository.findAllByOrderByName();
    }

    public Page<FuelType> findAll(Pageable pageable) {
        return fuelTypeRepository.findAll(pageable);
    }

    public Optional<FuelType> findById(Long id) {
        return fuelTypeRepository.findById(id);
    }

    public FuelType findByIdOrThrow(Long id) {
        return fuelTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("연료 타입을 찾을 수 없습니다: " + id));
    }

    public List<FuelType> findByCategory(String category) {
        return fuelTypeRepository.findByCategoryOrderByName(category);
    }

    public List<String> findDistinctCategories() {
        return fuelTypeRepository.findDistinctCategories();
    }

    @Transactional
    public FuelType create(FuelType fuelType) {
        log.info("Creating new fuel type: {}", fuelType.getName());
        
        // 중복 이름 체크
        if (fuelTypeRepository.existsByName(fuelType.getName())) {
            throw new IllegalArgumentException("이미 존재하는 연료 타입입니다: " + fuelType.getName());
        }
        
        return fuelTypeRepository.save(fuelType);
    }

    @Transactional
    public FuelType update(Long id, FuelType fuelType) {
        log.info("Updating fuel type: {}", id);
        
        FuelType existingFuelType = findByIdOrThrow(id);
        
        // 이름이 변경되는 경우 중복 체크
        if (!existingFuelType.getName().equals(fuelType.getName()) && 
            fuelTypeRepository.existsByName(fuelType.getName())) {
            throw new IllegalArgumentException("이미 존재하는 연료 타입입니다: " + fuelType.getName());
        }
        
        existingFuelType.setName(fuelType.getName());
        existingFuelType.setCategory(fuelType.getCategory());
        existingFuelType.setDescription(fuelType.getDescription());
        existingFuelType.setUnit(fuelType.getUnit());
        existingFuelType.setIsActive(fuelType.getIsActive());
        
        return fuelTypeRepository.save(existingFuelType);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting fuel type: {}", id);
        
        FuelType fuelType = findByIdOrThrow(id);
        
        // 사용 중인지 체크 (발열량, 배출계수가 있는지 확인)
        boolean hasCalorificValue = calorificValueRepository.existsByFuelTypeId(id);
        boolean hasEmissionFactor = emissionFactorRepository.existsByFuelTypeId(id);
        
        if (hasCalorificValue || hasEmissionFactor) {
            // 실제 삭제 대신 비활성화
            fuelType.setIsActive(false);
            fuelTypeRepository.save(fuelType);
            log.info("Fuel type deactivated instead of deleted: {}", id);
        } else {
            fuelTypeRepository.deleteById(id);
            log.info("Fuel type deleted: {}", id);
        }
    }

    public Optional<CalorificValue> getCalorificValue(Long fuelTypeId) {
        return calorificValueRepository.findByFuelTypeId(fuelTypeId);
    }

    public Optional<EmissionFactor> getEmissionFactor(Long fuelTypeId) {
        return emissionFactorRepository.findByFuelTypeId(fuelTypeId);
    }
}
