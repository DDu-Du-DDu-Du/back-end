# 알림 인박스 상태 확인 API 구현 플랜

## 1) 목표

- `GET /api/notification-inboxes/status` API를 추가하여 로그인 사용자의 알림 인박스 unread 상태를 단일 호출로 조회한다.
- 응답은 `hasNew`, `unreadCount`를 제공하며, 로그인 사용자가 없으면 `404`를 반환한다.
- 기존 알림 인박스 목록/읽음 처리 흐름의 계층 구조(bootstrap → application → port → infra)를 그대로 재사용한다.

## 2) 범위

### 포함

- Bootstrap: 상태 조회 엔드포인트/문서화 추가
- Application: 상태 조회 유스케이스/서비스 및 응답 DTO 추가
- Application Common Port: in/out 포트 메서드 확장
- Infra(MySQL): unread count 조회 쿼리 추가
- 테스트: `application/notification-application` 서비스 테스트 추가

### 제외

- 알림 인박스 도메인 모델(`NotificationInbox`) 변경
- 알림 인박스 읽음 처리 API 스펙 변경
- DB 스키마(DDL) 변경

## 3) 신규/변경 영향 패키지 및 클래스

> 클래스명은 현재 코드베이스 네이밍 패턴 기준의 제안안이며, 구현 중 컨벤션에 맞게 미세 조정 가능.

### 3.1 Bootstrap (`bootstrap/notification-api`)

- **변경** `com.ddudu.api.notification.inbox.controller.NotificationInboxController`
  - `GET /status` 엔드포인트 메서드 추가
- **변경** `com.ddudu.api.notification.inbox.doc.NotificationInboxControllerDoc`
  - 상태 조회 API 시그니처 + Swagger `@Operation`, `@ApiResponse(200/401/404)` 추가

### 3.2 Application Common (`application/application-common`)

- **신규** `com.ddudu.application.common.dto.notification.response.NotificationInboxStatusResponse`
  - 필드: `boolean hasNew`, `long unreadCount`
- **변경** `com.ddudu.application.common.port.notification.in.NotificationInboxSearchUseCase`
  - `status(Long loginId)` 메서드 추가 **또는** 아래 신규 UseCase 분리
- **신규(권장)** `com.ddudu.application.common.port.notification.in.GetNotificationInboxStatusUseCase`
  - 관심사 분리를 위해 상태 조회 전용 유스케이스 인터페이스
- **변경** `com.ddudu.application.common.port.notification.out.NotificationInboxLoaderPort`
  - `countUnreadByUserId(Long userId)` 메서드 추가

### 3.3 Application (`application/notification-application`)

- **신규(권장)** `com.ddudu.application.notification.inbox.GetNotificationInboxStatusService`
  - 로그인 사용자 조회
  - 사용자 미존재 시 `NotificationInboxErrorCode.LOGIN_USER_NOT_EXISTING` 예외
  - unread count 조회 후 응답 DTO 변환
- **변경(대안)** 기존 `NotificationInboxSearchService`에 상태 조회 로직을 함께 추가 가능하나, SRP 관점에서 비권장
- **신규 테스트** `com.ddudu.application.notification.inbox.GetNotificationInboxStatusServiceTest`
  - 성공/실패 케이스 작성 (`@SpringBootTest`, `@Transactional`, `@BeforeEach`)

### 3.4 Infra (`infra/notification-infra-mysql`)

- **변경** `com.ddudu.infra.mysql.notification.inbox.adapter.NotificationInboxAdapter`
  - `countUnreadByUserId` 구현
- **변경** `com.ddudu.infra.mysql.notification.inbox.repository.NotificationInboxQueryRepository`
  - unread count 조회 메서드 선언
- **변경** `com.ddudu.infra.mysql.notification.inbox.repository.NotificationInboxQueryRepositoryImpl`
  - Querydsl로 `user_id = :userId AND read_at IS NULL` count 쿼리 구현

## 4) 상세 설계

### 4.1 API 계약

- Endpoint: `GET /api/notification-inboxes/status`
- Response(200):
  - `hasNew`: `unreadCount > 0`
  - `unreadCount`: 로그인 사용자 기준 unread 인박스 수
- Error:
  - `401`: 인증 실패
  - `404`: 로그인 사용자 없음 (`11007`)

### 4.2 애플리케이션 플로우

1. `@Login`으로 `loginId` 획득
2. `UserLoaderPort.getUserOrElseThrow(loginId, LOGIN_USER_NOT_EXISTING)`
3. `NotificationInboxLoaderPort.countUnreadByUserId(userId)` 호출
4. `NotificationInboxStatusResponse(hasNew = unreadCount > 0, unreadCount)` 반환

### 4.3 쿼리 설계

- 조건: `notification_inbox.user_id = :userId AND notification_inbox.read_at IS NULL`
- 반환: `long unreadCount`
- 정렬/페이징 불필요 (count 단건)

## 5) 테스트 전략

## 5.1 Application 테스트 (`application/notification-application`)

- 성공 1: unread 1건 이상일 때 `hasNew=true`, `unreadCount` 일치
- 성공 2: unread 0건일 때 `hasNew=false`, `unreadCount=0`
- 실패 1: 로그인 사용자 미존재 시 404 예외 코드 검증

테스트 작성 규칙:
- 테스트 메서드명은 한국어
- `//given`, `//when`, `//then` 명시
- 실패 케이스 `//when`은 `ThrowingCallable` 사용
- `@SpringBootTest`, `@Transactional`, `@BeforeEach`로 데이터 준비

## 6) 구현 순서

1. `application-common`에 상태 응답 DTO + 유스케이스/포트 정의 추가
2. `notification-application` 서비스 구현 + 테스트 작성
3. `notification-infra-mysql` 쿼리/어댑터 구현
4. `notification-api` 컨트롤러/문서화 반영
5. 관련 모듈 테스트 실행 후 회귀 확인

## 7) 검증 커맨드(권장)

- `./gradlew :application:notification-application:test --tests "*GetNotificationInboxStatusServiceTest"`
- `./gradlew :bootstrap:notification-api:compileJava`
- 변경 범위 회귀 확인 시: `./gradlew :application:notification-application:test`

## 8) 리스크 및 체크포인트

- 기존 `NotificationInboxSearchUseCase`에 상태 조회를 합치면 인터페이스 응집도가 떨어질 수 있어 전용 `GetNotificationInboxStatusUseCase` 분리를 우선 검토한다.
- unread count 쿼리는 단순하지만, 대용량 사용자에서 인덱스(`user_id`, `read_at`) 효율을 확인한다.
- 문서화에서 `404(11007)` 예시 누락 시 운영 문서 불일치가 발생할 수 있으므로 `NotificationInboxErrorExamples`를 재사용한다.
