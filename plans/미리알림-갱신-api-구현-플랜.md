# 미리알림 갱신 API 구현 플랜

## 1) 목표

- `PUT /api/reminders/{id}` API를 추가하여, 로그인 사용자가 기존 미리알림 시간을 새로운 시간으로 갱신할 수 있도록 한다.
- 도메인 규칙(`Reminder` 생성 시점 검증)을 `update(todoScheduledAt, remindsAt)`에서도 동일하게 강제한다.
- 갱신 완료 후 `InterimSetReminderEvent`를 발행하고 `204 No Content`를 반환한다.

## 2) 요구사항 해석

### 2-1. 유스케이스 시나리오

1. 로그인 사용자 조회
2. 로그인 사용자 미존재 시 `404`
3. 미리알림 조회
4. 미리알림 미존재 시 `404`
5. 미리알림이 소속된 투두 조회
6. 투두 미존재 시 `404`
7. 투두 일정(`scheduledAt`)을 기준으로 미리알림 갱신
8. `InterimSetReminderEvent` 발행
9. `204 No Content` 응답

### 2-2. 도메인 변경 원칙

- `Reminder.update(todoScheduledAt, remindsAt)`는 내부적으로 기존 팩토리/검증 경로를 재사용해 생성 시점과 동일한 검증을 수행한다.
- 엔티티 불변식 위반 케이스는 기존과 동일한 `ReminderErrorCode`를 사용한다.

## 3) PUT vs PATCH 선택

- 본 이슈는 리소스(`Reminder`)의 핵심 상태를 "새로운 표현(새 알림 시각)으로 교체"하는 성격이 강하고, 동일 요청 반복 시 결과가 동일한 멱등성이 중요하다.
- 따라서 기본 스펙은 `PUT`을 채택한다.
- `PATCH`는 부분 변경 포맷(예: JSON Patch/Merge Patch) 도입 시 더 적합하나, 현재 요청 모델은 단일 의미(알림 시각 갱신)로 고정되어 `PUT`이 API 의도를 더 명확히 전달한다.

## 4) 계층별 구현 계획

### 4-1. Domain (`domain/planning-domain`)

1. `Reminder`에 `update(todoScheduledAt, remindsAt)` 추가
   - 기존 팩토리/검증 로직 재사용
   - 새 `Reminder` 도메인 객체를 반환하도록 설계
2. `ReminderTest`에 성공/실패 케이스 추가
   - 성공: 유효한 시간으로 갱신
   - 실패: 기존 생성 실패 케이스와 동일한 검증 실패

### 4-2. Application (`application/application-common`, `application/planning-application`)

1. 인바운드 유스케이스 추가
   - `UpdateReminderUseCase` (신규)
2. 요청 DTO 추가
   - `UpdateReminderRequest` (신규)
3. 서비스 구현
   - `UpdateReminderService` (신규)
   - 사용자/미리알림/투두 조회 및 404 처리
   - 도메인 `update(...)` 호출
   - 영속화 포트 호출
   - 이벤트 발행
4. 아웃바운드 포트 확장
   - `ReminderCommandPort`에 갱신 메서드 추가

### 4-3. Infra (`infra/planning-infra-mysql`)

1. `ReminderPersistenceAdapter`에 갱신 메서드 구현
2. `ReminderEntity`에 `update(Reminder reminder)` 메서드 추가
3. 영속화는 JPA 더티체킹으로 반영
   - 조회된 엔티티 필드 변경 후 트랜잭션 커밋 시 반영

### 4-4. Bootstrap (`bootstrap/planning-api`, `bootstrap/bootstrap-common`)

1. `ReminderController`에 `PUT /api/reminders/{id}` 엔드포인트 추가
2. `ReminderControllerDoc`에 스펙/에러 응답 문서화
3. 필요 시 `ReminderErrorExamples` 예시 보강

## 5) 테스트 계획

## 5-1. Domain 테스트

- `ReminderTest`
  - 미리알림 시간 수정 성공
  - 미리알림 팩토리 메서드 실패와 동일한 실패 케이스 검증

## 5-2. Application 테스트

- `UpdateReminderServiceTest` (`@SpringBootTest`, `@Transactional`)
  - 성공: 미리알림 갱신 성공
  - 성공: 갱신 후 이벤트 발행 성공
  - 실패: 로그인 사용자 없음
  - 실패: 미리알림 없음
  - 실패: 투두 없음

테스트 구현 규칙:
- 테스트 메서드명 한글
- `//given`, `//when`, `//then` 명시
- 실패 케이스 `//when`은 `ThrowingCallable` 사용
- 도메인 인스턴스 생성은 `testFixtures` + `BaseFixtures` 기반 랜덤 입력 사용

## 6) 신규/변경 발생 패키지 및 클래스

### 6-1. 신규 클래스 (예상)

- `application/application-common/src/main/java/com/ddudu/application/common/dto/reminder/request/UpdateReminderRequest.java`
- `application/application-common/src/main/java/com/ddudu/application/common/port/reminder/in/UpdateReminderUseCase.java`
- `application/planning-application/src/main/java/com/ddudu/application/planning/reminder/service/UpdateReminderService.java`
- `application/planning-application/src/test/java/com/ddudu/application/planning/reminder/service/UpdateReminderServiceTest.java`

### 6-2. 변경 클래스 (예상)

- `domain/planning-domain/src/main/java/com/ddudu/domain/planning/reminder/aggregate/Reminder.java`
- `domain/planning-domain/src/test/java/com/ddudu/domain/planning/reminder/aggregate/ReminderTest.java`
- `application/application-common/src/main/java/com/ddudu/application/common/port/reminder/out/ReminderCommandPort.java`
- `application/application-common/src/main/java/com/ddudu/application/common/port/reminder/out/ReminderLoaderPort.java` (필요 시 조회 시그니처 보강)
- `infra/planning-infra-mysql/src/main/java/com/ddudu/infra/mysql/planning/reminder/adapter/ReminderPersistenceAdapter.java`
- `infra/planning-infra-mysql/src/main/java/com/ddudu/infra/mysql/planning/reminder/entity/ReminderEntity.java`
- `bootstrap/planning-api/src/main/java/com/ddudu/api/planning/reminder/controller/ReminderController.java`
- `bootstrap/planning-api/src/main/java/com/ddudu/api/planning/reminder/doc/ReminderControllerDoc.java`
- `bootstrap/bootstrap-common/src/main/java/com/ddudu/bootstrap/common/doc/examples/ReminderErrorExamples.java` (필요 시)

## 7) 작업 순서 제안

1. 도메인 `Reminder.update(...)` 설계/테스트 확정
2. Application UseCase/Service + Port 시그니처 확정
3. Infra 어댑터/엔티티(더티체킹) 구현
4. Controller/Doc 반영
5. Application/Domain 테스트 작성 및 회귀 확인
