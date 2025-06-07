# ESG Scope Service

ESG 프로젝트의 Scope 1, 2 온실가스 배출량 계산 및 관리를 위한 마이크로서비스입니다.

## 개요

본 서비스는 다음과 같은 기능을 제공합니다:

### Scope 1 (직접 배출)

- **고정연소**: 사업장 내 보일러, 발전기 등의 연료 연소
- **이동연소**: 차량, 건설기계 등의 연료 연소

### Scope 2 (간접 배출)

- **전력 사용**: 구매 전력 사용에 따른 간접 배출
- **스팀 사용**: 구매 스팀 사용에 따른 간접 배출

## 기술 스택

- **Framework**: Spring Boot 3.5.0
- **Language**: Java 17
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA
- **Build Tool**: Gradle
- **Service Discovery**: Netflix Eureka
- **Documentation**: Swagger/OpenAPI

## 아키텍처

```
scope-service/
├── entity/           # 데이터베이스 엔티티
├── repository/       # 데이터 접근 계층
├── service/          # 비즈니스 로직
├── controller/       # REST API 엔드포인트
├── dto/             # 데이터 전송 객체
├── config/          # 설정 클래스
└── exception/       # 예외 처리
```

## 주요 엔티티

### 마스터 데이터

- `FuelType`: 연료 타입 (경유, 휘발유, LNG 등)
- `CalorificValue`: 연료별 발열량
- `EmissionFactor`: 연료별 온실가스 배출계수

### 활동 데이터

- `StationaryCombustion`: Scope 1 고정연소 데이터
- `MobileCombustion`: Scope 1 이동연소 데이터
- `ElectricityUsage`: Scope 2 전력 사용 데이터
- `SteamUsage`: Scope 2 스팀 사용 데이터

## 배출량 계산 공식

### Scope 1 연소 배출량

```
배출량 = 연료사용량 × 발열량 × 배출계수 × GWP
- CO2: 직접 배출
- CH4: 배출량 × 25 (GWP)
- N2O: 배출량 × 298 (GWP)
```

### Scope 2 전력 배출량

```
배출량 = 전력사용량(kWh) × 배출계수(tCO2/MWh) ÷ 1000
```

## API 엔드포인트

### Scope 1 고정연소

- `POST /api/v1/scope/stationary-combustion` - 데이터 등록
- `GET /api/v1/scope/stationary-combustion` - 목록 조회
- `GET /api/v1/scope/stationary-combustion/year/{year}` - 연도별 조회
- `PUT /api/v1/scope/stationary-combustion/{id}` - 수정
- `DELETE /api/v1/scope/stationary-combustion/{id}` - 삭제

### Scope 2 전력 사용

- `POST /api/v1/scope/electricity-usage` - 데이터 등록
- `GET /api/v1/scope/electricity-usage` - 목록 조회
- `GET /api/v1/scope/electricity-usage/year/{year}` - 연도별 조회
- `PUT /api/v1/scope/electricity-usage/{id}` - 수정
- `DELETE /api/v1/scope/electricity-usage/{id}` - 삭제

## 실행 방법

### 1. 데이터베이스 준비

```bash
# MySQL 데이터베이스 생성
CREATE DATABASE esg_scope CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 애플리케이션 실행

```bash
# 빌드 및 실행
./gradlew clean build
./gradlew bootRun
```

### 3. 확인

- Health Check: http://localhost:8080/actuator/health
- API 테스트: http://localhost:8080/api/v1/scope/stationary-combustion
