# 유저 세팅 조회 API 구현 플랜

## 1) 목표

- `GET /api/users/settings` API를 추가하여 로그인 사용자의 세팅 옵션 전체를 조회한다.
- 사용자 미존재 시 도메인/애플리케이션 계층의 기존 예외 체계를 활용해 `404`를 반환한다.
- 이미 `User` Aggregate에 존재하는 옵션 getter/도메인 매핑 로직을 재사용해 API 응답 DTO로 변환한다.

## 2) 구현 범위

### 포함

- Bootstrap(`bootstrap/user-api`): 컨트롤러 엔드포인트, 요청 사용자 식별, 응답 스펙 정의, Swagger 문서화
- Application(`application/user-application`): 유스케이스 인터페이스/서비스 추가, 로그인 사용자 조회 + 도메인 서비스 조합
- Domain(`domain/user-domain`): 세팅 응답 DTO 매핑 도메인 서비스 메서드(필요 시) 및 검증 테스트

### 제외

- 세팅 변경 API(Write)
- 옵션 스키마 구조 변경
- 인프라 저장 구조 변경

## 3) 레이어별 상세 설계

### 3.1 Domain (`domain/user-domain`)

1. `UserDomainService`에 "User -> 세팅 응답 DTO" 변환 메서드를 추가한다.
   - 기존 `User`의 옵션 접근 메서드(`getWeekStartDay`, `isDarkMode` 등)를 사용해 응답 전용 도메인 DTO를 생성한다.
2. 도메인 DTO(필요 시)를 `domain.user.user.service.dto` 패키지에 정의한다.
   - 계층형 구조를 응답 스펙과 동일하게 유지한다.
3. 테스트
   - 성공: 랜덤 `UserFixture` 기반으로 매핑 정확성 검증
   - 규칙 준수: 테스트 메서드명 한국어, `//given //when //then` 명시

### 3.2 Application (`application/user-application`)

1. 유스케이스 추가
   - 예: `GetUserSettingsUseCase`
   - 입력 없음(로그인 사용자 컨텍스트 기반), 출력은 애플리케이션 응답 DTO
2. 서비스 구현
   - 로그인 사용자 ID 조회 (`AuthUserPort` 또는 현재 인증 컨텍스트를 사용하는 기존 패턴 재사용)
   - `UserPort`로 사용자 조회
   - 미존재 시 `UserErrorCode` 기반 예외 발생(404 매핑)
   - `UserDomainService` 호출로 세팅 응답 변환
3. 테스트(`@SpringBootTest`, `@Transactional`)
   - 성공: 사용자 존재 시 응답 필드 전체 일치
   - 실패: 사용자 미존재 시 예외 및 에러코드 검증
   - `@BeforeEach`에서 입력 데이터 준비

### 3.3 Bootstrap (`bootstrap/user-api`)

1. 컨트롤러 엔드포인트 추가
   - `GET /api/users/settings`
2. 응답 DTO 정의
   - `display`, `menuActivation`, `appConnection` 하위 구조를 스펙과 동일하게 유지
3. 문서화
   - `UserControllerDoc`에 API 스펙 추가
   - 예외 케이스(404)를 `@ApiResponse` 및 `@ExampleObject`로 명시
   - 상태코드/에러 예시는 `bootstrap-common`의 정의를 참조

## 4) 테스트 전략

1. 도메인 테스트
   - 대상: `domain/user-domain`
   - `UserFixture` 확장/재사용으로 랜덤 데이터 기반 검증
2. 애플리케이션 테스트
   - 대상: `application/user-application`
   - 성공/실패 시나리오 모두 작성
3. 실행 순서
   - 신규 DDL이 없다면 즉시 테스트 실행
   - DDL 변경이 생길 경우 `./gradlew :bootstrap:bootstrap-gateway:flywayMigrate` 이후 테스트 실행

## 5) 작업 순서(권장)

1. Domain DTO/Service 매핑 메서드 추가 + 도메인 테스트 작성
2. Application UseCase/Service 추가 + 통합 테스트 작성
3. Bootstrap 컨트롤러/문서화 추가
4. 전체 모듈 컴파일 및 관련 테스트 실행
5. 응답 스펙 필드명(`isDarkMode`, `isActive`) 최종 점검

## 6) 리스크 및 체크포인트

- 인증 컨텍스트에서 사용자 ID를 가져오는 경로가 모듈별로 다를 수 있으므로 기존 "내 정보 조회" API의 패턴을 반드시 재사용한다.
- DTO 계층이 깊어 매핑 누락 가능성이 있으므로 필드 단위 단언을 사용한다.
- 문서와 실제 JSON 필드명이 불일치하지 않도록 스냅샷/직렬화 검증을 권장한다.
