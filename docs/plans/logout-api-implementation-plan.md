# 로그아웃 API 구현 플랜 (Feat: 로그아웃 API 개발)

## 1) 목표 및 요구사항 정리

- 엔드포인트: `DELETE /api/auth/logout`
- 헤더
  - `Authorization: Bearer {access token}`
  - `Refresh-Token: {refresh token}`
- 성공 응답: `204 No Content`
- 비즈니스 규칙
  1. 로그인 사용자 조회
  2. 로그인 사용자가 없으면 `404`
  3. Refresh token decode
  4. decode 된 사용자 ID와 로그인 사용자 ID가 다르면 `403`
  5. decode 된 `user + family`의 모든 refresh token 삭제

## 2) 구현 전략

### 2-1. 계층별 역할

- **Bootstrap(User API)**
  - 컨트롤러에 로그아웃 API 추가
  - Swagger 문서에 요청 헤더/응답/에러 케이스 명시
- **Application(User Application + Application Common)**
  - 로그아웃 UseCase/Service 추가
  - 사용자 존재 검증, refresh token decode, 사용자 일치 검증, 패밀리 토큰 삭제 orchestration
- **Infra(User Infra MySQL)**
  - 필요 시 `userId + family` 기준 삭제 메서드 추가(현재는 목록 기반 삭제만 제공)

### 2-2. 예외/상태코드 매핑 전략

- 사용자 미존재: `MissingResourceException(AuthErrorCode.USER_NOT_FOUND)` → 404
- 사용자 불일치: `SecurityException(AuthErrorCode.INVALID_AUTHORITY)` → 403
- refresh token decode 실패/형식 오류: 기존 `TokenManager`/`AuthErrorCode` 체계 재사용

## 3) 신규/변경 영향 클래스 및 패키지

## 3-1. Bootstrap (변경)

- 패키지: `com.ddudu.api.user.auth.controller`
  - **변경** `AuthController`
    - `logout(...)` 엔드포인트 추가
    - `@DeleteMapping("/logout")` + `@Login Long loginUserId` + `@RequestHeader("Refresh-Token")`
- 패키지: `com.ddudu.api.user.auth.doc`
  - **변경** `AuthControllerDoc`
    - 로그아웃 API 스펙(204/403/404 등) 문서화

## 3-2. Application Common (신규)

- 패키지: `com.ddudu.application.common.port.auth.in`
  - **신규** `LogoutUseCase`
    - `void logout(Long loginUserId, String refreshToken)`

## 3-3. User Application (신규)

- 패키지: `com.ddudu.application.user.auth.service`
  - **신규** `LogoutService implements LogoutUseCase`
    - `UserLoaderPort`로 사용자 존재 확인
    - `TokenManager`로 refresh token decode
    - 사용자 ID 일치성 검증 (불일치 시 `SecurityException`)
    - `TokenLoaderPort`로 대상 family 로드
    - `TokenManipulationPort.deleteAllFamily(...)` 호출

## 3-4. Infra (선택적 변경)

- 패키지: `com.ddudu.infra.mysql.user.auth.adapter`
  - **선택 변경** `AuthPersistenceAdpater`
    - 성능/쿼리 단순화를 위해 `deleteByUserIdAndFamily` 전용 메서드 도입 가능
- 패키지: `com.ddudu.infra.mysql.user.auth.repository`
  - **선택 변경** `RefreshTokenRepository`
    - `void deleteAllByUserIdAndFamily(Long userId, Integer family)` 추가 가능

## 3-5. 테스트 (신규/변경)

- 패키지: `application/user-application/src/test/java/com/ddudu/application/user/auth/service`
  - **신규** `LogoutServiceTest`
    - 테스트 메서드명은 한국어
    - 실패 케이스 `ThrowingCallable` 사용
    - `//given //when //then` 명시
- 패키지: `bootstrap/user-api/src/test/java/com/ddudu/api/user/auth/controller` (기존 패턴에 맞춰)
  - **신규/변경** 로그아웃 API MVC/통합 테스트

## 4) 상세 작업 순서

1. **UseCase 추가**
   - `LogoutUseCase` 인터페이스 생성
2. **Service 구현**
   - `LogoutService` 생성 및 트랜잭션 설정
   - 사용자 조회 → 토큰 decode → 사용자 일치 검증 → family 토큰 삭제
3. **Controller 연결**
   - `AuthController`에 `logout` 메서드 추가, 204 반환
4. **문서화 반영**
   - `AuthControllerDoc`에 로그아웃 스펙 및 예외 예시 추가
5. **테스트 추가**
   - 성공: 로그아웃 성공(패밀리 토큰 삭제 호출 검증)
   - 실패1: 로그인 사용자 없음(404)
   - 실패2: 로그인 사용자와 refresh token 사용자 불일치(403)

## 5) 리스크 및 확인 포인트

- `@Login` 인자 해석 결과가 `null`인 경우의 처리 정책
  - 본 이슈 요구사항 기준으로는 사용자 미존재로 간주해 404 처리 여부 합의 필요
- `AuthErrorCode.INVALID_AUTHORITY` 메시지가 로그아웃 문맥에서 충분히 명확한지 확인 필요
- 토큰 삭제 방식
  - 현재 구현(`loadByUserFamily` 후 `deleteAllFamily`) 유지 vs 저장소 직접 삭제 쿼리 도입

## 6) 완료 조건 (Definition of Done)

- 로그아웃 API가 스펙대로 204 응답
- 요구 실패 케이스(404, 403) 반영
- Swagger 문서 반영 완료
- Application 레이어 테스트(성공/실패) 통과
- 기존 인증/토큰 갱신 기능 회귀 없음
