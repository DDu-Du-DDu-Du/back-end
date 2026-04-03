# 디바이스 토큰 중복 등록 방지 구현 플랜 (Fix: 디바이스 토큰 중복 등록)

## 1) 목표 및 요구사항 정리

- 대상 API: `PUT /api/device-tokens` (기존 POST에서 변경)
- 핵심 규칙
  1. 로그인 사용자 조회
  2. 로그인 사용자 + 요청 `platform(channel)` 기준으로 기존 토큰 조회
  3. 동일 토큰이 이미 존재하면 **신규 생성 생략**
  4. 동일 토큰이 없으면 신규 생성
- 기대 효과: 동일 사용자/플랫폼의 중복 디바이스 토큰 등록 방지로 중복 푸시 수신 제거

## 2) 구현 전략

### 2-1. 계층별 처리 흐름

- **Bootstrap(Notification API)**
  - 엔드포인트 HTTP Method를 `POST` → `PUT`으로 변경
  - Swagger 응답 코드를 `201` 중심에서 `200` 중심으로 정리
- **Application(Notification Application)**
  - 기존 `SaveDeviceTokenService`에 중복 확인 로직 추가
  - 조회 대상은 `userId + channel` 조건
  - 기존 토큰 존재 시 저장 없이 기존 토큰 ID 반환
- **Infra(Notification Infra MySQL)**
  - Repository/Adapter에 `userId + channel` 조회 메서드 추가

### 2-2. 응답/상태코드 정책

- 본 이슈는 "멱등성 있는 등록" 성격이므로 `PUT`에 맞춰 `200 OK` + `SaveDeviceTokenResponse`를 기본으로 사용
- 중복/신규 여부와 상관없이 동일 스키마(`id`) 반환
- 로그인 사용자 미존재(404), 요청값 유효성 실패(400), 인증 실패(401) 정책은 기존 유지

## 3) 신규/변경 패키지 및 클래스

## 3-1. Bootstrap (변경)

- 패키지: `com.ddudu.api.notification.device.controller`
  - **변경** `NotificationDeviceTokenController`
    - `@PostMapping` → `@PutMapping`
    - `ResponseEntity.created(null)` → `ResponseEntity.ok(...)`
- 패키지: `com.ddudu.api.notification.device.doc`
  - **변경** `NotificationDeviceTokenControllerDoc`
    - Operation summary/response code를 `PUT` 기준으로 정리 (`200`)

## 3-2. Application (변경)

- 패키지: `com.ddudu.application.notification.device`
  - **변경** `SaveDeviceTokenService`
    - 로그인 사용자 확인 후 `NotificationDeviceTokenLoaderPort`로 `userId + channel` 토큰 목록 조회
    - `request.token()` 존재 여부 검사
    - 존재 시 저장 생략 + 기존 ID 반환
    - 미존재 시 기존 저장 로직 수행
- 패키지: `com.ddudu.application.common.port.notification.out`
  - **변경** `NotificationDeviceTokenLoaderPort`
    - `List<NotificationDeviceToken> getTokensOfUserByChannel(Long userId, DeviceChannel channel)` 추가

## 3-3. Infra (변경)

- 패키지: `com.ddudu.infra.mysql.notification.device.repository`
  - **변경** `NotificationDeviceTokenRepository`
    - `findAllByUserIdAndChannel(Long userId, String channel)` 또는 enum 매핑 메서드 추가
- 패키지: `com.ddudu.infra.mysql.notification.device.adapter`
  - **변경** `NotificationDeviceTokenAdapter`
    - 신규 LoaderPort 메서드 구현 (`userId + channel` 조회 후 domain 매핑)

## 3-4. 테스트 (변경)

- 패키지: `application/notification-application/src/test/java/com/ddudu/application/notification/device`
  - **변경** `SaveDeviceTokenServiceTest`
    - 기존 성공/실패 케이스 유지
    - 신규 케이스 추가
      1. 같은 사용자/플랫폼에 동일 토큰 존재 시 저장 생략 검증
      2. 같은 사용자/플랫폼에 다른 토큰이면 저장 수행 검증
    - 테스트 메서드명 한국어 유지, `//given //when //then` 및 실패 케이스 `ThrowingCallable` 규칙 준수

## 4) 상세 작업 순서

1. Controller/Doc의 HTTP Method 및 응답코드 정리
2. LoaderPort 시그니처 확장 (`userId + channel` 조회)
3. Infra Adapter/Repository에 채널 필터 조회 구현
4. `SaveDeviceTokenService`에 중복 확인 후 분기 처리 추가
5. Application 테스트 보강 및 회귀 확인

## 5) 리스크 및 확인 포인트

- 현재 DB에 중복 데이터가 이미 존재하는 경우
  - "중복 발견 시 어떤 ID를 반환할지"(최신/최초) 규칙 명확화 필요
- Channel 저장 타입(enum/string)과 Repository 시그니처 정합성 확인 필요
- API Method 변경에 따른 클라이언트 앱 연동 시점 조율 필요

## 6) 완료 조건 (Definition of Done)

- `PUT /api/device-tokens`로 호출 시 중복 토큰 재생성 없음
- 동일 사용자/플랫폼/토큰 요청 반복 시 동일 ID 반환(또는 저장 생략 정책 일관성 보장)
- 로그인 사용자 미존재/인증/입력 검증 예외는 기존 정책 유지
- `SaveDeviceTokenServiceTest`에 중복 방지 관련 케이스 추가 및 통과
