# 유저 세팅 옵션 변경 API 구현 플랜

## 1. 목표

- 로그인한 유저의 세팅 옵션을 변경하는 `PUT /api/users/settings` API를 추가한다.
- 변경 로직은 `User` 도메인의 행위를 통해 수행하고, 변경된 옵션을 그대로 응답으로 반환한다.
- `WeekStartDay.get(String)` 정적 팩토리를 통해 대소문자 입력을 허용하고, `MON`/`SUN` 외 값은 예외를 반환한다.

## 2. 구현 범위

- **Domain**: `User`의 옵션 변경 행위 및 `WeekStartDay` 정적 팩토리 강화
- **Application**: 로그인 유저 조회 + 옵션 변경 유스케이스 구현
- **Bootstrap(User API)**: 요청/응답 DTO, 컨트롤러 엔드포인트, Swagger 문서 추가
- **Infra(MySQL)**: `User` 저장 포트 재사용(추가 변경 필요 시 매핑 보완)
- **Test**: domain/application 테스트 추가

## 3. 신규/영향 클래스 및 패키지 위치

### 3.1 Domain (`domain/user-domain`)

#### 영향 클래스

- `com.ddudu.domain.user.user.aggregate.User`
  - `updateOption(...)` (또는 동등한 명명)의 옵션 변경 도메인 행위 추가/보완
- `com.ddudu.domain.user.user.aggregate.enums.WeekStartDay`
  - `get(String input)` 정적 팩토리 추가/수정 (uppercase 변환 후 검증)

#### 신규 클래스(필요 시)

- `com.ddudu.domain.user.user.exception.InvalidWeekStartDayException`
  - 현재 공통 예외 체계로 충분하면 신규 생성 없이 기존 에러 코드 재사용

### 3.2 Application Common (`application/application-common`)

#### 신규 클래스

- `com.ddudu.application.common.port.user.in.UpdateUserSettingsUseCase`
  - 유스케이스 인터페이스
- `com.ddudu.application.common.dto.user.request.UpdateUserSettingsCommand`
  - 애플리케이션 입력 커맨드 DTO
- `com.ddudu.application.common.dto.user.response.UserSettingsResponse`
  - 변경 후 반환 응답 DTO

#### 영향 클래스

- `com.ddudu.application.common.port.user.out.UserLoaderPort`
  - 저장 전용 포트 분리 여부 검토(필요 시 쓰기 포트 추가)

### 3.3 User Application (`application/user-application`)

#### 신규 클래스

- `com.ddudu.application.user.user.service.UpdateUserSettingsService`
  - 유스케이스 구현체 (`@UseCase`)

#### 영향 클래스

- 기존 사용자 조회/저장 어댑터 의존 포트 구성
  - 필요 시 `UserAppenderPort`/`UserSaverPort`를 `application-common`에 추가하고 주입

### 3.4 Bootstrap User API (`bootstrap/user-api`)

#### 신규 클래스

- `com.ddudu.api.user.user.dto.request.UpdateUserSettingsRequest`
- `com.ddudu.api.user.user.dto.response.UserSettingsApiResponse`

#### 영향 클래스

- `com.ddudu.api.user.user.controller.UserController`
  - `@PutMapping("/settings")` 엔드포인트 추가
- `com.ddudu.api.user.user.doc.UserControllerDoc`
  - API 문서 시그니처 및 예외 응답 문서화 추가

### 3.5 Infra User MySQL (`infra/user-infra-mysql`)

#### 영향 클래스

- `com.ddudu.infra.mysql.user.user.adapter.UserPersistenceAdapter`
  - 유저 저장/조회 포트 구현 재사용 확인
- `com.ddudu.infra.mysql.user.user.entity.UserEntity`
  - 옵션 JSON 매핑 필드/컨버터 영향 여부 점검

## 4. 상세 구현 단계

1. **도메인 행위 정리**
   - `User`에 옵션 갱신 메서드를 두고 내부에서 `Options`를 통째로 치환 또는 하위 값 변경
   - 불변성/유효성 정책에 맞춰 VO 생성 시 검증 수행

2. **WeekStartDay 정적 팩토리 구현**
   - `WeekStartDay.get(String input)`에서 `input.toUpperCase(Locale.ROOT)` 기반 매핑
   - `MON`, `SUN` 외 입력은 도메인 예외 반환

3. **Application 유스케이스 구현**
   - 로그인 유저 ID로 조회
   - 미존재 시 404 예외 (`UserErrorCode` 계열 사용)
   - 도메인 메서드로 옵션 변경 후 저장
   - `UserSettingsResponse`로 매핑 반환

4. **API 계층 연결**
   - Request DTO → Command 변환
   - UseCase 호출
   - Response DTO로 변환하여 `200 OK` 반환
   - Swagger 문서에 필드 설명 및 예시 보강

5. **영속성 영향 확인**
   - 옵션 객체가 JSON 컬럼으로 저장될 때 신규 구조가 정상 직렬화/역직렬화 되는지 확인
   - 기존 데이터의 누락 필드 기본값 보정 전략 점검

## 5. 테스트 계획

### 5.1 Domain 테스트 (`domain/user-domain`)

- `WeekStartDayTest`
  - 성공: `mon`, `MON`, `sun`, `SUN` 입력 매핑 성공
  - 실패: 허용되지 않은 문자열 입력 시 예외 발생
- `UserTest` 또는 `OptionsTest`
  - 옵션 갱신 시 기대 값으로 반영되는지 검증

### 5.2 Application 테스트 (`application/user-application`)

- `UpdateUserSettingsServiceTest`
  - 성공: 로그인 유저 세팅 변경 성공
  - 실패: 로그인 유저 미존재 시 예외 발생

> 테스트 작성 규칙 반영:
> - 테스트 메서드명 한국어
> - `//given //when //then` 명시
> - 실패 케이스 `ThrowingCallable` 사용
> - 도메인 Fixture는 `BaseFixtures` 기반 랜덤 데이터 사용

## 6. 리스크 및 확인 포인트

- 옵션 JSON 구조 변경 시 구버전 데이터 역직렬화 호환성 이슈
- 우선순위(`priority`) 중복/범위 검증 규칙이 명세에 없으므로, 1차 구현에서는 입력값 허용 후 후속 검증 정책 합의 필요
- API 응답과 도메인 구조를 1:1 매핑할 경우, 내부 구조 변경이 외부 스펙에 전파되지 않도록 Response DTO 분리 유지

## 7. 작업 순서 제안

1. Domain (`WeekStartDay`, `User.updateOption`) 구현 + 도메인 테스트
2. Application UseCase/Service 구현 + 애플리케이션 테스트
3. Bootstrap API DTO/Controller/Doc 구현
4. Infra 매핑 검증 및 회귀 테스트
