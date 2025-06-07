package com.nsmm.esg.scopeservice.controller;

import com.nsmm.esg.scopeservice.entity.FuelType;
import com.nsmm.esg.scopeservice.entity.CalorificValue;
import com.nsmm.esg.scopeservice.entity.EmissionFactor;
import com.nsmm.esg.scopeservice.service.FuelTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/fuel-types")
@RequiredArgsConstructor
@Validated
@Tag(name = "Fuel Type Management", description = "연료 타입 관리 API")
public class FuelTypeController {

    private final FuelTypeService fuelTypeService;

    @GetMapping
    @Operation(summary = "연료 타입 목록 조회", description = "모든 연료 타입 목록을 조회합니다.")
    public ResponseEntity<List<FuelType>> getAllFuelTypes() {
        log.info("Getting all fuel types");
        List<FuelType> fuelTypes = fuelTypeService.findAll();
        return ResponseEntity.ok(fuelTypes);
    }

    @GetMapping("/paged")
    @Operation(summary = "연료 타입 페이지네이션 조회", description = "연료 타입 목록을 페이지네이션으로 조회합니다.")
    public ResponseEntity<Page<FuelType>> getFuelTypesPaged(
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        log.info("Getting fuel types with pagination: page={}, size={}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<FuelType> fuelTypes = fuelTypeService.findAll(pageable);
        return ResponseEntity.ok(fuelTypes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "연료 타입 단건 조회", description = "특정 연료 타입을 조회합니다.")
    public ResponseEntity<FuelType> getFuelType(
            @Parameter(description = "연료 타입 ID") @PathVariable Long id) {
        log.info("Getting fuel type: {}", id);
        Optional<FuelType> fuelType = fuelTypeService.findById(id);
        return fuelType.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "카테고리별 연료 타입 조회", description = "특정 카테고리의 연료 타입 목록을 조회합니다.")
    public ResponseEntity<List<FuelType>> getFuelTypesByCategory(
            @Parameter(description = "연료 카테고리") @PathVariable String category) {
        log.info("Getting fuel types by category: {}", category);
        List<FuelType> fuelTypes = fuelTypeService.findByCategory(category);
        return ResponseEntity.ok(fuelTypes);
    }

    @GetMapping("/categories")
    @Operation(summary = "연료 카테고리 목록 조회", description = "모든 연료 카테고리 목록을 조회합니다.")
    public ResponseEntity<List<String>> getCategories() {
        log.info("Getting fuel categories");
        List<String> categories = fuelTypeService.findDistinctCategories();
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    @Operation(summary = "연료 타입 생성", description = "새로운 연료 타입을 생성합니다.")
    public ResponseEntity<FuelType> createFuelType(@Valid @RequestBody FuelType fuelType) {
        log.info("Creating fuel type: {}", fuelType.getName());
        FuelType created = fuelTypeService.create(fuelType);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "연료 타입 수정", description = "연료 타입 정보를 수정합니다.")
    public ResponseEntity<FuelType> updateFuelType(
            @Parameter(description = "연료 타입 ID") @PathVariable Long id,
            @Valid @RequestBody FuelType fuelType) {
        log.info("Updating fuel type: {}", id);
        FuelType updated = fuelTypeService.update(id, fuelType);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "연료 타입 삭제", description = "연료 타입을 삭제하거나 비활성화합니다.")
    public ResponseEntity<Void> deleteFuelType(
            @Parameter(description = "연료 타입 ID") @PathVariable Long id) {
        log.info("Deleting fuel type: {}", id);
        fuelTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/calorific-value")
    @Operation(summary = "연료 타입별 발열량 조회", description = "특정 연료 타입의 발열량 정보를 조회합니다.")
    public ResponseEntity<CalorificValue> getCalorificValue(
            @Parameter(description = "연료 타입 ID") @PathVariable Long id) {
        log.info("Getting calorific value for fuel type: {}", id);
        Optional<CalorificValue> calorificValue = fuelTypeService.getCalorificValue(id);
        return calorificValue.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/emission-factor")
    @Operation(summary = "연료 타입별 배출계수 조회", description = "특정 연료 타입의 배출계수 정보를 조회합니다.")
    public ResponseEntity<EmissionFactor> getEmissionFactor(
            @Parameter(description = "연료 타입 ID") @PathVariable Long id) {
        log.info("Getting emission factor for fuel type: {}", id);
        Optional<EmissionFactor> emissionFactor = fuelTypeService.getEmissionFactor(id);
        return emissionFactor.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
