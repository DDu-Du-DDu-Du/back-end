# 투두 미리알림 취소 API 구현 플랜

## 1) 목표

- `DELETE /api/reminders/{id}` API를 추가하여, 로그인 사용자가 설정한 미리알림을 취소(삭제)할 수 있도록 한다.
- 도메인(`domain/*`)의 비즈니스 규칙 변경 없이 애플리케이션/인프라/부트스트랩 계층에서 요구사항을 충족한다.

## 2) 수용 기준 (Acceptance Criteria)

1. 로그인 사용자 조회에 실패하면 `404 Not Found`를 반환한다.
2. 요청한 미리알림이 이미 발송(`isReminded == true`)된 상태면 `422 Unprocessable Entity`를 반환한다.
3. 미리알림이 존재하고 아직 발송 전이면 삭제 후 `204 No Content`를 반환한다.
4. 미리알림이 존재하지 않아도 멱등하게 `204 No Content`를 반환한다.

## 3) 예외/상태코드 설계

- `404`: 기존 `ReminderErrorCode.LOGIN_USER_NOT_EXISTING` 재사용.
- `422`: `UnsupportedOperationException` 계열 매핑을 활용해 `ReminderErrorCode`에 신규 에러코드를 추가하는 방식 제안.
  - 예시 이름: `REMINDER_ALREADY_REMINDED` (메시지: "이미 발송된 미리알림은 취소할 수 없습니다.")
  - 이유: 현재 글로벌 예외 처리기에서 `UnsupportedOperationException`은 비즈니스 불가 상태와 의미적으로 잘 맞고, HTTP 422와도 대응된다.

## 4) 구현 단계

### 4-1. Application 계층 유스케이스 추가

1. `CancelReminderByIdUseCase`(신규) 정의
   - 입력: `loginId`, `reminderId`
   - 출력: 없음 (`void`)
2. `CancelReminderByIdService`(신규) 구현
   - 사용자 조회 (`UserLoaderPort`)
   - 미리알림 조회 (`ReminderLoaderPort#getOptionalReminder`)
   - 존재하지 않으면 즉시 종료(멱등 204)
   - 존재하면 소유자 검증(본인 리마인더인지)
   - `isReminded`면 422 예외 발생
   - 발송 전이면 삭제 포트 호출

### 4-2. Port/Infra 계층 확장

1. `DeleteReminderPort`(신규) 추가
   - `void deleteById(Long reminderId)` 또는 `void delete(Reminder reminder)`
2. `ReminderCommandPort`에 삭제 메서드 추가(또는 분리 포트 채택)
3. `ReminderPersistenceAdapter` 변경
   - 삭제 메서드 구현
4. `ReminderRepository` 변경
   - 필요 시 사용자/상태 검증용 조회 메서드 추가

### 4-3. Bootstrap(API/문서) 계층 반영

1. `ReminderController`에 `@DeleteMapping("/{id}")` 추가
2. `ReminderControllerDoc`에 취소 API 스펙 추가
   - 204 / 401 / 404 / 422 응답 정의
3. `ReminderErrorExamples`에 422 예시 추가

### 4-4. 공통 예외 코드 반영

1. `ReminderErrorCode`에 `REMINDER_ALREADY_REMINDED` 추가
2. 필요 시 `ErrorCodeParser` 매핑 영향 확인(기존 enum 기반 파싱 구조 재확인)

### 4-5. 테스트 작성

- 대상: `application/*-application`, `domain/*-domain` 규칙 준수
- 이번 이슈는 도메인 변경 없음 → 애플리케이션 서비스 테스트 중심

테스트 케이스:
1. 성공: 발송 전 미리알림 취소 시 삭제되고 정상 종료
2. 성공: 존재하지 않는 미리알림 취소 요청 시 정상 종료(멱등)
3. 실패: 로그인 사용자 없음 → 404
4. 실패: 이미 발송된 미리알림 취소 요청 → 422

테스트 구현 규칙:
- 메서드명 한글
- `//given //when //then` 명시
- 실패 케이스의 `//when`은 `ThrowingCallable` 사용

## 5) 신규/변경 예상 패키지 및 클래스

### 5-1. 신규

- `application/application-common/src/main/java/com/ddudu/application/common/port/reminder/in/CancelReminderByIdUseCase.java`
- `application/application-common/src/main/java/com/ddudu/application/common/port/reminder/out/DeleteReminderPort.java` (분리 포트 선택 시)
- `application/planning-application/src/main/java/com/ddudu/application/planning/reminder/service/CancelReminderByIdService.java`
- `application/planning-application/src/test/java/com/ddudu/application/planning/reminder/service/CancelReminderByIdServiceTest.java`

### 5-2. 변경

- `bootstrap/planning-api/src/main/java/com/ddudu/api/planning/reminder/controller/ReminderController.java`
- `bootstrap/planning-api/src/main/java/com/ddudu/api/planning/reminder/doc/ReminderControllerDoc.java`
- `bootstrap/bootstrap-common/src/main/java/com/ddudu/bootstrap/common/doc/examples/ReminderErrorExamples.java`
- `common/src/main/java/com/ddudu/common/exception/ReminderErrorCode.java`
- `application/application-common/src/main/java/com/ddudu/application/common/port/reminder/out/ReminderCommandPort.java` (통합 포트 선택 시)
- `infra/planning-infra-mysql/src/main/java/com/ddudu/infra/mysql/planning/reminder/adapter/ReminderPersistenceAdapter.java`
- `infra/planning-infra-mysql/src/main/java/com/ddudu/infra/mysql/planning/reminder/repository/ReminderRepository.java`

## 6) 구현 시 체크포인트

- 멱등 요구사항(미존재 204) 때문에 "없음"을 예외로 처리하지 않도록 주의
- 이미 발송된 상태 판별 로직(`remindedAt != null` 혹은 도메인 메서드) 일관성 유지
- 인증/인가 실패와 리소스 미존재를 기존 프로젝트 정책(보안상 404 반환)에 맞춰 정렬
- Swagger ExampleObject를 bootstrap-common의 표준 형식으로 등록

## 7) 작업 순서 제안

1. `ReminderErrorCode`/예외 정책 먼저 확정
2. Application UseCase/Service 및 Port 시그니처 확정
3. Infra Adapter/Repository 구현
4. Controller + Doc + ErrorExample 반영
5. Application 테스트 작성 및 통과 확인
6. 회귀 테스트(기존 reminder 생성 API 영향) 확인

