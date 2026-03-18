# 도메인 용어 전환(Ddudu → Todo) 분석 및 구현 플랜

## 0) 목적

- 이슈에 정의된 순서(1~8)를 그대로 따르며, `Ddudu` 계열 용어를 `Todo` 계열 용어로 점진 전환한다.
- 범위는 코드/DB/문서/설정 전체이며, 기능(Behavior) 변경 없이 용어만 전환한다.

## 1) DB Flyway 신규 migration 및 afterMigrate 변경

### 분석

- 현재 DB migration 이력에는 `V8__change_todo_to_ddudu.sql`이 존재하여, 과거에 `todo → ddudu`로 한 차례 역전환된 이력이 있다.
- `afterMigrate.sql`에 `ddudu`, `repeat_ddudu`, 한글 `뚜두` 문자열이 광범위하게 분포하며, 전체 치환 영향도가 가장 높은 파일이다.
- 단순 테이블 rename만으로 끝나지 않고 FK/인덱스/시드 데이터/조회 SQL까지 동시 정합성 점검이 필요하다.

### 구현 플랜

1. `V18__change_ddudu_to_todo.sql`(가칭) 추가
   - `ddudus`/`repeat_ddudus` 계열 테이블 및 연관 FK/인덱스/컬럼명 rename 또는 재생성 SQL 작성
   - 기존 데이터 보존이 필요하면 rename 중심, 스키마 단순화를 원하면 drop/create + 데이터 마이그레이션 스크립트 분리
2. `afterMigrate.sql` 전면 정비
   - 테이블명/컬럼명/샘플 데이터 내 문자열(한글 포함)까지 `todo` 정책으로 변경
3. 로컬 검증
   - `./gradlew :bootstrap:bootstrap-gateway:flywayMigrate`로 이력 적용 및 실패 지점 확인
   - 기준 DB에서 신규 스키마/시드 데이터 적재 확인

## 2) domain, infra 모듈 변경

### 분석

- 용어 집중 구간은 `planning-domain`, `planning-infra-mysql`이며 aggregate/entity/repository/service/fixture/test 전 영역에 분포한다.
- 도메인 객체(`Ddudu`, `RepeatDdudu`)와 인프라 엔티티/리포지토리의 타입명 불일치가 생기지 않도록, 모듈 간 변경 배치를 같은 커밋 단위로 묶어야 한다.

### 구현 플랜

1. Domain 우선 변경
   - 패키지/클래스/메서드/필드: `ddudu`→`todo`, `repeatddudu`→`repeatTodo`
   - fixture 명칭/생성 메서드명도 정책 반영
2. Infra 동시 반영
   - Entity/Adapter/Repository/Querydsl(Q class 재생성 포함) 매핑명 동기화
3. 컴파일 고정
   - 도메인/인프라 모듈 단위 컴파일 및 테스트 우선 통과

## 3) application-common 모듈 변경

### 분석

- 외부 API와 내부 유스케이스를 연결하는 DTO/Port 명세가 집중되어 있어, 호환성 영향이 큰 단계다.
- request/response 필드명, interim 이벤트 DTO, stats DTO까지 광범위하게 연결되어 있다.

### 구현 플랜

1. DTO/Port 시그니처 일괄 변경
   - 클래스명/필드명/팩토리 메서드명 전환
2. 직렬화 키 정책 결정
   - API 호환이 필요하면 `@JsonProperty`로 과도기 호환, 즉시 전환이면 snake/camel 모두 todo 기준으로 통일
3. application 모듈 컴파일 에러를 드라이버로 누락 시그니처 제거

## 4) application 모듈 변경

### 분석

- `planning-application`의 서비스/유스케이스/테스트에서 `Ddudu` 의존도가 가장 높다.
- 서비스명(`CreateDduduService` 등)과 포트 호출부가 함께 바뀌므로 리팩터링 순서가 중요하다.

### 구현 플랜

1. 서비스/유스케이스 클래스명 전환
2. 포트 호출/매핑 코드 전환
3. 테스트 코드 전환
   - 테스트 메서드명에 포함된 한글 `뚜두` 식별 후 `투두`로 변경
   - given/when/then, ThrowingCallable 규칙 유지

## 5) bootstrap 모듈 변경

### 분석

- 컨트롤러/문서화(`*ControllerDoc`, `bootstrap-common` 에러 예시)에서 대외 노출 용어가 결정된다.
- URL path(`/api/ddudus` 등) 변경 여부는 클라이언트 영향이 매우 크므로 별도 의사결정이 필요하다.

### 구현 플랜

1. 컨트롤러/Doc/Swagger 스키마 명칭 전환
2. 에러 예시 클래스(`DduduErrorExamples`, `RepeatDduduErrorExamples`) 전환
3. API path 정책 확정
   - 즉시 변경: `/ddudus`→`/todos`
   - 점진 변경: Deprecated path 병행 운영 후 제거

## 6) 에러코드 등 상수의 한글 변경

### 분석

- `common` 모듈의 `DduduErrorCode`, `RepeatDduduErrorCode`와 메시지 문자열(`뚜두`)이 직접 사용자/문서에 노출된다.
- 상수 코드값(숫자)은 유지, 메시지만 변경하는지 여부를 먼저 확정해야 한다.

### 구현 플랜

1. 상수 클래스명 및 enum 항목명 전환
2. 한글 메시지 `뚜두`→`투두` 반영
3. bootstrap 예외 매핑/문서 예시와 메시지 정합성 재검증

## 7) gradle 파일 등 소스 이외 변경

### 분석

- `settings.gradle.kts`의 `rootProject.name = "ddudu"` 등 빌드/배포 식별자에 용어가 포함되어 있다.
- CI, 아티팩트명, 컨테이너명 등 외부 연동 포인트가 깨질 수 있는 단계다.

### 구현 플랜

1. Gradle/설정/문서/스크립트 문자열 일괄 점검
2. 변경 대상 분리
   - 내부 표시명(바꿔도 안전)
   - 외부 계약명(변경 시 하위 시스템 조정 필요)
3. CI 파이프라인 dry-run으로 산출물명/태스크명 영향 확인

## 8) 추가 누락 확인

### 분석

- 대량 rename 특성상 누락은 주로 테스트, SQL, 문서, 예외 메시지, API 스펙에서 발생한다.

### 구현 플랜

1. 정적 검색 체크리스트로 잔존 문자열 점검
   - `Ddudu`, `ddudu`, `RepeatDdudu`, `repeatDdudu`, `repeat-ddudu`, `repeatddudu`, `뚜두`
2. 모듈별 빌드/테스트 실행으로 잔여 참조 탐지
3. OpenAPI/에러 예시/DB schema를 교차 검증하여 최종 DoD 확인

## 실행 전략(권장)

- 큰 단일 PR 대신, 이슈의 1~8 단계를 반영한 **순차 PR(또는 stacked PR)** 로 진행
- 각 단계 완료 기준
  - 컴파일 성공
  - 해당 모듈 테스트 통과
  - 검색 키워드 잔존치가 예상 범위 내로 감소
- 최종 단계에서만 전역 rename/문서/설정 마무리

## 검증 커맨드(초안)

- `./gradlew :bootstrap:bootstrap-gateway:flywayMigrate`
- `./gradlew :domain:planning-domain:test :infra:planning-infra-mysql:test`
- `./gradlew :application:application-common:build :application:planning-application:test`
- `./gradlew :bootstrap:planning-api:test :bootstrap:stats-api:test`
- `rg -n "Ddudu|ddudu|RepeatDdudu|repeatDdudu|repeat-ddudu|repeatddudu|뚜두" domain infra application bootstrap common buildSrc`
