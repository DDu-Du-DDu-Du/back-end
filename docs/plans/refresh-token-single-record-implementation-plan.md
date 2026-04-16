# 리프레시 토큰 단일 레코드 전환 구현 플랜 (Fix: 리프레시 토큰 처리 방식 수정)

## 1) 목표 및 요구사항 요약

- `user_id + family` 조합당 refresh token 상태를 **1개 레코드**로 관리한다.
- 스키마를 아래 필드 구조로 전환한다.
  - `id`
  - `user_id`
  - `family`
  - `current_token`
  - `previous_token`
  - `refreshed_at`
- refresh 처리 로직을 CAS(Update-where-current-token) 방식으로 변경한다.
- 실패 시 재검증 분기에서 404/403/200(3분 내 재응답) 규칙을 정확히 반영한다.
- Flyway migration에서 구조 변경 + 기존 `refresh_tokens` 데이터 전체 삭제를 수행한다.

## 2) 구현 전략

### 2-1. 핵심 처리 흐름 (Token Refresh)

1. 요청 RT decode 시도 (`TokenManager.decodeRefreshToken`)
   - decode 실패 시 `401`.
2. CAS update 1차 시도
   - 조건: `user_id`, `family`, `current_token` 일치
   - 성공(영향 row=1): 새 RT를 즉시 반환 (`200`).
3. CAS update 실패(영향 row=0) 시 확인 로직
   - user 미존재: `404`
   - `user_id + family` 레코드 미존재: `404`
   - 요청 RT != `previous_token`: 레코드 삭제 후 `403`
   - 요청 RT == `previous_token` && `refreshed_at`이 현재 기준 3분 이내: 저장된 `current_token` 반환(`200`)
   - 요청 RT == `previous_token` && 3분 초과: 레코드 삭제 후 `403`

### 2-2. 도메인 모델링 방향

- 기존 `RefreshToken`의 “다중 이력 리스트 기반” 비교를 제거하고, 단일 레코드 상태 전이 관점으로 단순화한다.
- 객체지향 ask-and-tell을 위해 아래 성격의 도메인 행위를 도입한다.
  - `canReissueWith(previousToken, now, gracePeriod)`
  - `rotateTo(newToken, now)`
  - `isPreviousToken(token)` / `isCurrentToken(token)`
- 서비스는 “분기 orchestration”에 집중하고, 시간/토큰 비교 규칙은 가능한 한 도메인에 위임한다.

### 2-3. 영속 계층 전략

- CAS update는 Querydsl/JPA custom query로 구현하고 `updatedCount`를 반환한다.
- `loadByUserFamily`의 반환 타입을 단일건 중심(`Optional<RefreshToken>`)으로 정리한다.
- 삭제는 `deleteByUserFamily(userId, family)`를 재사용한다.

## 3) 신규/변경 영향 클래스 및 패키지

### 3-1. Domain (user-domain)

- 패키지: `com.modoo.domain.user.auth.aggregate`
  - **변경** `RefreshToken`
    - 필드 변경: `tokenValue` 중심 구조 → `currentToken`, `previousToken`, `refreshedAt`
    - 단일 레코드 정책에 맞는 상태 전이/검증 메서드 추가
- 패키지: `domain/user-domain/src/test/java/com/modoo/domain/user/auth/aggregate`
  - **변경** `RefreshTokenTest`
    - 단일 레코드 회전/재사용 허용시간(3분) 관련 테스트로 재구성

### 3-2. Application Common

- 패키지: `com.modoo.application.common.port.auth.out`
  - **변경** `TokenLoaderPort`
    - `List<RefreshToken> loadByUserFamily(...)` → `Optional<RefreshToken> loadByUserFamily(...)` (또는 단건 조회 메서드 추가)
  - **변경** `TokenManipulationPort`
    - CAS update 전용 메서드 추가
      - 예: `int rotateIfCurrentMatches(Long userId, int family, String expectedCurrent, String newToken, Instant now)`

### 3-3. User Application

- 패키지: `com.modoo.application.user.auth.service`
  - **변경** `TokenRefreshService`
    - 다중 토큰 목록 기반 로직 제거
    - CAS 선시도 + 실패 시 확인 분기(404/403/200) 구현
- 패키지: `com.modoo.application.user.auth.jwt`
  - **변경 가능** `TokenManager`
    - decode 실패를 `401`로 매핑하기 쉬운 예외 표준화 여부 검토
- 패키지: `application/user-application/src/test/java/com/modoo/application/user/auth/service`
  - **변경** `TokenRefreshServiceTest`
    - 요구된 7개 케이스 모두 반영
    - 테스트 메서드명 한국어, `//given //when //then`, 실패 케이스 `ThrowingCallable` 준수

### 3-4. Infra (user-infra-mysql)

- 패키지: `com.modoo.infra.mysql.user.auth.entiy`
  - **변경** `RefreshTokenEntity`
    - 컬럼 매핑 변경: `token_value` 제거, `current_token`, `previous_token`, `refreshed_at` 추가
- 패키지: `com.modoo.infra.mysql.user.auth.repository`
  - **변경** `RefreshTokenQueryRepository`
    - CAS update 메서드 및 단건 조회 메서드 시그니처 반영
  - **변경** `RefreshTokenQueryRepositoryImpl`
    - CAS update 쿼리 구현
    - 단건 조회/삭제 쿼리 정합성 점검
- 패키지: `com.modoo.infra.mysql.user.auth.adapter`
  - **변경** `AuthPersistenceAdpater`
    - 신규 port 메서드 구현 및 단건 조회로직 반영

### 3-5. Bootstrap / DB Migration

- 패키지: `bootstrap/bootstrap-gateway/src/main/resources/db/migration`
  - **신규** migration SQL
    - `refresh_tokens` 컬럼 구조 변경
    - `DELETE FROM refresh_tokens` 포함
- 패키지: `bootstrap/bootstrap-gateway/src/main/resources/db/data`
  - **변경 가능** `afterMigrate.sql`
    - 샘플 데이터가 신규 스키마와 호환되도록 수정

## 4) 상세 작업 순서

1. Flyway migration 추가 (스키마 변경 + 기존 데이터 삭제)
2. Domain `RefreshToken` 구조 및 행위 변경
3. Port 인터페이스 변경 (`TokenLoaderPort`, `TokenManipulationPort`)
4. Infra repository/adapter에 CAS update 및 단건 조회 반영
5. `TokenRefreshService` 로직 교체 (CAS 선시도 + fallback 분기)
6. 테스트 리팩터링/추가
   - domain 테스트
   - application 서비스 통합 테스트
7. 회귀 확인
   - 로그인/로그아웃/토큰발급 주요 시나리오 영향 점검

## 5) 테스트 플랜

- 사전: 신규 DDL이 있다면 `./gradlew :bootstrap:bootstrap-gateway:flywayMigrate` 실행
- 애플리케이션 테스트 타겟
  - `application/user-application`의 `TokenRefreshServiceTest`
- 도메인 테스트 타겟
  - `domain/user-domain`의 `RefreshTokenTest`
- 필수 케이스
  1. update 성공으로 early 응답 성공
  2. update 실패 + 3분 이내 current_token 응답 성공
  3. jwt decode 실패(401)
  4. user 없음(404)
  5. user+family 레코드 없음(404)
  6. previous_token 불일치로 삭제 후 403
  7. previous_token 일치이나 3분 경과로 삭제 후 403

## 6) 리스크 및 확인 포인트

- **동시성**: 동일 `user+family` 동시 refresh 요청에서 CAS로 원자성 보장 여부 확인
- **인덱스/유니크 제약**: `user_id + family` 유니크 인덱스 추가 필요성 검토
- **시간 기준**: `refreshed_at` 비교 시 DB 시간 vs 애플리케이션 시간 일관성 확보
- **호환성**: 기존 `token_value` 참조 코드 누락 여부 전역 점검

## 7) 완료 기준 (Definition of Done)

- 단일 레코드 정책 및 상태 전이가 코드/DB/테스트에 일관되게 반영된다.
- 명세된 7개 테스트 케이스가 모두 통과한다.
- migration 적용 후 refresh token 관련 테이블/샘플 데이터가 정상 동작한다.
