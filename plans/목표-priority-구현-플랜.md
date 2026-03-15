# 목표 priority 도입 구현 플랜

## 1) 목표

- 목표(`Goal`)에 `priority`(int, not null)를 도입한다.
- 목표 생성 시 사용자 기준 마지막 `priority + 1`로 자동 부여한다.
- 목표 기본 생성 로직(`createDefaultGoals`)은 루프 index를 `priority`로 사용한다.
- 목표 수정/상세/전체 조회 API에 `priority`를 반영한다.
- DB 마이그레이션 및 시드 데이터(`afterMigrate.sql`)를 함께 정합성 있게 갱신한다.

## 2) 신규/영향 클래스 및 패키지 위치

### 2-1. Domain (`domain/planning-domain`)

- 영향 클래스: `com.ddudu.domain.planning.goal.aggregate.Goal`
  - 파일: `domain/planning-domain/src/main/java/com/ddudu/domain/planning/goal/aggregate/Goal.java`
  - 변경: `priority` 필드 추가, 빌더/불변 재생성 메서드(`applyGoalUpdates`, `changeStatus`)에 `priority` 전파
- 영향 클래스: `com.ddudu.domain.planning.goal.dto.CreateGoalCommand`
  - 파일: `domain/planning-domain/src/main/java/com/ddudu/domain/planning/goal/dto/CreateGoalCommand.java`
  - 변경: `priority` 전달 필드 추가
- 영향 클래스: `com.ddudu.domain.planning.goal.service.GoalDomainService`
  - 파일: `domain/planning-domain/src/main/java/com/ddudu/domain/planning/goal/service/GoalDomainService.java`
  - 변경: `createDefaultGoals()`의 `idx`를 생성 목표 `priority`로 설정

### 2-2. Application Common (`application/application-common`)

- 영향 인터페이스: `com.ddudu.application.common.port.goal.out.GoalLoaderPort`
  - 파일: `application/application-common/src/main/java/com/ddudu/application/common/port/goal/out/GoalLoaderPort.java`
  - 변경: 사용자 기준 최대 priority 조회 메서드 추가
- 영향 DTO: `com.ddudu.application.common.dto.goal.request.UpdateGoalRequest`
  - 파일: `application/application-common/src/main/java/com/ddudu/application/common/dto/goal/request/UpdateGoalRequest.java`
  - 변경: `priority` 입력 필드 및 검증 추가
- 영향 DTO: `com.ddudu.application.common.dto.goal.response.BasicGoalResponse`
  - 파일: `application/application-common/src/main/java/com/ddudu/application/common/dto/goal/response/BasicGoalResponse.java`
  - 변경: 목록 응답에 `priority` 추가
- 영향 DTO: `com.ddudu.application.common.dto.goal.response.GoalWithRepeatDduduResponse`
  - 파일: `application/application-common/src/main/java/com/ddudu/application/common/dto/goal/response/GoalWithRepeatDduduResponse.java`
  - 변경: 상세 응답에 `priority` 추가

### 2-3. Application Service (`application/planning-application`)

- 영향 클래스: `com.ddudu.application.planning.goal.service.CreateGoalService`
  - 파일: `application/planning-application/src/main/java/com/ddudu/application/planning/goal/service/CreateGoalService.java`
  - 변경: 생성 직전 `maxPriority` 조회 후 `+1` 부여(없으면 1)
- 영향 클래스: `com.ddudu.application.planning.goal.service.UpdateGoalService`
  - 파일: `application/planning-application/src/main/java/com/ddudu/application/planning/goal/service/UpdateGoalService.java`
  - 변경: 수정 요청의 `priority`를 domain update에 반영
- 영향 클래스: `com.ddudu.application.planning.goal.service.RetrieveGoalService`
  - 파일: `application/planning-application/src/main/java/com/ddudu/application/planning/goal/service/RetrieveGoalService.java`
  - 변경: 상세 응답 매핑에 priority 포함
- 영향 클래스: `com.ddudu.application.planning.goal.service.RetrieveAllGoalsService`
  - 파일: `application/planning-application/src/main/java/com/ddudu/application/planning/goal/service/RetrieveAllGoalsService.java`
  - 변경: 목록 응답 매핑에 priority 포함

### 2-4. Infra (`infra/planning-infra-mysql`)

- 영향 엔티티: `com.ddudu.infra.mysql.planning.goal.entity.GoalEntity`
  - 파일: `infra/planning-infra-mysql/src/main/java/com/ddudu/infra/mysql/planning/goal/entity/GoalEntity.java`
  - 변경: `priority` 컬럼 매핑 추가, `from/toDomain/update` 반영
- 영향 어댑터: `com.ddudu.infra.mysql.planning.goal.adapter.GoalPersistenceAdapter`
  - 파일: `infra/planning-infra-mysql/src/main/java/com/ddudu/infra/mysql/planning/goal/adapter/GoalPersistenceAdapter.java`
  - 변경: `GoalLoaderPort` 신규 메서드 구현 연결
- 영향 인터페이스: `com.ddudu.infra.mysql.planning.goal.repository.GoalQueryRepository`
  - 파일: `infra/planning-infra-mysql/src/main/java/com/ddudu/infra/mysql/planning/goal/repository/GoalQueryRepository.java`
  - 변경: 최대 priority 조회 쿼리 시그니처 추가
- 영향 구현: `com.ddudu.infra.mysql.planning.goal.repository.GoalQueryRepositoryImpl`
  - 파일: `infra/planning-infra-mysql/src/main/java/com/ddudu/infra/mysql/planning/goal/repository/GoalQueryRepositoryImpl.java`
  - 변경: Querydsl 기반 사용자 최대 priority 조회 구현

### 2-5. Bootstrap API (`bootstrap/planning-api`)

- 영향 클래스: `com.ddudu.api.planning.goal.controller.GoalController`
  - 파일: `bootstrap/planning-api/src/main/java/com/ddudu/api/planning/goal/controller/GoalController.java`
  - 변경: 수정 API request 변경 반영
- 영향 문서: `com.ddudu.api.planning.goal.doc.GoalControllerDoc`
  - 파일: `bootstrap/planning-api/src/main/java/com/ddudu/api/planning/goal/doc/GoalControllerDoc.java`
  - 변경: 요청/응답 스키마 및 예시 문서 반영

### 2-6. DB Migration / Seed (`bootstrap/bootstrap-gateway`)

- 신규 파일: `bootstrap/bootstrap-gateway/src/main/resources/db/migration/V15__add_priority_to_goals.sql`
  - 변경: `goals.priority` 컬럼 추가(not null)
- 영향 파일: `bootstrap/bootstrap-gateway/src/main/resources/db/data/afterMigrate.sql`
  - 변경: 기존 목표(`goals`) DML에 `priority` 컬럼/값 추가

## 3) 구현 단계

1. `V15__add_priority_to_goals.sql` 추가
   - 운영 데이터 고려하여 default/backfill/not null 제약 순서 안전 적용
2. `afterMigrate.sql`의 목표 DML 수정
   - `INSERT INTO goals (...)` 컬럼 목록에 `priority` 추가
   - 기존 레코드의 의미에 맞는 `priority` 값 지정
3. `Goal`/`GoalEntity`/매핑 경로에 `priority` 반영
4. 목표 생성 유스케이스에 `maxPriority + 1` 로직 반영
5. `createDefaultGoals()`의 `idx -> priority` 반영
6. 목표 수정/상세/전체 조회 DTO 및 서비스 매핑 반영
7. API 문서(`GoalControllerDoc`) 스키마/예시 반영

## 4) 테스트 플랜

- Domain
  - `GoalDomainServiceTest`: 기본 목표 생성 시 `priority`가 1..N으로 매핑되는지 검증
  - `GoalTest`: 업데이트/상태변경 시 `priority` 유지/변경 검증
- Application
  - `CreateGoalServiceTest`: 사용자 첫 목표 생성 시 `priority=1`, 이후 `+1` 검증
  - `UpdateGoalServiceTest`: `priority` 수정 반영 검증
  - `RetrieveGoalServiceTest`, `RetrieveAllGoalsServiceTest`: 응답에 `priority` 포함 검증
- 테스트 규칙
  - `//given`, `//when`, `//then` 명시
  - 실패 케이스는 `ThrowingCallable` 사용
  - fixture는 `domain/planning-domain/src/testFixtures`의 랜덤 생성 메서드 확장하여 사용

## 5) 체크 포인트

- 정렬 기준 필요 시 정책 확정
  - 현재 목록 조회 정렬이 `status desc, id desc` 중심이므로, priority 우선 정렬이 필요한지 별도 합의 필요
- 생성 API의 `priority` 입력 허용 여부 확정
  - 본 플랜은 서버 자동 부여(`max + 1`)를 기본 정책으로 둠
- 마이그레이션 이후 시드 SQL(`afterMigrate.sql`)이 Flyway/초기화 파이프라인에서 정상 수행되는지 확인

