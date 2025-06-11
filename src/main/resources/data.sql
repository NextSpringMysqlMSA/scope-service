

-- 연료 타입 마스터 데이터
INSERT INTO fuel_type (category, name, unit, is_active, created_at, updated_at) VALUES
-- 석유계
('석유계', '경유', 'L', true, NOW(), NOW()),
('석유계', '휘발유', 'L', true, NOW(), NOW()),
('석유계', '등유', 'L', true, NOW(), NOW()),
('석유계', '중유', 'L', true, NOW(), NOW()),
('석유계', 'LPG', 'kg', true, NOW(), NOW()),

-- 석탄계  
('석탄계', '무연탄', 'kg', true, NOW(), NOW()),
('석탄계', '유연탄', 'kg', true, NOW(), NOW()),
('석탄계', '코크스', 'kg', true, NOW(), NOW()),

-- 가스계
('가스계', 'LNG', 'm³', true, NOW(), NOW()),
('가스계', '도시가스', 'm³', true, NOW(), NOW()),

-- 차량용
('차량용', '차량용경유', 'L', true, NOW(), NOW()),
('차량용', '차량용휘발유', 'L', true, NOW(), NOW());

-- 발열량 마스터 데이터 (2024년 기준)
INSERT INTO calorific_value (fuel_type_id, value, unit, year, is_active, created_at, updated_at) VALUES
-- 석유계 발열량 (TJ/kL)
(1, 38.20, 'TJ/kL', 2024, true, NOW(), NOW()),  -- 경유
(2, 34.30, 'TJ/kL', 2024, true, NOW(), NOW()),  -- 휘발유
(3, 37.70, 'TJ/kL', 2024, true, NOW(), NOW()),  -- 등유
(4, 40.90, 'TJ/kL', 2024, true, NOW(), NOW()),  -- 중유
(5, 50.80, 'TJ/kton', 2024, true, NOW(), NOW()), -- LPG (TJ/kton)

-- 석탄계 발열량 (TJ/kton)
(6, 25.80, 'TJ/kton', 2024, true, NOW(), NOW()), -- 무연탄
(7, 25.80, 'TJ/kton', 2024, true, NOW(), NOW()), -- 유연탄
(8, 28.20, 'TJ/kton', 2024, true, NOW(), NOW()), -- 코크스

-- 가스계 발열량 (TJ/천m³)
(9, 40.10, 'TJ/천m³', 2024, true, NOW(), NOW()),  -- LNG
(10, 40.10, 'TJ/천m³', 2024, true, NOW(), NOW()), -- 도시가스

-- 차량용 발열량 (TJ/kL)
(11, 38.20, 'TJ/kL', 2024, true, NOW(), NOW()), -- 차량용경유
(12, 34.30, 'TJ/kL', 2024, true, NOW(), NOW()); -- 차량용휘발유

-- 배출계수 마스터 데이터 (2024년 기준)
INSERT INTO emission_factor (fuel_type_id, co2_factor, ch4_factor, n2o_factor, year, is_active, created_at, updated_at) VALUES
-- 석유계 배출계수 (tCO2/TJ, kgCH4/TJ, kgN2O/TJ)
(1, 74.10, 10.00, 0.60, 2024, true, NOW(), NOW()),  -- 경유
(2, 69.30, 10.00, 0.60, 2024, true, NOW(), NOW()),  -- 휘발유
(3, 71.90, 10.00, 0.60, 2024, true, NOW(), NOW()),  -- 등유
(4, 77.40, 10.00, 0.60, 2024, true, NOW(), NOW()),  -- 중유
(5, 63.10, 1.00, 0.10, 2024, true, NOW(), NOW()),   -- LPG

-- 석탄계 배출계수
(6, 98.30, 10.00, 1.50, 2024, true, NOW(), NOW()),  -- 무연탄
(7, 95.30, 10.00, 1.50, 2024, true, NOW(), NOW()),  -- 유연탄
(8, 107.00, 10.00, 1.50, 2024, true, NOW(), NOW()), -- 코크스

-- 가스계 배출계수
(9, 56.10, 1.00, 0.10, 2024, true, NOW(), NOW()),   -- LNG
(10, 56.10, 1.00, 0.10, 2024, true, NOW(), NOW()),  -- 도시가스

-- 차량용 배출계수
(11, 74.10, 3.90, 3.90, 2024, true, NOW(), NOW()),  -- 차량용경유
(12, 69.30, 3.80, 5.70, 2024, true, NOW(), NOW());  -- 차량용휘발유

-- 테스트용 샘플 데이터 (선택사항)
-- 고정연소 샘플 데이터
INSERT INTO stationary_combustion 
(member_id, company_id, year, month, fuel_type_id, facility_name, facility_type, usage, co2_emission, ch4_emission, n2o_emission, total_emission, notes, created_at, updated_at) 
VALUES
(1, 1, 2024, 1, 1, '본사 보일러', '보일러', 1000.0000, 2.8596, 0.0096, 0.0054, 2.8746, '1월 본사 보일러 경유 사용', NOW(), NOW()),
(1, 1, 2024, 1, 9, '생산동 히터', '히터', 500.0000, 1.1242, 0.0005, 0.0001, 1.1248, '1월 생산동 LNG 히터 사용', NOW(), NOW());

-- 전력 사용 샘플 데이터  
INSERT INTO electricity_usage 
(member_id, company_id, year, month, facility_name, supplier, usage, emission_factor, total_emission, is_renewable, notes, created_at, updated_at)
VALUES
(1, 1, 2024, 1, '본사 사무동', '한국전력공사', 50000.0000, 0.4781, 23.9050, false, '1월 본사 전력 사용량', NOW(), NOW()),
(1, 1, 2024, 1, '생산동', '한국전력공사', 120000.0000, 0.4781, 57.3720, false, '1월 생산동 전력 사용량', NOW(), NOW());

-- 스팀 사용 샘플 데이터
INSERT INTO steam_usage 
(member_id, company_id, year, month, facility_name, supplier, usage, emission_factor, total_emission, notes, created_at, updated_at)
VALUES
(1, 1, 2024, 1, '생산공정', '지역난방공사', 100.0000, 0.0734, 7.3400, '1월 생산공정 스팀 사용량', NOW(), NOW());
