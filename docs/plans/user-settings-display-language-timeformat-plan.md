# PR 후속 작업 플랜: 사용자 세팅 `display.language`, `display.timeFormat` 추가

## 1) 목표 및 수용 기준

- 사용자 세팅 조회 API(`GET /api/users/settings`) 응답의 `display` 객체에 `language`, `timeFormat`이 포함되어야 한다.
- 사용자 세팅 수정 API(`PUT /api/users/settings`) 요청/응답의 `display` 객체에 `language`, `timeFormat`이 포함되어야 한다.
- `User` 도메인의 `Options.DisplayOptions`가 다음 기본값과 유효값을 가져야 한다.
  - `language`: `KO`, `EN`, 기본값 `EN`
  - `timeFormat`: `12H`, `24H`, 기본값 `12H`
- `users` 테이블에 `language`, `time_format` 컬럼이 Flyway migration으로 추가되어야 한다.
- Domain ↔ Entity 변환, API 요청 DTO ↔ Domain 갱신, Domain ↔ API 응답 DTO 매핑에 신규 필드가 누락되지 않아야 한다.
- 기존 사용자 세팅 조회/수정 테스트와 Options 기본값 테스트가 신규 필드 검증을 포함한 상태로 통과해야 한다.

## 2) 구현 단계

1. **도메인 enum 추가**
   - `Language`, `TimeFormat` enum을 `User` 도메인 enum 패키지에 추가한다.
   - 기존 `WeekStartDay.get(String)` 패턴과 동일하게 대소문자 입력을 허용하는 정적 변환 메서드를 둔다.
   - 유효하지 않은 값은 `IllegalArgumentException`으로 처리하되, 필요하면 `UserErrorCode`에 전용 코드 추가 여부를 먼저 확인한다.

2. **도메인 DisplayOptions 확장**
   - `DisplayOptions`에 `Language language`, `TimeFormat timeFormat` 필드를 추가한다.
   - 빌더 생성자에서 `language == null`이면 `Language.EN`, `timeFormat == null`이면 `TimeFormat.TWELVE_HOUR` 또는 enum 네이밍 정책에 맞춘 `12H` 대응 상수를 적용한다.
   - Java enum 상수는 숫자로 시작할 수 없으므로 `TWELVE_HOUR`/`TWENTY_FOUR_HOUR`처럼 안전한 이름을 사용하고, 요청/응답 문자열이 꼭 `12H`/`24H`여야 한다면 Jackson 매핑(`@JsonValue`, `@JsonCreator`) 또는 별도 값 필드를 검토한다.

3. **User aggregate 갱신 경로 확장**
   - `User`에 `getLanguage()`, `getTimeFormat()` 편의 getter를 추가한다.
   - `updateOptions(...)` 파라미터에 `language`, `timeFormat`을 추가하고 `DisplayOptions.builder()`에 전달한다.
   - 기존 알림/메뉴/연동 옵션은 변경하지 않고 display 옵션만 신규 필드를 추가한다.

4. **DB schema 및 JPA Entity 매핑 추가**
   - 신규 Flyway 파일 `V23__add_display_language_time_format_to_users.sql`을 추가한다.
   - `users.language VARCHAR(2) NOT NULL DEFAULT 'EN'`, `users.time_format VARCHAR(3) NOT NULL DEFAULT '12H'`를 추가한다.
   - `UserEntity`에 `Language`, `TimeFormat` 필드를 추가하고 `@Column(nullable = false)`를 지정한다.
   - `time_format`의 DB 저장값을 `12H`/`24H`로 유지하려면 `@Enumerated(EnumType.STRING)` 대신 JPA `AttributeConverter` 또는 enum 코드값 변환 방식을 사용한다.
   - `UserEntity.from(User)`, `update(User)`, `buildDisplayOptions()` 매핑에 신규 필드를 모두 반영한다.

5. **Application DTO 및 UseCase 매핑 확장**
   - `UpdateUserSettingsRequest.Display`에 `String language`, `String timeFormat`을 추가한다.
   - `UserSettingsResponse.Display`에 `Language language`, `TimeFormat timeFormat` 또는 API 요구 포맷에 맞는 문자열 필드를 추가한다.
   - `UserSettingsResponse.from(...)` 시그니처와 빌더 매핑을 확장한다.
   - `UpdateUserSettingsService`에서 요청 display 값을 `User.updateOptions(...)`로 전달하고, 저장 결과를 응답 생성 시 포함한다.
   - `GetUserSettingsService`에서 조회 결과 응답 생성 시 신규 getter 값을 전달한다.

6. **Bootstrap API 문서 확인/보강**
   - 현재 컨트롤러는 application-common DTO를 직접 사용하므로 런타임 스펙은 DTO 변경으로 반영된다.
   - Swagger 문서가 예시 객체를 별도로 갖는 경우 `display.language`, `display.timeFormat` 예시를 추가한다.
   - 예외 문서는 dependent usecase/domain에서 발생 가능한 예외를 `@ExampleObject`로 표현한다는 기존 규칙을 유지한다.

7. **테스트 및 Fixture 보강**
   - `UserFixture.createRandomOptions()`와 display 관련 fixture 메서드가 랜덤 `Language`, `TimeFormat`을 생성하도록 확장한다.
   - `OptionsTest`, `OptionSubVoTest`, `UserTest`의 옵션 생성/기본값/수정 검증에 신규 필드를 추가한다.
   - `GetUserSettingsServiceTest`, `UpdateUserSettingsServiceTest`의 요청 생성과 응답 검증에 신규 필드를 추가한다.
   - 실패 케이스를 추가한다면 테스트 규칙에 따라 `//given`, `//when`, `//then`을 명시하고 `ThrowingCallable` 람다를 사용한다.

## 3) 신규/변경 영향 클래스 및 패키지

### 3-1. 신규 예상

- **Domain enum**
  - 패키지: `com.modoo.domain.user.user.aggregate.enums`
  - `domain/user-domain/src/main/java/com/modoo/domain/user/user/aggregate/enums/Language.java`
  - `domain/user-domain/src/main/java/com/modoo/domain/user/user/aggregate/enums/TimeFormat.java`
- **Flyway migration**
  - `bootstrap/bootstrap-gateway/src/main/resources/db/migration/V23__add_display_language_time_format_to_users.sql`

### 3-2. 변경 핵심

- **Domain**
  - 패키지: `com.modoo.domain.user.user.aggregate.vo`
    - `domain/user-domain/src/main/java/com/modoo/domain/user/user/aggregate/vo/DisplayOptions.java`
  - 패키지: `com.modoo.domain.user.user.aggregate`
    - `domain/user-domain/src/main/java/com/modoo/domain/user/user/aggregate/User.java`
- **Application Common DTO**
  - 패키지: `com.modoo.application.common.dto.user.request`
    - `application/application-common/src/main/java/com/modoo/application/common/dto/user/request/UpdateUserSettingsRequest.java`
  - 패키지: `com.modoo.application.common.dto.user.response`
    - `application/application-common/src/main/java/com/modoo/application/common/dto/user/response/UserSettingsResponse.java`
- **User Application**
  - 패키지: `com.modoo.application.user.user.service`
    - `application/user-application/src/main/java/com/modoo/application/user/user/service/GetUserSettingsService.java`
    - `application/user-application/src/main/java/com/modoo/application/user/user/service/UpdateUserSettingsService.java`
- **Infra MySQL**
  - 패키지: `com.modoo.infra.mysql.user.user.entity`
    - `infra/user-infra-mysql/src/main/java/com/modoo/infra/mysql/user/user/entity/UserEntity.java`
- **Bootstrap User API 문서 확인 대상**
  - 패키지: `com.modoo.api.user.user.controller`
    - `bootstrap/user-api/src/main/java/com/modoo/api/user/user/controller/UserController.java`
  - 패키지: `com.modoo.api.user.user.doc`
    - `bootstrap/user-api/src/main/java/com/modoo/api/user/user/doc/UserControllerDoc.java`

### 3-3. 테스트 및 Fixture 영향

- **Domain Fixture/Test**
  - 패키지: `com.modoo.fixture`
    - `domain/user-domain/src/testFixtures/java/com/modoo/fixture/UserFixture.java`
  - 패키지: `com.modoo.domain.user.user.aggregate.vo`
    - `domain/user-domain/src/test/java/com/modoo/domain/user/user/aggregate/vo/OptionsTest.java`
    - `domain/user-domain/src/test/java/com/modoo/domain/user/user/aggregate/vo/OptionSubVoTest.java`
  - 패키지: `com.modoo.domain.user.user.aggregate`
    - `domain/user-domain/src/test/java/com/modoo/domain/user/user/aggregate/UserTest.java`
- **Application Test**
  - 패키지: `com.modoo.application.user.user.service`
    - `application/user-application/src/test/java/com/modoo/application/user/user/service/GetUserSettingsServiceTest.java`
    - `application/user-application/src/test/java/com/modoo/application/user/user/service/UpdateUserSettingsServiceTest.java`

## 4) 검증 순서

1. DDL 반영 확인
   - `./gradlew :bootstrap:bootstrap-gateway:flywayMigrate`
2. 도메인 테스트
   - `./gradlew :domain:user-domain:test`
3. 애플리케이션 테스트
   - `./gradlew :application:user-application:test`
4. 필요 시 전체 회귀 확인
   - `./gradlew test`

## 5) 구현 시 주의사항

- `language`, `timeFormat`은 DB 기본값과 도메인 기본값이 서로 달라지지 않도록 한 곳에서 명확히 맞춘다.
- API 스펙의 `timeFormat` 값이 `12H`/`24H`로 고정되어 있으므로 Java enum 상수명과 JSON 표현값을 분리할지 여부를 먼저 결정한다.
- 기존 `WeekStartDay`처럼 요청 DTO에서는 문자열을 받고 도메인에서 enum으로 변환하는 방식을 따르면 usecase 시그니처 변경 폭이 작다.
- 기존 사용자의 레코드도 migration 기본값으로 즉시 읽기 가능해야 하므로 `NOT NULL DEFAULT`를 포함한다.
