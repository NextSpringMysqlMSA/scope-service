-- ==============================================
-- ESG 연료 마스터 데이터 초기화 스크립트
-- ==============================================

-- 1. FuelType 테이블 데이터 INSERT (67개 연료)
INSERT INTO fuel_type (fuel_id, category, name, description, unit, is_active) VALUES
-- === 고정연소 - 액체연료 (석유계 17개) ===
('CRUDE_OIL', 'LIQUID_PETROLEUM', '원유', NULL, 'L', true),
('NAPHTHA', 'LIQUID_PETROLEUM', '나프타', NULL, 'L', true),
('GASOLINE', 'LIQUID_PETROLEUM', '휘발유', NULL, 'L', true),
('AVIATION_GASOLINE', 'LIQUID_PETROLEUM', '항공휘발유', NULL, 'L', true),
('JET_FUEL_KEROSENE', 'LIQUID_PETROLEUM', '제트유(등유형)', NULL, 'L', true),
('JET_FUEL_GASOLINE', 'LIQUID_PETROLEUM', '제트유(휘발유형)', NULL, 'L', true),
('KEROSENE', 'LIQUID_PETROLEUM', '등유', NULL, 'L', true),
('DIESEL', 'LIQUID_PETROLEUM', '경유', NULL, 'L', true),
('HEAVY_OIL_A', 'LIQUID_PETROLEUM', '중유(A급)', NULL, 'L', true),
('HEAVY_OIL_B', 'LIQUID_PETROLEUM', '중유(B급)', NULL, 'L', true),
('HEAVY_OIL_C', 'LIQUID_PETROLEUM', '중유(C급)', NULL, 'L', true),
('BUNKER_A_OIL', 'LIQUID_PETROLEUM', '벙커A유', NULL, 'L', true),
('BUNKER_B_OIL', 'LIQUID_PETROLEUM', '벙커B유', NULL, 'L', true),
('BUNKER_C_OIL', 'LIQUID_PETROLEUM', '벙커C유', NULL, 'L', true),
('LUBRICANTS', 'LIQUID_PETROLEUM', '윤활유', NULL, 'L', true),
('BITUMEN_ASPHALT', 'LIQUID_PETROLEUM', '역청/아스팔트', NULL, 'kg', true),
('PETROLEUM_COKE', 'LIQUID_PETROLEUM', '석유코크스', NULL, 'kg', true),

-- === 고정연소 - 고체연료 (석탄계 7개) ===
('ANTHRACITE', 'SOLID_COAL', '무연탄', NULL, 'kg', true),
('BITUMINOUS_COAL', 'SOLID_COAL', '유연탄', NULL, 'kg', true),
('SUB_BITUMINOUS_COAL', 'SOLID_COAL', '아역청탄', NULL, 'kg', true),
('LIGNITE', 'SOLID_COAL', '갈탄', NULL, 'kg', true),
('COKING_COAL', 'SOLID_COAL', '코크스용탄', NULL, 'kg', true),
('COKE_OVEN_COKE', 'SOLID_COAL', '코크스오븐코크스', NULL, 'kg', true),
('CHARCOAL', 'SOLID_COAL', '목탄', NULL, 'kg', true),

-- === 고정연소 - 가스연료 (5개) ===
('NATURAL_GAS', 'GAS_FUEL', '천연가스', NULL, 'Nm³', true),
('LIQUEFIED_NATURAL_GAS', 'GAS_FUEL', '액화천연가스(LNG)', NULL, 'kg', true),
('LIQUEFIED_PETROLEUM_GAS', 'GAS_FUEL', '액화석유가스(LPG)', NULL, 'kg', true),
('PROPANE', 'GAS_FUEL', '프로판', NULL, 'kg', true),
('BUTANE', 'GAS_FUEL', '부탄', NULL, 'kg', true),

-- === 이동연소 - 차량전용연료 (3개) ===
('MOTOR_GASOLINE', 'VEHICLE_FUEL', '자동차용 휘발유', NULL, 'L', true),
('AUTOMOTIVE_DIESEL', 'VEHICLE_FUEL', '자동차용 경유', NULL, 'L', true),
('LIQUEFIED_PETROLEUM_GAS_VEHICLE', 'VEHICLE_FUEL', '자동차용 LPG', NULL, 'L', true),

-- === 이동연소 - 항공용연료 (3개) ===
('AVIATION_GASOLINE_MOBILE', 'AVIATION_FUEL', '항공휘발유', NULL, 'L', true),
('JET_FUEL_KEROSENE_MOBILE', 'AVIATION_FUEL', '제트유(등유형)', NULL, 'L', true),
('JET_FUEL_GASOLINE_MOBILE', 'AVIATION_FUEL', '제트유(휘발유형)', NULL, 'L', true),

-- === 이동연소 - 바이오연료 (2개) ===
('BIODIESEL', 'BIO_FUEL', '바이오디젤', NULL, 'L', true),
('BIOETHANOL', 'BIO_FUEL', '바이오에탄올', NULL, 'L', true),

-- === 전력 (1개) ===
('ELECTRICITY_KWH', 'ELECTRICITY', '전력', NULL, 'kWh', true),

-- === 스팀 (3개) ===
('STEAM_TYPE_A', 'STEAM', '스팀 A타입', '일반 스팀', 'GJ', true),
('STEAM_TYPE_B', 'STEAM', '스팀 B타입', '고압 스팀', 'GJ', true),
('STEAM_TYPE_C', 'STEAM', '스팀 C타입', '초고압 스팀', 'GJ', true);

-- 2. EmissionFactor 테이블 데이터 INSERT (67개 연료별 배출계수)
INSERT INTO emission_factor (fuel_type_id, co2factor, ch4factor, n2o_factor, year, is_active) VALUES
-- === 고정연소 - 액체연료 (석유계) ===
((SELECT id FROM fuel_type WHERE fuel_id = 'CRUDE_OIL'), 2.13, 0.0033, 0.0006, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'NAPHTHA'), 1.93, 0.0033, 0.0006, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'GASOLINE'), 2.08, 0.0033, 0.0006, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'AVIATION_GASOLINE'), 2.08, 0.0033, 0.0006, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'JET_FUEL_KEROSENE'), 2.16, 0.0033, 0.0006, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'JET_FUEL_GASOLINE'), 2.08, 0.0033, 0.0006, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'KEROSENE'), 2.16, 0.0033, 0.0006, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'DIESEL'), 2.58, 0.0033, 0.0006, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'HEAVY_OIL_A'), 2.68, 0.0033, 0.0006, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'HEAVY_OIL_B'), 2.75, 0.0033, 0.0006, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'HEAVY_OIL_C'), 2.86, 0.0033, 0.0006, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'BUNKER_A_OIL'), 2.68, 0.0033, 0.0006, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'BUNKER_B_OIL'), 2.75, 0.0033, 0.0006, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'BUNKER_C_OIL'), 2.86, 0.0033, 0.0006, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'LUBRICANTS'), 2.68, 0.0033, 0.0006, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'BITUMEN_ASPHALT'), 3.2, 0.0033, 0.0006, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'PETROLEUM_COKE'), 3.5, 0.0033, 0.0006, 2024, true),

-- === 고정연소 - 고체연료 (석탄계) ===
((SELECT id FROM fuel_type WHERE fuel_id = 'ANTHRACITE'), 2.36, 0.01, 0.0015, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'BITUMINOUS_COAL'), 2.42, 0.01, 0.0015, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'SUB_BITUMINOUS_COAL'), 1.9, 0.01, 0.0015, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'LIGNITE'), 1.17, 0.01, 0.0015, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'COKING_COAL'), 2.42, 0.01, 0.0015, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'COKE_OVEN_COKE'), 3.21, 0.01, 0.0015, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'CHARCOAL'), 3.17, 0.01, 0.0015, 2024, true),

-- === 고정연소 - 가스연료 ===
((SELECT id FROM fuel_type WHERE fuel_id = 'NATURAL_GAS'), 2.176, 0.001, 0.0001, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'LIQUEFIED_NATURAL_GAS'), 2.75, 0.001, 0.0001, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'LIQUEFIED_PETROLEUM_GAS'), 3.0, 0.001, 0.0001, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'PROPANE'), 2.96, 0.001, 0.0001, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'BUTANE'), 3.03, 0.001, 0.0001, 2024, true),

-- === 이동연소 - 차량전용연료 ===
((SELECT id FROM fuel_type WHERE fuel_id = 'MOTOR_GASOLINE'), 2.08, 0.0033, 0.0006, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'AUTOMOTIVE_DIESEL'), 2.58, 0.0033, 0.0006, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'LIQUEFIED_PETROLEUM_GAS_VEHICLE'), 1.51, 0.001, 0.0001, 2024, true),

-- === 이동연소 - 항공용연료 ===
((SELECT id FROM fuel_type WHERE fuel_id = 'AVIATION_GASOLINE_MOBILE'), 2.08, 0.0033, 0.0006, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'JET_FUEL_KEROSENE_MOBILE'), 2.16, 0.0033, 0.0006, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'JET_FUEL_GASOLINE_MOBILE'), 2.08, 0.0033, 0.0006, 2024, true),

-- === 이동연소 - 바이오연료 (CO2 배출계수 0) ===
((SELECT id FROM fuel_type WHERE fuel_id = 'BIODIESEL'), 0, 0.0033, 0.0006, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'BIOETHANOL'), 0, 0.0033, 0.0006, 2024, true),

-- === 전력 ===
((SELECT id FROM fuel_type WHERE fuel_id = 'ELECTRICITY_KWH'), 0.0004653, 0, 0, 2024, true),

-- === 스팀 (ScopeModal.tsx에서 하드코딩된 값들) ===
((SELECT id FROM fuel_type WHERE fuel_id = 'STEAM_TYPE_A'), 56.452, 0, 0, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'STEAM_TYPE_B'), 60.974, 0, 0, 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'STEAM_TYPE_C'), 59.685, 0, 0, 2024, true);

-- 3. CalorificValue 테이블 데이터 INSERT (필요한 경우)
-- 현재 프론트엔드에서는 배출계수만 사용하므로 발열량은 기본값으로 설정
INSERT INTO calorific_value (fuel_type_id, value, unit, year, is_active) VALUES
-- === 액체연료 발열량 (TJ/kL) ===
((SELECT id FROM fuel_type WHERE fuel_id = 'CRUDE_OIL'), 38.1, 'TJ/kL', 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'NAPHTHA'), 31.1, 'TJ/kL', 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'GASOLINE'), 31.0, 'TJ/kL', 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'KEROSENE'), 36.8, 'TJ/kL', 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'DIESEL'), 38.2, 'TJ/kL', 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'HEAVY_OIL_A'), 39.1, 'TJ/kL', 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'HEAVY_OIL_B'), 40.2, 'TJ/kL', 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'HEAVY_OIL_C'), 41.7, 'TJ/kL', 2024, true),

-- === 고체연료 발열량 (TJ/ton) ===
((SELECT id FROM fuel_type WHERE fuel_id = 'ANTHRACITE'), 26.7, 'TJ/ton', 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'BITUMINOUS_COAL'), 25.8, 'TJ/ton', 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'SUB_BITUMINOUS_COAL'), 18.9, 'TJ/ton', 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'LIGNITE'), 11.9, 'TJ/ton', 2024, true),

-- === 가스연료 발열량 ===
((SELECT id FROM fuel_type WHERE fuel_id = 'NATURAL_GAS'), 40.2, 'TJ/10^6m³', 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'LIQUEFIED_NATURAL_GAS'), 50.4, 'TJ/ton', 2024, true),
((SELECT id FROM fuel_type WHERE fuel_id = 'LIQUEFIED_PETROLEUM_GAS'), 50.2, 'TJ/ton', 2024, true),

-- === 전력 (kWh → TJ 변환계수) ===
((SELECT id FROM fuel_type WHERE fuel_id = 'ELECTRICITY_KWH'), 0.0036, 'TJ/MWh', 2024, true);
