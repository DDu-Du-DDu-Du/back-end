# 투두 API 타임존 반영 구현 플랜

## 1) 목표

- 클라이언트가 전달한 `timeZone`을 기준으로 투두의 `scheduledOn`, `beginAt`, `endAt`을 응답/저장 방향에 맞게 변환한다.
- 조회 API는 클라이언트가 요청한 `date`, `yearMonth`, 기간, 요일/시간 조건을 먼저 `timeZone` 기준의 시작/종료 시각으로 해석한 뒤 UTC 쿼리 조건으로 변환하고, DB/도메인에 저장된 UTC 기준 `scheduledOn + beginAt/endAt`을 클라이언트 타임존 기준의 날짜/시간으로 변환해 응답한다.
- 생성/수정 API는 클라이언트 타임존 기준으로 전달된 `scheduledOn + beginAt/endAt`을 UTC 기준의 날짜/시간으로 역변환해 도메인 및 DB에 저장한다.
- `beginAt`, `endAt`이 모두 없는 날짜 전용 투두는 시간 변환을 수행하지 않고 기존 `scheduledOn`을 유지한다.
- `timeZone`이 없거나 blank인 경우 `UTC`로 간주한다.
- `timeZone` 형식은 IANA Zone ID(`Asia/Seoul` 등)를 사용하고, Java `ZoneId.of(...)` 기반으로 검증한다.

## 2) 구현 범위

### 포함 API

#### 조회 응답 변환 대상

- `GET /api/todos/daily/list`
- `GET /api/todos/daily/timetable`
- `GET /api/stats/completion/weekly`
- `GET /api/stats/completion/monthly`
- `GET /api/todos/{id}`
- `GET /api/todos`
- `GET /api/todos/dashboard`

#### 요청 저장 변환 대상

- `POST /api/todos`
- `PUT /api/todos/{id}`
- `PUT /api/todos/{id}/date`
- `POST /api/todos/{id}/repeat`
- `PUT /api/todos/{id}/period`

### 제외

- DB 스키마/DDL 변경은 하지 않는다. 저장 컬럼(`scheduledOn`, `beginAt`, `endAt`)은 기존 UTC 기준 의미만 명확히 유지한다.
- 도메인 엔티티에 타임존 필드를 추가하지 않는다.
- 리마인더 `remindsAt` 자체의 스펙 변경은 본 작업에 포함하지 않고, 투두의 UTC `scheduleDatetime` 재계산 흐름을 유지한다.

## 3) 변환 정책

### 3.1 조회 방향: 클라이언트 조회 조건 → UTC 쿼리 조건 → 클라이언트 응답값

1. `timeZone`을 `ZoneId`로 파싱한다. 값이 없으면 `ZoneOffset.UTC`를 사용한다.
2. 조회 조건의 `date`, `yearMonth`, 요일, 시작/종료 `time`은 애플리케이션 서비스에서 먼저 client zone 기준의 `ZonedDateTime` 범위로 만든다.
3. client zone 범위를 `withZoneSameInstant(ZoneOffset.UTC)`로 UTC 범위로 변환하고, 변환된 `LocalDate`와 `LocalTime`을 repository/query port 조건으로 전달한다.
   - 예: `GET /api/todos/daily/list?date=2026-05-06&timeZone=Asia/Seoul`은 `2026-05-06T00:00:00+09:00` ~ `2026-05-06T23:59:59.999999999+09:00`을 UTC 범위로 변환한 뒤 `scheduledOn/time` 조건으로 조회한다.
   - `BasicTodoResponse`, `GoalGroupedTodos`, `TodoForTimetable`처럼 응답 DTO가 단순하더라도, 일자/요일/시간 기반 조회라면 DTO 변환 전에 쿼리 기준 날짜와 시간을 반드시 client zone → UTC로 변환한다.
4. 조회된 `beginAt`, `endAt`이 모두 없으면 응답 `scheduledOn`은 변환하지 않는다.
5. 시간이 하나라도 있으면 각각 `scheduledOn.atTime(time)`을 UTC `ZonedDateTime`으로 만든다.
6. `withZoneSameInstant(clientZone)`으로 같은 instant를 클라이언트 타임존 기준으로 이동한다.
7. 변환된 값의 `toLocalDate()`, `toLocalTime()`으로 응답 `scheduledOn`, `beginAt`, `endAt`을 재구성한다.
8. `beginAt`, `endAt` 중 하나만 존재하는 비정상/부분 데이터도 존재하는 시간만 변환하되, 응답 날짜는 우선순위를 `beginAt` 변환 날짜 → `endAt` 변환 날짜 → 원본 `scheduledOn`으로 둔다.

### 3.2 저장 방향: 클라이언트 요청값 → UTC 저장값

1. `timeZone`을 `ZoneId`로 파싱한다. 값이 없으면 `ZoneOffset.UTC`를 사용한다.
2. `beginAt`, `endAt`이 모두 없으면 `scheduledOn`은 변환하지 않는다.
3. 시간이 하나라도 있으면 각각 `scheduledOn.atTime(time)`을 client `ZonedDateTime`으로 만든다.
4. `withZoneSameInstant(ZoneOffset.UTC)`로 UTC 기준으로 이동한다.
5. 변환된 값의 `toLocalDate()`, `toLocalTime()`으로 커맨드/도메인 저장 값을 재구성한다.
6. `scheduledOn`이 날짜 경계를 넘어갈 수 있으므로, 저장 전 검증/반복 생성/통계 조회 조건이 모두 UTC 날짜 기준으로 수행되는지 테스트한다.

## 4) 신규/변경 패키지 및 클래스

### 4.1 Common 유틸 (신규)

1. `common/src/main/java/com/modoo/common/time/TimeZoneConverter.java`
   - 패키지: `com.modoo.common.time`
   - 역할: 순수 Java 시간 변환 유틸.
   - 주요 메서드
     - `ZoneId parseOrUtc(String timeZone)`
     - `ZonedDateTime toClientZonedDateTime(LocalDate scheduledOn, LocalTime time, ZoneId clientZone)`
     - `ZonedDateTime toUtcZonedDateTime(LocalDate scheduledOn, LocalTime time, ZoneId clientZone)`
     - `DateTimeRange toUtcRange(LocalDate clientDate, LocalTime startTime, LocalTime endTime, ZoneId clientZone)`
   - `ZoneId.of(...)` 실패 시 공통 예외 코드로 변환할 수 있도록 `IllegalArgumentException` 또는 신규 에러 코드를 사용한다.
   - 응답 필드 묶음용 공통 record는 추가하지 않는다. 변환된 `scheduledOn/beginAt/endAt` 묶음은 각 request/response DTO factory 또는 application service 내부 지역 변수로 조립한다.

2. `common/src/main/java/com/modoo/common/time/DateTimeRange.java` (신규 후보)
   - 패키지: `com.modoo.common.time`
   - 역할: client 조회 조건을 UTC 쿼리 조건으로 바꾼 시작/종료 `LocalDateTime` 또는 `LocalDate + LocalTime` 범위를 표현한다.
   - 일별/시간표/대시보드/통계처럼 조회 전 쿼리 범위 변환이 필요한 서비스에서 사용한다.
   - 단순 응답 필드 묶음이 아니라 repository 조건 전달을 위한 범위 객체로만 사용한다.

3. `common/src/test/java/com/modoo/common/time/TimeZoneConverterTest.java`
   - 패키지: `com.modoo.common.time`
   - 참고: AGENTS의 테스트 생성 범위는 domain/application 모듈 중심이지만, 변환 유틸이 common에 위치하면 최소 단위 테스트를 추가하는 편이 안전하다. 범위를 엄격히 제한해야 한다면 application 테스트에서 유틸 경로를 커버한다.

### 4.2 Common 예외/문서 (변경 후보)

1. `common/src/main/java/com/modoo/common/exception/TodoErrorCode.java`
   - 패키지: `com.modoo.common.exception`
   - 변경: 잘못된 타임존 입력용 에러 코드 추가 후보(`INVALID_TIME_ZONE`).

2. `common/src/main/java/com/modoo/common/exception/StatsErrorCode.java`
   - 패키지: `com.modoo.common.exception`
   - 변경: 통계 API에서 잘못된 타임존 입력을 별도 코드로 내려야 한다면 `INVALID_TIME_ZONE` 추가 후보.
   - 대안: 타임존 검증 실패를 bootstrap 공통 bad request로 통일해 도메인별 에러 코드 추가를 피한다.

3. `bootstrap/bootstrap-common/src/main/java/com/modoo/bootstrap/common/doc/examples/TodoErrorExamples.java`
   - 패키지: `com.modoo.bootstrap.common.doc.examples`
   - 변경: Todo API 문서에 잘못된 타임존 `@ExampleObject` 추가.

4. `bootstrap/bootstrap-common/src/main/java/com/modoo/bootstrap/common/doc/examples/StatsErrorExamples.java`
   - 패키지: `com.modoo.bootstrap.common.doc.examples`
   - 변경: Stats API 문서에 잘못된 타임존 `@ExampleObject` 추가.

### 4.3 Bootstrap - Todo API (변경)

1. `bootstrap/planning-api/src/main/java/com/modoo/api/planning/todo/controller/TodoController.java`
   - 패키지: `com.modoo.api.planning.todo.controller`
   - 변경: 조회 API 쿼리 파라미터와 요청 DTO에 `timeZone`을 연결한다.
   - 상세
     - `getDailyList(..., String timeZone)` 추가 후 usecase에 전달
     - `getDailyTimetable(..., String timeZone)` 추가 후 usecase에 전달
     - `getDashboard(..., @RequestParam(required = false) String timeZone)` 추가 후 usecase에 전달
     - `getList(..., TodoSearchRequest request)`에서 `TodoSearchRequest`가 `timeZone`을 받도록 생성자 확장
     - `getById(..., @RequestParam(required = false) String timeZone)` 추가 후 usecase에 전달
     - `create`, `update`, `moveDate`, `repeatOnAnotherDay`, `setUpPeriod`는 요청 record의 `timeZone` 필드를 사용

2. `bootstrap/planning-api/src/main/java/com/modoo/api/planning/todo/doc/TodoControllerDoc.java`
   - 패키지: `com.modoo.api.planning.todo.doc`
   - 변경: `timeZone` query/body 스키마 문서화, 예시 `Asia/Seoul`, 기본값 `UTC`, invalid timezone 예외 예시 추가.

### 4.4 Bootstrap - Stats API (변경)

1. `bootstrap/stats-api/src/main/java/com/modoo/api/stats/controller/StatsController.java`
   - 패키지: `com.modoo.api.stats.controller`
   - 변경: `calculateWeekly`, `calculateMonthly`에 `@RequestParam(required = false) String timeZone` 추가 후 usecase에 전달.

2. `bootstrap/stats-api/src/main/java/com/modoo/api/stats/doc/StatsControllerDoc.java`
   - 패키지: `com.modoo.api.stats.doc`
   - 변경: 완료도 주간/월간 API의 `timeZone` query param과 invalid timezone 예외 예시 추가.

### 4.5 Application Common DTO - Todo 요청 (변경)

1. `application/application-common/src/main/java/com/modoo/application/common/dto/todo/request/CreateTodoRequest.java`
   - 패키지: `com.modoo.application.common.dto.todo.request`
   - 변경: `String timeZone` 필드 추가.
   - `toCommand()`에서 `TimeZoneConverter.convertToUtc(...)` 결과를 `CreateTodoCommand`에 주입.

2. `application/application-common/src/main/java/com/modoo/application/common/dto/todo/request/UpdateTodoRequest.java`
   - 패키지: `com.modoo.application.common.dto.todo.request`
   - 변경: `String timeZone` 필드 추가.
   - `toCommand()`에서 UTC 변환 결과를 `UpdateTodoCommand`에 주입.

3. `application/application-common/src/main/java/com/modoo/application/common/dto/todo/request/MoveDateRequest.java`
   - 패키지: `com.modoo.application.common.dto.todo.request`
   - 변경: `String timeZone` 필드 추가.
   - 날짜만 받는 요청이지만 기존 투두에 시간이 있으면 `newDate + 기존 beginAt/endAt`을 서비스에서 UTC로 역변환해야 한다.

4. `application/application-common/src/main/java/com/modoo/application/common/dto/todo/request/RepeatAnotherDayRequest.java`
   - 패키지: `com.modoo.application.common.dto.todo.request`
   - 변경: `String timeZone` 필드 추가.
   - 반복 대상 날짜를 클라이언트 날짜로 보고, 원본 투두의 시간과 결합해 UTC 생성값으로 변환한다.

5. `application/application-common/src/main/java/com/modoo/application/common/dto/todo/request/PeriodSetupRequest.java`
   - 패키지: `com.modoo.application.common.dto.todo.request`
   - 변경: `String timeZone` 필드 추가.
   - 기존 `scheduledOn`과 신규 begin/end를 결합해 UTC 저장값으로 변환한다.

6. `application/application-common/src/main/java/com/modoo/application/common/dto/todo/request/TodoSearchRequest.java`
   - 패키지: `com.modoo.application.common.dto.todo.request`
   - 변경: `String timeZone` query param 필드 추가, 생성자에 인자 추가.

### 4.6 Application Common DTO - Todo 응답 (변경)

1. `application/application-common/src/main/java/com/modoo/application/common/dto/todo/response/TodoDetailResponse.java`
   - 패키지: `com.modoo.application.common.dto.todo.response`
   - 변경: `from(Todo todo, List<RetrieveReminderResponse> reminders, String timeZone)` 또는 `from(..., ZoneId clientZone)` overload 추가.

2. `application/application-common/src/main/java/com/modoo/application/common/dto/todo/response/TodoDashboardItem.java`
   - 패키지: `com.modoo.application.common.dto.todo.response`
   - 변경: `from(Todo todo, String timeZone)` 또는 `from(..., ZoneId clientZone)` overload 추가.

3. `application/application-common/src/main/java/com/modoo/application/common/dto/todo/response/BasicTodoResponse.java`
   - 패키지: `com.modoo.application.common.dto.todo.response`
   - 변경 후보: 현재 필드에는 `scheduledOn/beginAt/endAt`이 없어 응답 변환 대상이 아닐 수 있다. 다만 `BasicTodoResponse`를 포함하는 일별/요일 기반 조회는 application service에서 query 시작 날짜와 시간을 먼저 client zone 기준으로 해석한 뒤 UTC 조건으로 변환해 조회한다. API 응답에서 시간 필드를 포함해야 한다면 별도 DTO 확장 또는 factory overload를 추가한다.

4. `application/application-common/src/main/java/com/modoo/application/common/dto/todo/TodoForTimetable.java`
   - 패키지: `com.modoo.application.common.dto.todo`
   - 변경: `of(Todo todo, String color, String timeZone)` 또는 `of(..., ZoneId clientZone)` overload 추가.

5. `application/application-common/src/main/java/com/modoo/application/common/dto/todo/SimpleTodoSearchDto.java`
   - 패키지: `com.modoo.application.common.dto.todo`
   - 변경: 검색 응답의 `scheduledOn`을 클라이언트 타임존 기준으로 내려야 하므로 repository projection 이후 application에서 변환하거나, DTO 생성 지점에 변환 팩토리를 추가한다.

6. `application/application-common/src/main/java/com/modoo/application/common/dto/todo/GoalGroupedTodos.java`
   - 패키지: `com.modoo.application.common.dto.todo`
   - 변경 후보: 현재는 `BasicTodoResponse` 리스트만 포함한다. 일별/요일 기반 목록 쿼리는 `GoalGroupedTodos` 조립 전에 application service에서 client zone 기준 시작/종료 날짜·시간을 UTC 쿼리 조건으로 변환한다. 응답에 `scheduledOn/beginAt/endAt`이 실제로 필요하다면 요구사항과 응답 스펙 정합성 확인 후 `BasicTodoResponse` 확장 또는 별도 DTO 도입이 필요하다.

### 4.7 Application Common DTO - Stats 응답 (변경)

1. `application/application-common/src/main/java/com/modoo/application/common/dto/stats/response/TodoCompletionResponse.java`
   - 패키지: `com.modoo.application.common.dto.stats.response`
   - 변경: `date`를 UTC 집계일에서 클라이언트 타임존 기준 일자로 변환해야 하는지 정책 확정 필요.
   - 권장: 조회 기간 자체를 client date → UTC date range로 확장 조회한 뒤, 응답 `date`는 client date로 재집계한다.

### 4.8 Application Common Port (변경)

1. `application/application-common/src/main/java/com/modoo/application/common/port/todo/in/GetDailyTodosByGoalUseCase.java`
   - `get(Long loginId, Long userId, LocalDate date, String timeZone)`로 변경.

2. `application/application-common/src/main/java/com/modoo/application/common/port/todo/in/GetTimetableUseCase.java`
   - `get(Long loginId, Long userId, LocalDate date, String timeZone)`로 변경.

3. `application/application-common/src/main/java/com/modoo/application/common/port/todo/in/GetTodoDashboardUseCase.java`
   - `get(Long loginId, String timeZone)`로 변경.

4. `application/application-common/src/main/java/com/modoo/application/common/port/todo/in/TodoSearchUseCase.java`
   - request 내부 `timeZone` 사용으로 시그니처 유지 가능.

5. `application/application-common/src/main/java/com/modoo/application/common/port/todo/in/RetrieveTodoUseCase.java`
   - `findById(Long loginId, Long id, String timeZone)`로 변경.

6. `application/application-common/src/main/java/com/modoo/application/common/port/stats/in/CalculateCompletionUseCase.java`
   - `calculateWeekly(Long loginId, Long userId, LocalDate date, String timeZone)`로 변경.
   - `calculateMonthly(Long loginId, Long userId, YearMonth yearMonth, String timeZone)`로 변경.

### 4.9 Application Service - Todo 조회 (변경)

1. `application/planning-application/src/main/java/com/modoo/application/planning/todo/service/GetDailyTodosByGoalService.java`
   - 패키지: `com.modoo.application.planning.todo.service`
   - 변경: 클라이언트 날짜의 하루 시작/종료(`00:00:00` ~ `23:59:59.999999999`)를 먼저 `timeZone` 기준으로 만든 뒤 UTC 날짜/시간 범위로 변환해 조회한다.
   - `BasicTodoResponse`처럼 응답에 시간 필드가 없더라도, 쿼리 기준 날짜·시간은 application service에서 변환한 UTC 조건을 사용한다.
   - 기존 포트가 단일 `LocalDate`만 받는다면 `TodoLoaderPort` 또는 query port에 UTC 범위 조회 메서드 추가를 검토한다.
   - 응답 DTO 생성 시 UTC → client 변환 적용.

2. `application/planning-application/src/main/java/com/modoo/application/planning/todo/service/GetTimetableService.java`
   - 패키지: `com.modoo.application.planning.todo.service`
   - 변경: client date와 시간표 기준 시간 구간을 `timeZone` 기준으로 해석한 뒤 UTC 날짜/시간 범위 조건으로 조회하고, 응답 시간 그룹핑은 변환 후 client `beginAt` 기준으로 수행.

3. `application/planning-application/src/main/java/com/modoo/application/planning/todo/service/GetTodoDashboardService.java`
   - 패키지: `com.modoo.application.planning.todo.service`
   - 변경: 대시보드 기준일(`today`)을 UTC `LocalDate.now()`가 아니라 client zone의 오늘로 산정한다.
   - 대시보드 조회 시작 날짜와 종료 날짜도 client zone 기준으로 계산한 뒤 UTC 날짜/시간 범위 조건으로 변환해 query port에 전달한다.
   - 조회 범위가 날짜 경계를 넘는 타임존에서도 오늘 인덱스/콘텐츠 날짜가 client 기준이 되도록 변환 후 그룹핑.

4. `application/planning-application/src/main/java/com/modoo/application/planning/todo/service/RetrieveTodoService.java`
   - 패키지: `com.modoo.application.planning.todo.service`
   - 변경: 상세 응답 생성 시 UTC → client 변환 적용.

5. `application/planning-application/src/main/java/com/modoo/application/planning/todo/service/TodoSearchService.java`
   - 패키지: `com.modoo.application.planning.todo.service`
   - 변경: 검색에 날짜/요일 필터가 추가되거나 `SimpleTodoSearchDto.scheduledOn`을 client 기준으로 변환해야 한다면, application service에서 필터 시작 날짜·시간을 client zone → UTC 조건으로 바꾼 뒤 repository에 전달한다. 시간이 없는 projection은 변환 정확도가 낮으므로 repository가 `beginAt/endAt`도 함께 projection하도록 확장하는 방안을 우선 검토한다.

### 4.10 Application Service - Todo 저장 (변경)

1. `application/planning-application/src/main/java/com/modoo/application/planning/todo/service/CreateTodoService.java`
   - 패키지: `com.modoo.application.planning.todo.service`
   - 변경: `CreateTodoRequest.toCommand()` 결과가 UTC 변환값을 담도록 연결.
   - 리마인더 생성 시 `todo.getScheduleDatetime()`은 변환 후 UTC 값을 사용한다.

2. `application/planning-application/src/main/java/com/modoo/application/planning/todo/service/UpdateTodoService.java`
   - 패키지: `com.modoo.application.planning.todo.service`
   - 변경: `UpdateTodoRequest.toCommand()` 결과가 UTC 변환값을 담도록 연결.
   - 기존 리마인더 업데이트/취소/등록 로직은 변환 후 `todo.getScheduleDatetime()` 기준으로 유지.

3. `application/planning-application/src/main/java/com/modoo/application/planning/todo/service/MoveDateService.java`
   - 패키지: `com.modoo.application.planning.todo.service`
   - 변경: 요청 `newDate`와 기존 `beginAt/endAt`을 client zone에서 결합 후 UTC 날짜/시간으로 변환해 도메인 `moveDate`에 전달.

4. `application/planning-application/src/main/java/com/modoo/application/planning/todo/service/RepeatService.java`
   - 패키지: `com.modoo.application.planning.todo.service`
   - 변경: 요청 `repeatOn`과 원본 투두 시간/기간을 client zone 기준으로 결합 후 UTC 생성 커맨드로 변환.

5. `application/planning-application/src/main/java/com/modoo/application/planning/todo/service/PeriodSetupService.java`
   - 패키지: `com.modoo.application.planning.todo.service`
   - 변경: 기존 `scheduledOn`과 요청 `beginAt/endAt`을 client zone 기준으로 결합 후 UTC `scheduledOn/beginAt/endAt`으로 저장.

### 4.11 Application Service - Stats 조회 (변경)

1. `application/stats-application/src/main/java/com/modoo/application/stats/service/CalculateCompletionService.java`
   - 패키지: `com.modoo.application.stats.service`
   - 변경: 주간/월간 기준일은 client zone의 date/month로 해석한다.
   - client 기간의 시작/끝을 UTC date range로 확장 조회한다.
   - UTC 저장 일자별 집계를 그대로 응답하면 날짜 경계 투두가 잘못 매핑될 수 있으므로, 가능한 경우 Todo 단위 projection을 추가하거나 `TodoStatsPort`에 client date별 재집계용 메서드를 추가한다.
   - 최소 변경안: 기존 `TodoCompletionResponse`를 UTC → client date로 변환 후 동일 날짜끼리 merge한다. 단, 시간 없는 투두의 처리 정책을 함께 명시한다.

### 4.12 Domain (변경 최소화)

1. `domain/planning-domain/src/main/java/com/modoo/domain/planning/todo/dto/CreateTodoCommand.java`
   - 패키지: `com.modoo.domain.planning.todo.dto`
   - 변경: 필드 추가 없이 변환된 UTC `scheduledOn/beginAt/endAt`만 받도록 유지 가능.

2. `domain/planning-domain/src/main/java/com/modoo/domain/planning/todo/dto/UpdateTodoCommand.java`
   - 패키지: `com.modoo.domain.planning.todo.dto`
   - 변경: 필드 추가 없이 변환된 UTC `scheduledOn/beginAt/endAt`만 받도록 유지 가능.

3. `domain/planning-domain/src/main/java/com/modoo/domain/planning/todo/aggregate/Todo.java`
   - 패키지: `com.modoo.domain.planning.todo.aggregate`
   - 변경: 원칙적으로 없음. 도메인은 UTC로 정규화된 날짜/시간만 다룬다.

4. `domain/planning-domain/src/main/java/com/modoo/domain/planning/repeattodo/dto/CreateRepeatTodoCommand.java`
   - 패키지: `com.modoo.domain.planning.repeattodo.dto`
   - 변경 후보: `POST /api/todos/{id}/repeat`가 반복 투두 도메인을 경유한다면 UTC 변환값을 담도록 호출부만 조정하고 DTO 구조 변경은 피한다.

## 5) 인프라/포트 변경 후보

1. `application/application-common/src/main/java/com/modoo/application/common/port/todo/out/TodoLoaderPort.java`
   - 변경 후보: client 하루가 UTC 기준 2일에 걸치는 경우를 위해 날짜 범위 조회 메서드 추가.
   - 예: `List<Todo> getDailyTodosBetween(Long userId, LocalDate fromUtcDate, LocalDate toUtcDate)`.

2. `infra/planning-infra-mysql/src/main/java/com/modoo/infra/mysql/planning/todo/adapter/TodoPersistenceAdapter.java`
   - 변경 후보: 신규 범위 조회 포트 구현.

3. `infra/planning-infra-mysql/src/main/java/com/modoo/infra/mysql/planning/todo/repository/TodoQueryRepository.java`
   - 변경 후보: 검색/일별/시간표/대시보드에서 필요한 `scheduledOn`, `beginAt`, `endAt` projection 보강.

4. `infra/planning-infra-mysql/src/main/java/com/modoo/infra/mysql/planning/todo/repository/TodoQueryRepositoryImpl.java`
   - 변경 후보: UTC 날짜 범위 조건, 검색 projection 시간 필드 추가, stats 재집계용 query 추가.

5. `application/application-common/src/main/java/com/modoo/application/common/port/stats/out/TodoStatsPort.java`
   - 변경 후보: 완료도 통계를 UTC 일자 집계가 아니라 투두 단위 또는 client date 집계로 계산할 수 있는 메서드 추가.

## 6) 테스트 전략

### 6.1 Domain 테스트

- 도메인 자체는 UTC 정규화 이후의 값만 받도록 유지하므로 신규 테스트는 최소화한다.
- 변환 결과로 날짜가 바뀐 command가 들어왔을 때 기존 도메인 검증이 유지되는지 회귀 테스트를 추가할 수 있다.
- 대상 후보
  - `domain/planning-domain/src/test/java/com/modoo/domain/planning/todo/aggregate/TodoTest.java`
  - `domain/planning-domain/src/test/java/com/modoo/domain/planning/todo/service/TodoDomainServiceTest.java`
  - `domain/planning-domain/src/testFixtures/java/com/modoo/fixture/TodoFixture.java`

### 6.2 Application 테스트 - 저장 방향

- `application/planning-application/src/test/java/com/modoo/application/planning/todo/service/CreateTodoServiceTest.java`
  - `Asia/Seoul` 요청이 UTC 저장값으로 변환되는 성공 케이스.
  - `timeZone` 누락 시 UTC 기준 저장 케이스.
  - 시간이 없는 투두는 scheduledOn 변환을 skip하는 케이스.

- `application/planning-application/src/test/java/com/modoo/application/planning/todo/service/UpdateTodoServiceTest.java`
  - 수정 시 UTC 변환 및 리마인더 재설정 기준 시간 검증.
  - `timeZone` 누락 시 UTC 기준.

- `application/planning-application/src/test/java/com/modoo/application/planning/todo/service/TodoPeriodSetupServiceTest.java`
  - 기간 설정 시 기존 scheduledOn + 요청 begin/end가 client → UTC로 변환되는지 검증.

- `application/planning-application/src/test/java/com/modoo/application/planning/todo/service/RepeatServiceTest.java`
  - 다른 날 반복하기 요청 날짜가 client → UTC로 변환되어 생성되는지 검증.

- `MoveDateService` 테스트 파일이 없다면 `application/planning-application/src/test/java/com/modoo/application/planning/todo/service/MoveDateServiceTest.java` 신규 추가.
  - `PUT /api/todos/{id}/date`의 `newDate`가 기존 시간과 결합되어 UTC 저장 날짜로 변환되는지 검증.

### 6.3 Application 테스트 - 조회 방향

- `application/planning-application/src/test/java/com/modoo/application/planning/todo/service/GetDailyTodosByGoalServiceTest.java`
  - UTC 저장 투두가 `Asia/Seoul` 기준 요청 날짜에 포함되고 응답 날짜/시간이 변환되는지 검증.
  - `timeZone` 누락 시 UTC 기준.

- `application/planning-application/src/test/java/com/modoo/application/planning/todo/service/GetDailyTodosByTimeServiceTest.java`
  - 시간표 그룹핑이 UTC 시간이 아니라 client 변환 후 시간 기준으로 수행되는지 검증.

- `application/planning-application/src/test/java/com/modoo/application/planning/todo/service/RetrieveTodoServiceTest.java`
  - 상세 응답의 scheduledOn/beginAt/endAt 변환 검증.

- `application/planning-application/src/test/java/com/modoo/application/planning/todo/service/TodoSearchServiceTest.java`
  - 검색 응답의 scheduledOn 변환 검증.

- `application/planning-application/src/test/java/com/modoo/application/planning/todo/service/GetTodoDashboardServiceTest.java`
  - client zone 기준 오늘/인덱스/응답 날짜 변환 검증.

### 6.4 Application 테스트 - Stats

- `application/stats-application/src/test/java/com/modoo/application/stats/service/CalculateCompletionServiceTest.java`
  - 주간 완료도: client 기준 주간 경계와 UTC 날짜 경계가 다른 케이스.
  - 월간 완료도: client 기준 월 경계와 UTC 날짜 경계가 다른 케이스.
  - `timeZone` 누락 시 UTC 기준.
  - completed/uncompleted/total 재집계 정합성.

### 6.5 테스트 작성 규칙

- 테스트 메서드명은 한국어로 작성한다.
- 각 테스트에 `//given`, `//when`, `//then`을 명시한다.
- 실패 케이스는 `ThrowingCallable` 람다를 `//when`에 선언하고 `//then`에서 검증한다.
- application service 테스트는 `@SpringBootTest`, `@Transactional`, `@BeforeEach` 데이터 준비 방식을 유지한다.
- 신규 DDL이 없으므로 `flywayMigrate`는 원칙적으로 생략 가능하지만, 통합 테스트 DB 초기 상태가 필요하면 `./gradlew :bootstrap:bootstrap-gateway:flywayMigrate` 후 대상 테스트를 실행한다.

## 7) 권장 작업 순서

1. `TimeZoneConverter`와 조회 조건용 `DateTimeRange`를 common에 추가하고 변환 정책 단위 테스트를 고정한다. 응답 필드 묶음용 공통 record는 만들지 않는다.
2. Todo 요청 DTO(`CreateTodoRequest`, `UpdateTodoRequest`, `MoveDateRequest`, `RepeatAnotherDayRequest`, `PeriodSetupRequest`, `TodoSearchRequest`)에 `timeZone`을 추가한다.
3. 저장 방향 서비스(`CreateTodoService`, `UpdateTodoService`, `MoveDateService`, `RepeatService`, `PeriodSetupService`)부터 client → UTC 변환을 적용한다.
4. Todo 조회 usecase/port 시그니처를 확장하고, 응답 DTO 팩토리에 UTC → client 변환 overload를 추가한다.
5. 일별/요일/시간표/대시보드 조회 조건의 시작 날짜와 시간을 application service에서 client zone 기준으로 해석한 뒤 UTC 범위 조회로 보강한다.
6. 검색 projection이 날짜만 보유하는 문제를 해결하기 위해 `SimpleTodoSearchDto` 또는 검색 projection에 시간 필드를 추가한다.
7. Stats 완료도 조회에 `timeZone`을 추가하고, client date 기준 집계/merge 정책을 구현한다.
8. ControllerDoc과 bootstrap-common error example을 갱신한다.
9. application 테스트를 API 그룹별로 보강하고 회귀 테스트를 실행한다.

## 8) 리스크 및 체크포인트

- `LocalDate.now()`와 `YearMonth.now()`가 서버 UTC 기준으로 호출되는 기존 흐름은 client zone 기준으로 대체해야 한다.
- client 하루는 UTC 기준으로 1~2개의 날짜에 걸칠 수 있어, 기존 단일 날짜 조회 조건은 누락을 만들 수 있다. 따라서 repository 호출 전에 application service에서 쿼리 시작 날짜·시간과 종료 날짜·시간을 client zone → UTC로 변환해야 한다.
- 완료도 통계는 UTC 일자별 집계 결과를 단순 변환하면 날짜 경계 투두의 completed/uncompleted count가 섞일 수 있으므로, client date 기준 재집계 방식을 우선 검토한다.
- 시간이 없는 투두는 변환 skip 정책 때문에 client 날짜 범위 조회와 응답 필터링에서 별도 처리해야 한다.
- `Asia/Seoul`처럼 DST가 없는 타임존뿐 아니라 `America/Los_Angeles`처럼 DST가 있는 타임존도 테스트에 포함하면 `ZoneId` 변환 안정성을 확인할 수 있다.
- OpenAPI 문서와 실제 controller 파라미터명이 불일치하지 않도록 마지막에 `/v3/api-docs` 또는 springdoc 생성 결과를 확인한다.
