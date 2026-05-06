# 투두 생성/수정 목표 Optional 구현 플랜

## 배경

- 투두 생성/수정 요청에서 `goalId`가 없어도 투두를 생성하거나 수정할 수 있어야 한다.
- 목표가 전달된 경우에는 기존과 동일하게 목표 존재 여부, 목표 소유자 여부, 종료 목표 여부를 검증한다.
- 목표가 전달되지 않은 경우에는 목표 조회 및 목표 기반 검증을 생략한다.
- 현재 통계 및 타임테이블은 목표 전용 서비스로 유지하므로, `goalId`가 `null`인 투두는 별도 그룹/응답으로 포함하지 않고 기존 목표 기반 조회 흐름에서 자연스럽게 생략한다.

## 구현 원칙

1. `goalId` Optional 처리는 생성/수정 입력, 도메인 생성/수정, 영속화 저장/수정, DB 스키마까지 일관되게 반영한다.
2. 목표가 있는 요청의 기존 검증 강도는 낮추지 않는다.
3. 목표가 없는 투두는 생성/수정 API의 정상 케이스로 허용한다.
4. 통계 및 타임테이블은 목표 전용 서비스이므로 `goalId == null` 투두를 포함하기 위한 별도 조회/응답 처리는 하지 않는다.
5. 도메인 수정 로직과 영속화 수정 로직 모두 `goalId` 변경을 반영하도록 확인한다.

## 상세 구현 단계

### 1. 요청 DTO의 `goalId` 필수 검증 제거

- `CreateTodoRequest.goalId`에서 `@NotNull(message = "2001 NULL_GOAL_VALUE")`를 제거한다.
- `UpdateTodoRequest.goalId`에서 `@NotNull(message = "2001 NULL_GOAL_VALUE")`를 제거한다.
- `@Positive(message = "2014 NEGATIVE_OR_ZERO_GOAL_ID")`는 유지해, 값이 전달된 경우 양수만 허용한다.
- `toCommand()`는 기존처럼 `goalId`를 command로 전달하되, `null` 전달을 정상 케이스로 허용한다.

#### 영향 패키지 및 클래스

| 유형 | 패키지 | 클래스 |
| --- | --- | --- |
| 변경 | `com.modoo.application.common.dto.todo.request` | `CreateTodoRequest` |
| 변경 | `com.modoo.application.common.dto.todo.request` | `UpdateTodoRequest` |

### 2. 애플리케이션 서비스의 목표 조회/검증 조건부 처리

- `CreateTodoService#create()`에서 `request.goalId()`가 `null`이면 목표 조회를 수행하지 않는다.
- `CreateTodoService#create()`에서 목표가 있는 경우에만 목표 소유자 검증과 종료 목표 검증을 수행한다.
- `UpdateTodoService#update()`에서 `request.goalId()`가 `null`이면 목표 조회와 목표 소유자 검증을 수행하지 않는다.
- `UpdateTodoService#update()`에서 기존 투두 작성자 검증은 목표 여부와 무관하게 유지한다.
- 목표가 있는 수정 요청은 기존처럼 목표 존재 여부와 목표 소유자 검증을 수행한다.

#### 영향 패키지 및 클래스

| 유형 | 패키지 | 클래스 |
| --- | --- | --- |
| 변경 | `com.modoo.application.planning.todo.service` | `CreateTodoService` |
| 변경 | `com.modoo.application.planning.todo.service` | `UpdateTodoService` |

### 3. Todo 도메인 생성/수정 로직의 `goalId` Optional 반영

- `Todo.validate(...)`에서 `goalId` non-null 검증을 제거한다.
- `userId`, `name`, `memo`, 기간 검증은 기존과 동일하게 유지한다.
- `Todo.update(...)`는 수정 요청의 `goalId`를 새 도메인 상태에 반영해야 한다.
- 현재처럼 `Todo.update(...)`가 builder에 `.goalId(goalId)`를 세팅하는 구조를 유지하거나, 누락되어 있다면 반드시 추가한다.
- 테스트에서는 `goalId == null` 생성뿐 아니라, 기존 목표가 있던 투두를 `goalId == null`로 수정하는 도메인 시나리오를 검증한다.

#### 영향 패키지 및 클래스

| 유형 | 패키지 | 클래스 |
| --- | --- | --- |
| 변경 | `com.modoo.domain.planning.todo.aggregate` | `Todo` |
| 확인 | `com.modoo.domain.planning.todo.dto` | `CreateTodoCommand` |
| 확인 | `com.modoo.domain.planning.todo.dto` | `UpdateTodoCommand` |
| 확인 | `com.modoo.domain.planning.todo.service` | `TodoDomainService` |

### 4. 영속화 엔티티와 DB 스키마의 `goal_id` nullable 반영

- `TodoEntity.goalId`의 `@Column(nullable = false)` 설정을 제거하거나 nullable 허용으로 변경한다.
- `TodoEntity.from(Todo todo)`는 기존처럼 `todo.getGoalId()`를 저장하되, `null` 값을 허용한다.
- `TodoEntity.toDomain()`은 기존처럼 `goalId`를 도메인으로 복원하되, `null` 값을 허용한다.
- `TodoEntity.update(Todo todo)`에 `this.goalId = todo.getGoalId();`를 반영한다.
  - 이 작업이 없으면 도메인 `Todo.update(...)`가 `goalId` 변경을 반영하더라도 DB 수정에는 반영되지 않는다.
- 신규 Flyway migration을 추가해 `todos.goal_id` 컬럼을 `NULL` 허용으로 변경한다.
- nullable FK는 유지해, `goal_id`가 있는 경우에는 기존처럼 유효한 목표만 참조하도록 한다.

#### 영향 패키지 및 클래스

| 유형 | 패키지/경로 | 클래스/파일 |
| --- | --- | --- |
| 변경 | `com.modoo.infra.mysql.planning.todo.entity` | `TodoEntity` |
| 신규 | `bootstrap/bootstrap-gateway/src/main/resources/db/migration` | `V{next}__allow_null_todo_goal_id.sql` |

### 5. 통계 및 타임테이블의 `goalId == null` 처리 정책

- 현재 통계 및 타임테이블은 목표 전용 서비스로 유지한다.
- `goalId == null`인 투두를 통계/타임테이블 응답에 포함하기 위한 별도 그룹, 기본 목표, 기본 색상, left join 보완은 구현하지 않는다.
- 기존 목표 기반 inner join 또는 목표 기반 조건으로 인해 `goalId == null` 투두가 조회 결과에서 제외되는 것은 의도한 동작으로 본다.
- 단, 생성/수정 API의 저장 결과 조회처럼 목표 전용이 아닌 단건 응답에서는 `goalId == null`을 그대로 허용한다.
- 향후 목표 없는 투두를 통계/타임테이블에 포함해야 하는 요구사항이 생기면 별도 이슈에서 응답 스펙과 노출 정책을 먼저 정의한다.

#### 영향 패키지 및 클래스

| 유형 | 패키지 | 클래스 |
| --- | --- | --- |
| 변경 없음 | `com.modoo.infra.mysql.planning.todo.repository` | `TodoQueryRepositoryImpl`의 목표 전용 통계/타임테이블 조회 |
| 변경 없음 | `com.modoo.application.planning.todo.model` | `Timetable` |
| 변경 없음 | `com.modoo.application.planning.todo.model` | `TodoList`의 목표 그룹 응답 흐름 |

### 6. 테스트 계획

#### 도메인 테스트

- `goalId`가 `null`이어도 투두를 생성할 수 있는지 검증한다.
- 기존 목표가 있는 투두를 `goalId == null`로 수정할 수 있는지 검증한다.
- `goalId == null`이어도 이름, 메모, 기간, 사용자 검증은 기존처럼 동작하는지 확인한다.
- 실패 케이스는 `ThrowingCallable`을 사용하고 `// given`, `// when`, `// then` 주석을 명시한다.
- 테스트 메서드명은 한국어로 작성한다.

#### 애플리케이션 테스트

- 투두 생성 시 `goalId == null`이면 목표 조회 없이 저장되는지 검증한다.
- 투두 수정 시 `goalId == null`이면 목표 조회 없이 수정되는지 검증한다.
- 투두 수정 시 `goalId == null`로 변경된 값이 영속화 계층까지 반영되는지 검증한다.
- 목표가 있는 생성/수정 요청은 기존 목표 검증이 유지되는지 검증한다.
- 애플리케이션 서비스 테스트는 `@SpringBootTest`, `@Transactional`, `@BeforeEach` 기반 데이터 준비 규칙을 따른다.

#### 마이그레이션 및 회귀 테스트

- 신규 DDL이 추가되므로 `./gradlew :bootstrap:bootstrap-gateway:flywayMigrate`를 먼저 실행한다.
- 이후 관련 도메인/애플리케이션 테스트를 실행한다.
- 통계 및 타임테이블은 목표 전용 서비스 정책에 따라 `goalId == null` 투두가 포함되지 않는 기존 흐름을 회귀 확인한다.

## 권장 구현 순서

1. 요청 DTO의 `goalId @NotNull` 제거.
2. `Todo.validate(...)`의 `goalId` non-null 검증 제거.
3. `Todo.update(...)`가 수정 요청의 `goalId`를 도메인 상태에 반영하는지 확인 및 보완.
4. 생성/수정 애플리케이션 서비스에서 목표 조회/검증을 `goalId != null`일 때만 수행하도록 변경.
5. `TodoEntity.goalId` nullable 반영 및 `TodoEntity.update(...)`의 `goalId` 갱신 추가.
6. `todos.goal_id` nullable 변경 Flyway migration 추가.
7. 도메인 및 애플리케이션 테스트 추가/수정.
8. `flywayMigrate`와 관련 테스트 실행.
9. 통계 및 타임테이블은 목표 전용 서비스로 유지되며, `goalId == null` 투두를 포함하기 위한 추가 처리는 하지 않았음을 리뷰 노트에 명시.

## 구현 시 주의사항

- `goalId == null`은 생성/수정 API에서만 정상 입력으로 본다.
- 목표가 있는 요청에 대한 검증 누락이 발생하지 않도록 목표 검증 로직을 별도 private method로 분리하는 방안을 고려한다.
- `TodoEntity.update(...)`에 `goalId` 갱신이 빠지면 수정 API에서 목표 제거가 DB에 반영되지 않으므로 반드시 테스트로 보호한다.
- 통계 및 타임테이블은 목표 전용 서비스라는 정책을 코드 주석 또는 테스트명으로 명확히 드러내면 후속 변경 시 혼선을 줄일 수 있다.
