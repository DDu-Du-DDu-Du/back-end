# 완료도 통계 조회 API 범위 확장 구현 플랜

## 1) 목표

- `GET /api/stats/completion/monthly` 조회 범위를 **요청 월의 이전달 1일 ~ 다음달 말일**로 확장한다.
- 응답 필드에 `completedCount`를 추가해 `totalCount`, `completedCount`, `uncompletedCount` 정합성을 보장한다.
- `totalCount = 0`인 날짜는 응답에서 제외한다.
- `userId` 미지정 시 로그인 사용자 기준으로 조회하고, 지정 시 관계(relationship)에 따른 공개 범위를 유지한다.

## 2) 구현 범위

### 포함

- Application: 월간 완료도 조회 기간 계산 로직 확장, 빈 날짜 제외 필터링
- Application Common DTO: 완료 개수 필드 추가
- Infra(Querydsl): 일자별 `completedCount` 집계 컬럼 추가
- Bootstrap: API 쿼리 파라미터 명(`yearMonth`) 및 문서 스펙 정합화
- Test: stats-application 통합 테스트 케이스 보강

### 제외

- Domain 모델(aggregate, policy) 구조 변경
- 신규 DB 스키마/DDL 변경
- 주간 완료도 API 스펙 변경

## 3) 신규/변경 클래스 및 패키지 명시

### 3.1 변경 대상 클래스

1. `application/application-common/src/main/java/com/ddudu/application/common/dto/stats/response/DduduCompletionResponse.java`
   - 패키지: `com.ddudu.application.common.dto.stats.response`
   - 변경: `completedCount` 필드 추가, `createEmptyResponse` 초기값 반영

2. `application/stats-application/src/main/java/com/ddudu/application/stats/service/CalculateCompletionService.java`
   - 패키지: `com.ddudu.application.stats.service`
   - 변경: 월간 조회 시 기간을 `yearMonth.minusMonths(1).atDay(1)` ~ `yearMonth.plusMonths(1).atEndOfMonth()`로 확장
   - 변경: `totalCount > 0`만 응답에 포함하도록 필터링
   - 변경: `userId == null` 처리 규칙을 월간에도 명시적으로 통일

3. `infra/planning-infra-mysql/src/main/java/com/ddudu/infra/mysql/planning/ddudu/repository/DduduQueryRepositoryImpl.java`
   - 패키지: `com.ddudu.infra.mysql.planning.ddudu.repository`
   - 변경: `projectCompletion()`에 `completedCount` 집계식 추가 후 DTO 생성자 인자 순서 반영

4. `bootstrap/stats-api/src/main/java/com/ddudu/api/stats/controller/StatsController.java`
   - 패키지: `com.ddudu.api.stats.controller`
   - 변경: 월간 API 요청 파라미터를 문서 스펙(`yearMonth`)과 일치하도록 정리

5. `bootstrap/stats-api/src/main/java/com/ddudu/api/stats/doc/StatsControllerDoc.java`
   - 패키지: `com.ddudu.api.stats.doc`
   - 변경: 월간 완료도 API의 쿼리 파라미터명/설명(`yearMonth`) 및 응답 예시 필드(`completedCount`) 반영

6. `application/stats-application/src/test/java/com/ddudu/application/stats/service/CalculateCompletionServiceTest.java`
   - 패키지: `com.ddudu.application.stats.service`
   - 변경: 기간 확장(이전달~다음달), `completedCount` 정합성, `totalCount=0` 날짜 제외, `userId` 미지정 케이스 검증 추가

### 3.2 신규 클래스

- 현재 설계 기준 **신규 클래스 추가 없음**.
- 기존 계층(Controller → UseCase/Service → Port → Repository projection) 내 변경으로 요구사항 충족 가능.

## 4) 레이어별 상세 설계

### 4.1 Bootstrap (`bootstrap/stats-api`)

- 컨트롤러와 문서에서 월간 조회 파라미터명을 `yearMonth`로 통일한다.
- 응답 스키마에서 `completedCount`가 노출되도록 문서 예시를 갱신한다.

### 4.2 Application (`application/stats-application`)

- 로그인 사용자 존재 검증, 조회 대상 사용자 존재 검증 로직은 유지한다.
- 월간 조회의 기준 기간을 확장한다.
- 포트에서 조회된 일자별 응답 중 `totalCount == 0`인 항목은 최종 응답에서 제거한다.
- `completedCount + uncompletedCount == totalCount`를 보장하도록 테스트에서 검증한다.

### 4.3 Infra (`infra/planning-infra-mysql`)

- Querydsl projection에서 일자별 `completedCount`를 직접 집계한다.
- 기존 `groupBy(dduduEntity.scheduledOn)`는 유지한다.
- 관계 기반 접근 제어(`privacyTypes`) 조건은 기존 그대로 유지한다.

## 5) 테스트 전략

1. 애플리케이션 통합 테스트 (`application/stats-application`)
   - 성공
     - 완료도 조회 성공(범위: 이전달 1일 ~ 다음달 말일)
     - `completedCount`, `uncompletedCount`, `totalCount` 정합성
     - `total=0` 날짜 응답 제외
     - `userId` 미지정 시 로그인 사용자 기준 조회
   - 실패
     - 로그인 사용자 미존재
     - 요청 사용자 미존재

2. 실행 순서
   - DDL 변경이 없으므로 `flywayMigrate`는 생략 가능
   - 대상 테스트 실행: `./gradlew :application:stats-application:test --tests "*CalculateCompletionServiceTest"`

## 6) 작업 순서(권장)

1. `DduduCompletionResponse`에 `completedCount` 필드 추가
2. Querydsl projection(`DduduQueryRepositoryImpl`)에 `completedCount` 집계 반영
3. `CalculateCompletionService` 월간 기간 확장 + 빈 날짜 제외 처리
4. `StatsController`/`StatsControllerDoc` 파라미터명 및 문서 스펙 정합화
5. `CalculateCompletionServiceTest` 시나리오 보강 및 회귀 검증

## 7) 리스크 및 체크포인트

- 기존 월간 API가 "해당 월의 모든 일자 반환(빈 날 포함)"에 의존 중인 클라이언트가 있을 수 있으므로 변경 공지 필요.
- 파라미터 키를 `date`에서 `yearMonth`로 바꿀 경우 하위 호환 정책(병행 지원 여부)을 사전 결정해야 한다.
- projection 생성자 파라미터 순서가 DTO record 선언 순서와 불일치하면 런타임 매핑 오류가 발생하므로 우선 검증한다.
