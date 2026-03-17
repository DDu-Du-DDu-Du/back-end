# PR 후속 작업 플랜: Ddudu 도메인 메모(memo) 필드 추가

## 1) 목표 및 수용 기준
- `ddudus` 테이블에 `memo VARCHAR(2000) NULL` 컬럼이 추가되어야 한다.
- 도메인 `Ddudu`에 `memo`가 추가되어야 하며, 2000자를 초과하면 `IllegalArgumentException`이 발생해야 한다.
- `Ddudu.update(...)` 경로에서도 `memo` 갱신이 가능해야 하며, `memo != null`인 경우 길이 검증이 적용되어야 한다.
- 영속 엔티티 `DduduEntity`의 `from(...)`, `toDomain()`, `update(...)`에 `memo` 매핑이 반영되어야 한다.
- 뚜두 상세조회 응답(`DduduDetailResponse`)에 `memo`가 포함되어야 한다.
- 기존 Ddudu 관련 테스트 및 상세조회 유즈케이스 테스트가 `memo` 필드 반영 후 통과해야 한다.

## 2) 구현 단계
1. **DB 스키마 변경(Flyway)**
   - 신규 migration SQL 추가: `ddudus.memo` 컬럼(`VARCHAR(2000) NULL`) 생성.
   - 기존 schema 대비 마이그레이션 순번 충돌 여부 확인.

2. **도메인 모델 확장 및 검증 추가**
   - `Ddudu` aggregate에 `memo` 필드와 상수(`MAX_MEMO_LENGTH = 2000`) 추가.
   - 생성/수정 시 공통 검증 로직 추가:
     - `memo == null` 이면 허용
     - `memo != null` 이고 길이 > 2000 이면 `IllegalArgumentException` 발생
   - `getFullBuilder()` 및 `update(...)`에 memo 전달 로직 반영.

3. **도메인 DTO/커맨드 반영**
   - `UpdateDduduCommand`(필요 시 `CreateDduduCommand`)에 memo 필드 추가.
   - 서비스 계층에서 명령 객체 → 도메인 업데이트 호출부에 memo 전달.

4. **인프라 엔티티/매핑 반영**
   - `DduduEntity`에 `@Column(name = "memo", length = 2000)` 필드 추가.
   - `from(Ddudu)`, `toDomain()`, `update(Ddudu)`에 memo 매핑 반영.

5. **상세조회 응답/문서 반영**
   - `DduduDetailResponse`에 memo 필드 추가 및 `from(Ddudu)` 매핑 반영.
   - 필요 시 API 문서 객체(ControllerDoc/Schema description) 예시 업데이트.

6. **테스트 보강 및 회귀 검증**
   - 도메인 테스트: memo 경계값(2000자 허용, 2001자 실패) 추가.
   - 애플리케이션 테스트: 상세조회/수정 시 memo 전달 및 응답 반영 확인.
   - Fixture 확장: 랜덤 memo 생성 메서드(유효/실패 케이스 분리) 추가.

## 3) 신규/변경 영향 클래스 및 패키지 경로

### 3-1. 신규(예상)
- `bootstrap/bootstrap-gateway/src/main/resources/db/migration/V{next}__add_memo_to_ddudus.sql`

### 3-2. 변경(핵심)
- **Domain**
  - `domain/planning-domain/src/main/java/com/ddudu/domain/planning/ddudu/aggregate/Ddudu.java`
  - `domain/planning-domain/src/main/java/com/ddudu/domain/planning/ddudu/dto/UpdateDduduCommand.java`
  - `domain/planning-domain/src/main/java/com/ddudu/domain/planning/ddudu/dto/CreateDduduCommand.java` *(생성 API에도 memo 반영 시)*
- **Application**
  - `application/planning-application/src/main/java/com/ddudu/application/planning/ddudu/service/UpdateDduduService.java`
  - `application/planning-application/src/main/java/com/ddudu/application/planning/ddudu/service/RetrieveDduduService.java` *(간접 영향: 응답 매핑 확인)*
  - `application/application-common/src/main/java/com/ddudu/application/common/dto/ddudu/response/DduduDetailResponse.java`
- **Infra**
  - `infra/planning-infra-mysql/src/main/java/com/ddudu/infra/mysql/planning/ddudu/entity/DduduEntity.java`
- **Bootstrap(API 문서/요청응답 DTO 경유 확인)**
  - `bootstrap/planning-api/src/main/java/com/ddudu/api/planning/ddudu/controller/DduduController.java`
  - `bootstrap/planning-api/src/main/java/com/ddudu/api/planning/ddudu/doc/DduduControllerDoc.java`

### 3-3. 테스트 영향(우선 반영)
- **Domain Test**
  - `domain/planning-domain/src/test/java/com/ddudu/domain/planning/ddudu/aggregate/DduduTest.java`
  - `domain/planning-domain/src/testFixtures/java/com/ddudu/fixture/DduduFixture.java`
- **Application Test**
  - `application/planning-application/src/test/java/com/ddudu/application/planning/ddudu/service/RetrieveDduduServiceTest.java`
  - `application/planning-application/src/test/java/com/ddudu/application/planning/ddudu/service/UpdateDduduServiceTest.java`

## 4) 검증 순서
1. DDL 변경이 있으면 선행 마이그레이션 실행
   - `./gradlew :bootstrap:bootstrap-gateway:flywayMigrate`
2. 도메인 테스트
   - `./gradlew :domain:planning-domain:test`
3. 애플리케이션 테스트
   - `./gradlew :application:planning-application:test --tests "*RetrieveDduduServiceTest" --tests "*UpdateDduduServiceTest"`
4. 전체 회귀(필요 시)
   - `./gradlew test`

## 5) 리스크 및 체크포인트
- DB 컬럼 추가 후 QueryDSL/Q 클래스 재생성 필요 여부 확인.
- `memo` null/blank 정책이 요구사항과 일치하는지(현재 요구사항은 길이 제한만 명시).
- 상세조회 외에 목록/검색 응답에서 memo 노출이 필요한지 범위 재확인.
