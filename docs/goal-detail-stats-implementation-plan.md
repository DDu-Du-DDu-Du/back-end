# 특정 목표 상세 통계 요약 API 구현 플랜

## 1) 목표

- 엔드포인트 `GET /api/stats/detail/{goalId}?userId={optional}` 를 추가한다.
- 응답 필드: `id`, `name`, `createdAt`, `totalCount`, `completedCount`, `completeRate`.
- 조회 대상 사용자 결정 규칙: `userId` 쿼리 파라미터가 있으면 해당 사용자, 없으면 로그인 사용자.
- 예외 규칙:
  - 사용자 미존재: 404
  - 목표 미존재: 404

## 2) 계층별 구현 범위 및 클래스 배치

### 2-1. Bootstrap (API)

#### 영향 클래스

- `bootstrap/stats-api/src/main/java/com/ddudu/api/stats/controller/StatsController.java`
  - 신규 GET 메서드 추가 (`/detail/{goalId}`)
- `bootstrap/stats-api/src/main/java/com/ddudu/api/stats/doc/StatsControllerDoc.java`
  - Swagger 문서 메서드/응답/에러 예시 추가
- `bootstrap/bootstrap-common/src/main/java/com/ddudu/bootstrap/common/doc/examples/StatsErrorExamples.java`
  - 필요 시 에러 예시 상수 보강 (재사용 가능하면 추가 없음)

#### 신규 클래스

- `bootstrap/stats-api/src/main/java/com/ddudu/api/stats/controller/response/GoalDetailStatsResponse.java`
  - API 응답 모델 (혹은 application 응답 DTO를 그대로 반환하는 기존 컨벤션 유지 시 생성 생략 가능)

### 2-2. Application (UseCase)

#### 영향 클래스

- `application/stats-application/src/main/java/com/ddudu/application/stats/service/`
  - 신규 서비스 구현 클래스 추가

#### 신규 클래스

- `application/application-common/src/main/java/com/ddudu/application/common/port/stats/in/CollectGoalDetailStatsUseCase.java`
  - 유즈케이스 입력 포트
- `application/stats-application/src/main/java/com/ddudu/application/stats/service/CollectGoalDetailStatsService.java`
  - 유즈케이스 구현 (`@UseCase`, `@Transactional(readOnly = true)`)
- `application/application-common/src/main/java/com/ddudu/application/common/dto/stats/response/GoalDetailStatsSummaryResponse.java`
  - 유즈케이스 응답 DTO
- `application/application-common/src/main/java/com/ddudu/application/common/port/stats/out/GoalDetailStatsPort.java`
  - 목표 단건 통계 조회 출력 포트
- `application/application-common/src/main/java/com/ddudu/application/common/dto/stats/GoalStatusSummaryRaw.java`
  - 인프라 조회 결과를 받는 raw DTO (`dduduId`, `status`)

### 2-3. Infra (Query)

#### 영향 클래스

- `infra/planning-infra-mysql/src/main/java/com/ddudu/infra/mysql/planning/ddudu/adapter/DduduPersistenceAdapter.java`
  - 신규 출력 포트 구현 추가 (`implements GoalDetailStatsPort`)
- `infra/planning-infra-mysql/src/main/java/com/ddudu/infra/mysql/planning/ddudu/repository/DduduQueryRepository.java`
  - goal 단건 통계용 조회 메서드 시그니처 추가
- `infra/planning-infra-mysql/src/main/java/com/ddudu/infra/mysql/planning/ddudu/repository/DduduQueryRepositoryImpl.java`
  - Querydsl 조회 구현 (`dduduId`, `status` projection)

#### 신규 클래스

- 없음 (기존 repository/adapter 확장 우선)

## 3) 집계 전략 검토 및 제안

### 3-1. 대안 비교

1. **DB COUNT 집계 방식**
   - 장점: 전송량이 최소화되고 DB 한 번에 계산 가능
   - 단점: 집계 조건이 늘어날수록 Querydsl 식이 복잡해지고, API 요구사항이 바뀌면 SQL 수정 범위가 커짐

2. **상태 로우 조회 후 Application Stream 집계 방식 (`dduduId + status`)**
   - 장점: 도메인/유즈케이스 레벨에서 규칙 변경이 쉬움, 테스트에서 계산 로직 검증이 직관적
   - 단점: 목표 내 뚜두 건수가 매우 큰 경우 전송량/메모리 사용량 증가 가능

3. **`BaseStats` 조회 후 Application 집계 방식**
   - 장점: 기존 `MonthlyStatsPort.collectMonthlyStats()`에서 사용하는 투영 모델과 사고방식을 재사용할 수 있어, 향후 상세 통계(요일/시간대/미루기) 확장 시 모델 일관성이 높음
   - 단점: 이번 API에 불필요한 필드(`scheduledOn`, `beginAt`, `endAt`, `isPostponed`, `goalName` 등)까지 함께 로딩될 가능성이 커서 단순 요약 API에는 과한 projection이 될 수 있음
   - 단점: `BaseStats`는 월간/상세 통계를 위한 성격이 강해, 목표 단건 요약 API의 최소 데이터 원칙과는 다소 거리가 있음

### 3-2. 최종 제안 (이번 요구사항 기준)

- 이번 API는 반환 필드가 `totalCount/completedCount/completeRate`로 단순하므로, **`dduduId + status` 최소 projection 조회 + application stream 집계**를 최종안으로 채택한다.
- `BaseStats` 옵션은 향후 이 API가 요일/시간대/미루기 지표까지 포함하도록 확장될 때 재검토한다.
- 과도한 데이터 로딩 위험을 줄이기 위해 아래 가이드를 함께 적용한다.
  - projection 최소화: `dduduId`, `status` 외 필드는 조회하지 않음
  - 필터 최소화: `goalId`, `userId`(목표 소유자) 기준만 적용
  - 향후 목표별 뚜두 건수가 큰 환경이 확인되면, 같은 포트 인터페이스를 유지한 채 DB COUNT 집계 구현으로 교체 가능하도록 설계

### 3-3. Stream 집계 예시 흐름

- `List<GoalStatusSummaryRaw> rows = goalDetailStatsPort.loadGoalStatuses(goalId, targetUserId)`
- `long totalCount = rows.size();`
- `long completedCount = rows.stream().filter(row -> row.status().isComplete()).count();`
- `int completeRate = totalCount == 0 ? 0 : (int) Math.round(completedCount * 100.0 / totalCount);`

## 4) 상세 로직 설계

1. `StatsController` 신규 API에서 `loginId`, `goalId`, `userId(optional)`를 받는다.
2. `CollectGoalDetailStatsService`에서 조회 대상 사용자 ID를 결정한다.
   - `targetUserId = (userId != null) ? userId : loginId`
3. `UserLoaderPort.getUserOrElseThrow(targetUserId, StatsErrorCode.USER_NOT_EXISTING...)`로 사용자 존재 검증.
4. `GoalLoaderPort.getGoalOrElseThrow(goalId, StatsErrorCode.GOAL_NOT_EXISTING...)`로 목표 존재 검증.
5. `GoalDetailStatsPort`로 `goalId + targetUserId` 조건의 `dduduId/status` 목록 조회.
6. Application stream으로 `totalCount`, `completedCount` 계산.
7. `completeRate` 계산:
   - `totalCount == 0` 이면 `0`
   - 아니면 `Math.round(completedCount * 100.0 / totalCount)`
8. `goal.id/name/createdAt` + 집계값으로 응답 DTO를 조립하여 반환.

## 5) 테스트 계획

### 5-1. Application 테스트

- 대상 파일(신규):
  - `application/stats-application/src/test/java/com/ddudu/application/stats/service/CollectGoalDetailStatsServiceTest.java`
- 케이스:
  - 성공: 목표 상세 통계 요약 성공
  - 성공: 뚜두 0건일 때 total/completed/rate 모두 0 처리
  - 성공: 완료/미완료 혼합 상태에서 stream 집계가 정확함
  - 실패: 사용자 없음 (404 코드명 검증)
  - 실패: 목표 없음 (404 코드명 검증)
- 규칙 반영:
  - `@SpringBootTest`, `@Transactional`, `@BeforeEach`
  - `//given //when //then` 명시
  - 실패 케이스의 `//when`은 `ThrowingCallable` 사용
  - 테스트 메서드명 한국어

### 5-2. Infra 조회 검증

- 통합 테스트 또는 repository 테스트로 projection 조회 정확성 확인
  - `dduduId`, `status`만 조회되는지 확인
  - 목표/사용자 조건 필터 정확성 확인
  - 데이터 없음 시 빈 리스트 반환 확인

## 6) 문서/스키마/호환성 점검

- OpenAPI 문서에 신규 엔드포인트, 파라미터, 성공/실패 응답 예시 반영.
- DB DDL 변경은 불필요(조회 쿼리만 추가).
- 기존 `/api/stats/detail/{goalId}/achieved`, `/postponed`와 URL 충돌 없음 확인.

## 7) 구현 순서 (권장)

1. `application-common`에 in/out port + raw DTO 추가
2. `stats-application` 서비스 구현 및 테스트 작성 (stream 집계 로직 포함)
3. `planning-infra-mysql` 조회 쿼리/어댑터 확장
4. `stats-api` 컨트롤러 + 문서 반영
5. 전체 테스트 및 API 스모크 확인

