# 뚜두 조회 API `postponedAt` 추가 구현 플랜

## 1) 목표

- 아래 4개 조회 응답에 `postponedAt`(미루기 시각) 필드를 일관되게 추가한다.
  - `GET /api/ddudus` (뚜두 검색)
  - `GET /api/ddudus/daily/list` (일별 리스트)
  - `GET /api/ddudus/daily/timetable` (일별 시간표)
  - `GET /api/ddudus/{id}` (뚜두 상세)
- 기존 정렬/페이징/권한 로직은 유지하고, 응답 스키마만 확장한다.
- DB DDL 변경 없이(이미 `ddudus.postponed_at` 컬럼 존재) DTO/Projection/테스트를 정합성 있게 맞춘다.

## 2) 영향 범위 요약 (패키지/모듈)

- **bootstrap/planning-api**
  - 패키지: `com.ddudu.api.planning.ddudu.controller`, `com.ddudu.api.planning.ddudu.doc`
- **application/application-common**
  - 패키지: `com.ddudu.application.common.dto.ddudu`, `com.ddudu.application.common.dto.ddudu.response`
- **application/planning-application**
  - 패키지: `com.ddudu.application.planning.ddudu.service`, `com.ddudu.application.planning.ddudu.model`
- **infra/planning-infra-mysql**
  - 패키지: `com.ddudu.infra.mysql.planning.ddudu.repository`
- **테스트(application/planning-application)**
  - 패키지: `com.ddudu.application.planning.ddudu.service`

## 3) 신규/변경 영향 클래스 목록

### 3-1. 변경 대상 클래스

1. 검색 응답 DTO
   - `application/application-common/src/main/java/com/ddudu/application/common/dto/ddudu/SimpleDduduSearchDto.java`
   - 변경: `postponedAt` 필드 추가

2. 일별 리스트 응답 DTO
   - `application/application-common/src/main/java/com/ddudu/application/common/dto/ddudu/response/BasicDduduResponse.java`
   - 변경: `postponedAt` 필드 추가 + `from(Ddudu)` 매핑 반영

3. 일별 시간표 배치 DTO
   - `application/application-common/src/main/java/com/ddudu/application/common/dto/ddudu/DduduForTimetable.java`
   - 변경: `postponedAt` 필드 추가 + `of(Ddudu, color)` 매핑 반영

4. 상세 응답 DTO
   - `application/application-common/src/main/java/com/ddudu/application/common/dto/ddudu/response/DduduDetailResponse.java`
   - 변경: `postponedAt` 필드 추가 + `from(Ddudu)` 매핑 반영

5. 검색 Query Projection
   - `infra/planning-infra-mysql/src/main/java/com/ddudu/infra/mysql/planning/ddudu/repository/DduduQueryRepositoryImpl.java`
   - 변경: `projectSimpleDdudu()` 생성자 projection에 `dduduEntity.postponedAt` 추가

6. API 문서 인터페이스(필요 시)
   - `bootstrap/planning-api/src/main/java/com/ddudu/api/planning/ddudu/doc/DduduControllerDoc.java`
   - 변경: 응답 스키마 예시/설명 최신화(레코드 스키마 자동 반영 수준 점검 후 최소 변경)

7. 응답 라우팅 컨트롤러(변경 최소, 영향 확인)
   - `bootstrap/planning-api/src/main/java/com/ddudu/api/planning/ddudu/controller/DduduController.java`
   - 변경: 코드 로직 변경은 거의 없으나, API 계약 변경 영향으로 회귀 확인

8. 유즈케이스 서비스(코드 변경 최소, 영향 확인)
   - `application/planning-application/src/main/java/com/ddudu/application/planning/ddudu/service/DduduSearchService.java`
   - `application/planning-application/src/main/java/com/ddudu/application/planning/ddudu/service/GetDailyDdudusByGoalService.java`
   - `application/planning-application/src/main/java/com/ddudu/application/planning/ddudu/service/GetTimetableService.java`
   - `application/planning-application/src/main/java/com/ddudu/application/planning/ddudu/service/RetrieveDduduService.java`
   - 변경: DTO 스펙 확장으로 인한 반환 계약 검증

### 3-2. 신규 클래스 (예상)

- **없음(우선안)**
  - 본 이슈는 기존 도메인/엔티티에 `postponedAt`가 이미 존재하므로 DTO/Projection 중심 확장으로 처리한다.

## 4) API별 상세 구현 계획

### A. `GET /api/ddudus` (검색)

1. `SimpleDduduSearchDto`에 `LocalDateTime postponedAt` 추가
2. `DduduQueryRepositoryImpl.projectSimpleDdudu()`에서 projection 인자 순서를 DTO와 일치시켜 `postponedAt` 매핑
3. `DduduSearchService`는 기존 흐름 유지(결과 DTO만 확장)
4. 검색 응답 테스트에서 `postponedAt` 노출 여부 검증

### B. `GET /api/ddudus/daily/list` (일별 리스트)

1. `BasicDduduResponse`에 `postponedAt` 추가
2. `BasicDduduResponse.from(Ddudu)`에서 `ddudu.getPostponedAt()` 매핑
3. `GoalGroupedDdudus`/서비스 로직은 변경 없이 DTO 확장 반영
4. 일별 리스트 테스트에서 `postponedAt` 값 검증

### C. `GET /api/ddudus/daily/timetable` (일별 시간표)

1. `DduduForTimetable`에 `postponedAt` 추가
2. `DduduForTimetable.of(Ddudu, color)`에 매핑 추가
3. `TimetableResponse` 구조는 유지
4. 시간표 테스트에서 시간 배정 뚜두와 미배정 뚜두 각각 `postponedAt` 확인

### D. `GET /api/ddudus/{id}` (상세)

1. `DduduDetailResponse`에 `postponedAt` 추가
2. `DduduDetailResponse.from(Ddudu)` 매핑 추가
3. `RetrieveDduduService`는 기존 조회/권한 검증 로직 유지
4. 상세조회 테스트에서 `postponedAt` 노출 검증

## 5) 테스트 계획

> AGENTS 규칙 준수: application 모듈 테스트 중심, `@SpringBootTest`, `@Transactional`, `@BeforeEach`, `//given //when //then`, 실패 케이스 `ThrowingCallable`, 테스트 메서드명 한국어 유지.

### 5-1. 변경 대상 테스트 클래스

- `application/planning-application/src/test/java/com/ddudu/application/planning/ddudu/service/DduduSearchServiceTest.java`
- `application/planning-application/src/test/java/com/ddudu/application/planning/ddudu/service/GetDailyDdudusByGoalServiceTest.java`
- `application/planning-application/src/test/java/com/ddudu/application/planning/ddudu/service/GetDailyDdudusByTimeServiceTest.java`
- `application/planning-application/src/test/java/com/ddudu/application/planning/ddudu/service/RetrieveDduduServiceTest.java`

### 5-2. 검증 포인트

- 미루지 않은 뚜두: `postponedAt == null`
- 미룬 뚜두: `postponedAt != null` 및 기대 시점(또는 null 아님) 검증
- 기존 응답 필드 및 조회 조건(권한/날짜/스크롤) 회귀 없음

## 6) 구현 순서 (권장)

1. `application-common` DTO 4종 확장 (`SimpleDduduSearchDto`, `BasicDduduResponse`, `DduduForTimetable`, `DduduDetailResponse`)
2. `infra` 검색 projection(`DduduQueryRepositoryImpl`) 반영
3. `application` 서비스 회귀 확인 및 필요한 매핑 보정
4. `bootstrap` 문서/스키마 점검 (`DduduControllerDoc`)
5. 대상 테스트 4종 수정 및 실행

## 7) 리스크 및 체크포인트

- **Projection-Record 순서 불일치 리스크**
  - Querydsl `Projections.constructor`는 인자 순서가 중요하므로 DTO 필드 순서와 정확히 맞춰야 함.
- **JSON 직렬화 포맷 리스크**
  - `LocalDateTime postponedAt` 직렬화 형식이 기존 API 컨벤션과 일치하는지 스냅샷/문서로 확인.
- **기존 클라이언트 호환성**
  - 필드 추가는 하위 호환이지만, 필드명(`postponedAt`) 오탈자/nullable 계약을 명확히 유지.

