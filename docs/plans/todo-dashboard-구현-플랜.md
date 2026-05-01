# PR 후속 작업 플랜: 투두 목록 대시보드 조회 API

## 1) 목표 및 수용 기준
- `GET /api/todos/dashboard` API가 추가되어야 한다.
- 응답은 `isEmpty`, `contents`, `todayIndex`를 포함해야 한다.
- `contents`는 날짜별 투두 그룹 목록이며, **오늘 날짜 그룹은 투두가 없어도 반드시 포함**되어야 한다.
- 오늘이 아닌 날짜는 조회된 투두가 없으면 응답에서 생략되어야 한다.
- 로그인 사용자가 없으면 404 에러가 발생해야 한다.
- 각 날짜 그룹 내 `todos` 정렬 기준은 아래 순서를 따라야 한다.
  1. `status DESC`
  2. `beginAt ASC (null comes last)`
  3. `endAt ASC (null comes last)`
  4. `id ASC`

## 2) 구현 단계
1. **API 엔드포인트/문서 추가 (bootstrap/planning-api)**
   - `TodoController`에 `@GetMapping("/dashboard")` 엔드포인트를 추가한다.
   - `TodoControllerDoc`에 대시보드 조회 문서 메서드, 응답 스키마 및 예외 예시를 반영한다.

2. **UseCase/DTO 정의 (application/application-common)**
   - 인바운드 포트 `GetTodoDashboardUseCase`를 추가한다.
   - 응답 DTO(`TodoDashboardResponse`, `TodoDashboardContent`, `TodoDashboardItem`)를 추가한다.
   - 아웃바운드 포트(`TodoLoaderPort`)에 대시보드 조회용 메서드를 추가한다.

3. **유즈케이스 서비스 구현 (application/planning-application)**
   - `GetTodoDashboardService`를 추가하고 `GetTodoDashboardUseCase`를 구현한다.
   - 처리 순서
     - 로그인 사용자 조회/검증
     - 투두 목록 조회
     - 날짜별 그룹핑
     - 오늘 날짜 그룹 보정(빈 리스트 포함)
     - `isEmpty`, `todayIndex` 계산
   - 그룹 내 `todos` 정렬은 명시된 기준으로 최종 보장한다.

4. **인프라 조회 구현 (infra/planning-infra-mysql)**
   - `TodoPersistenceAdapter`에서 `TodoLoaderPort` 신규 메서드를 구현한다.
   - `TodoQueryRepository`(및 Impl) 또는 `TodoRepository` 쿼리에 정렬 기준을 반영한다.
   - DB/JPQL 제약으로 `NULLS LAST`를 직접 쓰기 어려우면 `CASE WHEN ... IS NULL` 방식으로 동일 정렬을 보장한다.

5. **예외/문서 정합성 확인 (bootstrap/bootstrap-common + planning-api)**
   - 로그인 사용자 없음 케이스가 기존 `TodoErrorCode`/에러 예시와 정합한지 점검한다.
   - Swagger 스키마와 실제 DTO 필드명이 일치하는지 점검한다.

6. **테스트 작성 및 회귀 검증**
   - `application/planning-application`에 서비스 테스트를 추가한다.
   - 성공 케이스
     - 일반 조회 성공
     - 조회 대상 없음(`isEmpty=true`, 오늘 그룹 포함)
     - 오늘 계획된 투두만 없음(오늘 그룹 empty)
   - 실패 케이스
     - 로그인 사용자 없음(ThrowingCallable 사용)
   - 정렬 케이스
     - `status DESC`
     - `beginAt` null last
     - `endAt` null last
     - tie-breaker `id ASC`

## 3) 신규/변경 패키지 및 클래스

### 3-1. 신규(예상)
- **Application Common**
  - `application/application-common/src/main/java/com/modoo/application/common/port/todo/in/GetTodoDashboardUseCase.java`
  - `application/application-common/src/main/java/com/modoo/application/common/dto/todo/response/TodoDashboardResponse.java`
  - `application/application-common/src/main/java/com/modoo/application/common/dto/todo/response/TodoDashboardContent.java`
  - `application/application-common/src/main/java/com/modoo/application/common/dto/todo/response/TodoDashboardItem.java`
- **Planning Application**
  - `application/planning-application/src/main/java/com/modoo/application/planning/todo/service/GetTodoDashboardService.java`
- **Test**
  - `application/planning-application/src/test/java/com/modoo/application/planning/todo/service/GetTodoDashboardServiceTest.java`

### 3-2. 변경(핵심)
- **Bootstrap (API)**
  - `bootstrap/planning-api/src/main/java/com/modoo/api/planning/todo/controller/TodoController.java`
  - `bootstrap/planning-api/src/main/java/com/modoo/api/planning/todo/doc/TodoControllerDoc.java`
- **Application Common (Port)**
  - `application/application-common/src/main/java/com/modoo/application/common/port/todo/out/TodoLoaderPort.java`
- **Infra (MySQL)**
  - `infra/planning-infra-mysql/src/main/java/com/modoo/infra/mysql/planning/todo/adapter/TodoPersistenceAdapter.java`
  - `infra/planning-infra-mysql/src/main/java/com/modoo/infra/mysql/planning/todo/repository/TodoRepository.java`
  - `infra/planning-infra-mysql/src/main/java/com/modoo/infra/mysql/planning/todo/repository/TodoQueryRepository.java`
  - `infra/planning-infra-mysql/src/main/java/com/modoo/infra/mysql/planning/todo/repository/TodoQueryRepositoryImpl.java`

## 4) 테스트/검증 순서
1. DDL 변경이 없는 경우 바로 테스트 진행
2. 애플리케이션 테스트 실행
   - `./gradlew :application:planning-application:test --tests "*GetTodoDashboardServiceTest"`
3. API 모듈 컴파일 확인
   - `./gradlew :bootstrap:planning-api:compileJava`
4. 필요 시 회귀 테스트
   - `./gradlew test`

## 5) 리스크 및 체크포인트
- `status`의 정렬 우선순위가 enum ordinal/string 중 어떤 기준인지 쿼리/도메인 레벨에서 일관되게 해석해야 한다.
- `todayIndex`는 오늘 그룹 강제 삽입 이후 기준으로 계산되어야 한다.
- `null` 정렬 처리(`beginAt`, `endAt`)가 DB/ORM에 따라 달라질 수 있으므로 테스트로 고정해야 한다.
