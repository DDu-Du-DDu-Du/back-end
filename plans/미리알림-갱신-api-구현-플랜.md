# 미리알림 갱신 API 구현 플랜

## 1) 목표 및 범위

- PR 요구사항인 `PUT /api/reminders/{id}`(204 No Content)를 구현한다.
- 유스케이스 순서(로그인 사용자 조회 → 리마인더 조회 → 투두 조회 → 갱신 → 이벤트 발행)를 그대로 반영한다.
- 도메인 규칙은 `Reminder.from(...)`과 동일 검증을 재사용하도록 `Reminder.update(todoScheduledAt, remindsAt)`를 추가한다.
- 인프라 갱신은 JPA 더티체킹 기반으로 처리한다(명시적 save 없이 트랜잭션 커밋 시 반영).

## 2) 메서드 선택: PUT vs PATCH

### 결론

- **이번 스펙은 `PUT`보다 `PATCH`가 더 의미적으로 적합**하다.

### 근거

- HTTP Semantics(RFC 9110)에서 `PUT`은 대상 리소스의 **전체 표현(complete replacement)**을 저장하는 의미가 강하다.
- HTTP PATCH(RFC 5789)는 리소스의 **부분 변경(partial modification)**을 위해 정의되었다.
- 이번 요청 본문은 `remindsAt` 단일 필드 변경이므로 부분 갱신 성격이다.

### 적용 제안

- 단기: 요구 스펙을 존중하여 `PUT /api/reminders/{id}` 구현.
- 중기: API 일관성 개선을 위해 `PATCH /api/reminders/{id}` 병행 제공(또는 차기 버전 전환) 검토.

## 3) 상세 구현 단계

1. 요청/유스케이스 계약 추가
   - `UpdateReminderRequest(remindsAt)` DTO 추가.
   - `UpdateReminderUseCase` 입력 계약 추가.
2. 애플리케이션 서비스 구현
   - `UpdateReminderService`에서 아래 순서 보장:
     1) 사용자 조회(없으면 404)
     2) 리마인더 조회(없으면 404)
     3) 투두 조회(없으면 404)
     4) 권한 검증(리마인더 생성자=로그인 사용자)
     5) `Reminder.update(...)` 호출
     6) 포트로 갱신 반영(더티체킹)
     7) `InterimSetReminderEvent` 발행
3. 도메인 로직 보강
   - `Reminder.update(todoScheduledAt, remindsAt)` 추가.
   - 내부에서 `Reminder.from(...)`과 동일 제약(`NULL_SCHEDULED_AT`, `NULL_REMINDS_AT`, `INVALID_REMINDS_AT`)을 강제.
4. 인프라 업데이트 경로 추가
   - `ReminderEntity.update(Reminder reminder)` 메서드 추가.
   - 어댑터에서 엔티티 조회 후 필드 변경(더티체킹 반영).
5. 부트스트랩 API/문서 반영
   - 컨트롤러에 `@PutMapping("/{id}")` 추가.
   - Swagger Doc에 204/에러 코드 예시를 확장.
6. 테스트 작성
   - Domain: `ReminderTest`에 update 성공/실패 케이스.
   - Application: `UpdateReminderServiceTest`에 성공/실패/이벤트 발행 검증.
   - 테스트 메서드명은 한국어, 실패 케이스는 `ThrowingCallable` + `//given //when //then` 규칙 준수.

## 4) 신규/변경 패키지 및 클래스

## 신규 예정

- `application/application-common/src/main/java/com/ddudu/application/common/dto/reminder/request/UpdateReminderRequest.java`
- `application/application-common/src/main/java/com/ddudu/application/common/port/reminder/in/UpdateReminderUseCase.java`
- `application/planning-application/src/main/java/com/ddudu/application/planning/reminder/service/UpdateReminderService.java`
- `application/planning-application/src/test/java/com/ddudu/application/planning/reminder/service/UpdateReminderServiceTest.java`

## 변경 예정

- `domain/planning-domain/src/main/java/com/ddudu/domain/planning/reminder/aggregate/Reminder.java`
  - `update(todoScheduledAt, remindsAt)` 추가
- `domain/planning-domain/src/test/java/com/ddudu/domain/planning/reminder/aggregate/ReminderTest.java`
  - update 성공/실패 검증 추가
- `application/application-common/src/main/java/com/ddudu/application/common/port/reminder/out/ReminderCommandPort.java`
  - update 시그니처 추가
- `infra/planning-infra-mysql/src/main/java/com/ddudu/infra/mysql/planning/reminder/adapter/ReminderPersistenceAdapter.java`
  - 조회 후 엔티티 변경 기반 업데이트 구현
- `infra/planning-infra-mysql/src/main/java/com/ddudu/infra/mysql/planning/reminder/entity/ReminderEntity.java`
  - `update(Reminder)` 메서드 추가
- `bootstrap/planning-api/src/main/java/com/ddudu/api/planning/reminder/controller/ReminderController.java`
  - `PUT /api/reminders/{id}` 엔드포인트 추가
- `bootstrap/planning-api/src/main/java/com/ddudu/api/planning/reminder/doc/ReminderControllerDoc.java`
  - 204/404/400 응답 스펙 및 예시 추가
- `bootstrap/bootstrap-common/src/main/java/com/ddudu/bootstrap/common/doc/examples/ReminderErrorExamples.java`
  - 신규/재사용 에러 예시 정합화

## 5) 예외/에러코드 매핑 계획

- 로그인 사용자 없음: `ReminderErrorCode.LOGIN_USER_NOT_EXISTING`(404)
- 리마인더 없음: `ReminderErrorCode.ID_NOT_EXISTING` 또는 동등 코드(404)
- 투두 없음: `ReminderErrorCode.TODO_NOT_EXISTING`(404)
- 유효하지 않은 remindsAt/scheduledAt: `ReminderErrorCode.NULL_SCHEDULED_AT`, `NULL_REMINDS_AT`, `INVALID_REMINDS_AT`(400)
- 권한 없음: `ReminderErrorCode.INVALID_AUTHORITY`(403)

> 주의: `ID_NOT_EXISTING` 코드 존재 여부에 따라, Reminder 전용 not found 코드가 없으면 이번 작업에서 추가 여부를 결정한다.

## 6) 테스트/검증 실행 계획

1. (DDL 변경이 있을 때만) `./gradlew :bootstrap:bootstrap-gateway:flywayMigrate`
2. 도메인 테스트
   - `./gradlew :domain:planning-domain:test --tests "*ReminderTest"`
3. 애플리케이션 테스트
   - `./gradlew :application:planning-application:test --tests "*UpdateReminderServiceTest"`
4. API 모듈 컴파일/테스트
   - `./gradlew :bootstrap:planning-api:test`

## 7) 구현 순서(권장 커밋 단위)

1. Domain(`Reminder.update`) + Domain Test
2. Application Common(port/dto) + Planning Application Service/Test
3. Infra(Dirty Checking update 경로)
4. Bootstrap API/Doc
5. 통합 테스트 및 예외/문서 예시 정리

