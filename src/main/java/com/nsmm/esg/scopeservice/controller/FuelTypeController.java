package com.nsmm.esg.scopeservice.controller;

import com.nsmm.esg.scopeservice.entity.FuelType;
import com.nsmm.esg.scopeservice.entity.CalorificValue;
import com.nsmm.esg.scopeservice.entity.EmissionFactor;
import com.nsmm.esg.scopeservice.service.FuelTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/fuel-types")
@RequiredArgsConstructor
@Validated
@Tag(name = "Fuel Type Management", description = "에너지 타입 관리 API")
public class FuelTypeController {

    private final FuelTypeService fuelTypeService;

    @GetMapping
    @Operation(summary = "에너지 타입 목록 조회", description = "모든 에너지 타입 목록을 조회합니다.")
    public ResponseEntity<List<FuelType>> getAllFuelTypes() {
        log.info("Getting all fuel types");
        return ResponseEntity.ok(fuelTypeService.findAll());
    }

    @GetMapping("/paged")
    @Operation(summary = "에너지 타입 페이지네이션 조회", description = "에너지 타입 목록을 페이지네이션으로 조회합니다.")
    public ResponseEntity<Page<FuelType>> getFuelTypesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(fuelTypeService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "에너지 타입 단객 조회", description = "특정 에너지 타입을 조회합니다.")
    public ResponseEntity<FuelType> getFuelTypeById(@PathVariable Long id) {
        return fuelTypeService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/fuel-id/{fuelId}")
    @Operation(summary = "fuelId로 조회", description = "fuelId 기본의 에너지 타입을 조회합니다.")
    public ResponseEntity<FuelType> getFuelTypeByFuelId(@PathVariable String fuelId) {
        return fuelTypeService.findByFuelId(fuelId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "카테고리별 조회", description = "특정 카테고리의 에너지 타입 목록을 조회합니다.")
    public ResponseEntity<List<FuelType>> getFuelTypesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(fuelTypeService.findByCategory(category));
    }

    @GetMapping("/categories")
    @Operation(summary = "카테고리 목록 조회", description = "모든 에너지 카테고리 목록을 조회합니다.")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(fuelTypeService.findDistinctCategories());
    }

    @PostMapping
    @Operation(summary = "새 에너지 타입 생성", description = "새로운 에너지 타입을 생성합니다.")
    public ResponseEntity<FuelType> createFuelType(@Valid @RequestBody FuelType fuelType) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fuelTypeService.create(fuelType));
    }

    @PutMapping("/{id}")
    @Operation(summary = "에너지 타입 수정", description = "에너지 타입 정보를 수정합니다.")
    public ResponseEntity<FuelType> updateFuelType(@PathVariable Long id, @Valid @RequestBody FuelType fuelType) {
        return ResponseEntity.ok(fuelTypeService.update(id, fuelType));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "에너지 타입 삭제", description = "에너지 타입을 삭제하거나 비활성화합니다.")
    public ResponseEntity<Void> deleteFuelType(@PathVariable Long id) {
        fuelTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/calorific-value")
    @Operation(summary = "발에률 정보 조회", description = "특정 에너지 타입의 발에률 정보를 조회합니다.")
    public ResponseEntity<CalorificValue> getCalorificValue(@PathVariable Long id) {
        return fuelTypeService.getCalorificValue(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/emission-factor")
    @Operation(summary = "배출계수 정보 조회", description = "특정 에너지 타입의 배출계수 정보를 조회합니다.")
    public ResponseEntity<EmissionFactor> getEmissionFactor(@PathVariable Long id) {
        return fuelTypeService.getEmissionFactor(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
