# 뚜두 생성/수정 Full Context 핸들링 구현 플랜

## 1) 목표

- `POST /api/ddudus` 생성 API에 `beginAt`, `endAt`, `remindDays`, `remindHours`, `remindMinutes`를 반영한다.
- 기존 제목 수정 API는 `PUT /api/ddudus/{id}/name`으로 엔드포인트를 변경하고, full context 수정용 `PUT /api/ddudus/{id}` API를 신규로 추가한다.
- `Ddudu` 도메인에서 시간/미리알림 검증 및 `remindAt` 계산을 일관되게 처리하고, 수정 시 이벤트 발행 정책(기존 취소 + 신규 등록)을 적용한다.

## 2) 신규/영향 클래스 및 패키지 위치

### 2.1 Bootstrap (영향)

- `bootstrap/planning-api/src/main/java/com/ddudu/api/planning/ddudu/controller/DduduController.java`
  - 신규 full context 수정용 `PUT /api/ddudus/{id}` 엔드포인트 추가
  - 기존 제목 수정 API를 `PUT /api/ddudus/{id}/name`으로 변경
- `bootstrap/planning-api/src/main/java/com/ddudu/api/planning/ddudu/doc/DduduControllerDoc.java`
  - 생성/수정 요청 스키마 변경 반영
  - 신규 수정 API 문서(`@Operation`, `@ApiResponse`, `@ExampleObject`) 추가
  - 제목 수정 API 문서 endpoint를 `/api/ddudus/{id}/name` 기준으로 갱신

### 2.2 Application Common DTO/Port (신규 + 영향)

- `application/application-common/src/main/java/com/ddudu/application/common/dto/ddudu/request/CreateDduduRequest.java` (영향)
  - full context 필드 추가 및 `toCommand()` 확장
- `application/application-common/src/main/java/com/ddudu/application/common/dto/ddudu/request/UpdateDduduRequest.java` (신규)
  - 패키지: `com.ddudu.application.common.dto.ddudu.request`
  - 수정 요청 필드(`goalId`, `name`, `scheduledOn`, `beginAt`, `endAt`, `remindDays`, `remindHours`, `remindMinutes`) 정의
- `application/application-common/src/main/java/com/ddudu/application/common/port/ddudu/in/UpdateDduduUseCase.java` (신규)
  - 패키지: `com.ddudu.application.common.port.ddudu.in`
- `application/application-common/src/main/java/com/ddudu/application/common/port/ddudu/in/ChangeNameUseCase.java` (영향)
  - 유지(기존 제목 수정 API 전용)

### 2.3 Application Service (신규 + 영향)

- `application/planning-application/src/main/java/com/ddudu/application/planning/ddudu/service/CreateDduduService.java` (영향)
  - 생성 커맨드 확장 적용
  - 미리알림 생성 시 `InterimSetReminderEvent` 발행 로직 추가
- `application/planning-application/src/main/java/com/ddudu/application/planning/ddudu/service/UpdateDduduService.java` (신규)
  - 패키지: `com.ddudu.application.planning.ddudu.service`
  - 로그인 사용자/뚜두/목표 검증 후 도메인 `update(...)` 호출
  - 수정 전 remindAt 유무 기준 `InterimCancelReminderEvent` + `InterimSetReminderEvent` 발행
- `application/planning-application/src/main/java/com/ddudu/application/planning/ddudu/service/ChangeNameService.java` (영향)
  - 유지(기존 제목 수정 API 전용)

### 2.4 Domain (신규 + 영향)

- `domain/planning-domain/src/main/java/com/ddudu/domain/planning/ddudu/aggregate/Ddudu.java` (영향)
  - 생성자 검증 범위 확장: remind 입력 기반 검증 조건 반영
  - 기존 `changeName(String)`은 유지하고, full context 수정용 `update(...)`를 신규 추가
  - `remindAt`이 존재하더라도 수정 인자(remindDays/hours/minutes) 중 하나라도 들어오면 재검증/재계산
- `domain/planning-domain/src/main/java/com/ddudu/domain/planning/ddudu/dto/CreateDduduCommand.java` (영향)
  - full context 필드 추가
- `domain/planning-domain/src/main/java/com/ddudu/domain/planning/ddudu/dto/UpdateDduduCommand.java` (신규)
  - 패키지: `com.ddudu.domain.planning.ddudu.dto`
- `domain/planning-domain/src/main/java/com/ddudu/domain/planning/ddudu/service/DduduDomainService.java` (영향)
  - `create(...)` 확장
  - `update(...)` 도메인 오케스트레이션 메서드 추가

### 2.5 Test (영향 + 신규)

- `domain/planning-domain/src/testFixtures/java/com/ddudu/fixture/DduduFixture.java` (영향)
  - `createValidDdudu()` 등 happy-path 픽스처 추가/보강
  - `createDduduWithBeginAt(...)`, `createDduduWithReminderDays(...)` 등 타깃 필드 기반 메서드 추가
- `domain/planning-domain/src/test/java/com/ddudu/domain/planning/ddudu/aggregate/DduduTest.java` (영향)
  - `update(...)` 성공/실패 케이스 추가
- `domain/planning-domain/src/test/java/com/ddudu/domain/planning/ddudu/service/DduduDomainServiceTest.java` (영향)
  - create/update 커맨드 변환 검증 추가
- `application/planning-application/src/test/java/com/ddudu/application/planning/ddudu/service/CreateDduduServiceTest.java` (영향)
  - full context 생성 시나리오 및 이벤트 발행 검증
- `application/planning-application/src/test/java/com/ddudu/application/planning/ddudu/service/UpdateDduduServiceTest.java` (신규)
  - 수정 성공/실패 및 이벤트 발행 정책 검증
- `application/planning-application/src/test/java/com/ddudu/application/planning/ddudu/service/ChangeNameServiceTest.java` (영향)
  - 엔드포인트 변경(`/api/ddudus/{id}/name`)에 맞는 계약 유지 검증

## 3) 상세 구현 단계

1. **Request/Command 확장**
   - `CreateDduduRequest`, `CreateDduduCommand`에 full context 필드 추가
   - `UpdateDduduRequest`, `UpdateDduduCommand` 신규 생성
2. **도메인 모델 확장**
   - `Ddudu` 생성자/`update(...)`에 검증 분기 반영
   - remind 관련 규칙
     - 세 입력이 모두 없으면 검증 스킵
     - 하나라도 들어오면 음수/시작시간/과거시간 검증 수행
     - 기존 remindAt 존재 여부와 무관하게 새 입력이 있으면 재설정
3. **애플리케이션 유스케이스 연결**
   - `CreateDduduService`에서 확장 커맨드 사용 및 set 이벤트 발행
   - `UpdateDduduService` 신규 구현
     - 사용자/뚜두/목표/권한 검증
     - 수정 전후 remindAt 비교로 cancel/set 이벤트 발행
4. **부트스트랩 API 확장**
   - 컨트롤러에 신규 update 엔드포인트(`PUT /api/ddudus/{id}`) 추가
   - 기존 제목 수정 엔드포인트를 `PUT /api/ddudus/{id}/name`으로 변경
   - 문서 인터페이스에 생성/수정 스펙 및 에러 예시 반영
5. **기존 changeName 경로 유지 정리**
   - `ChangeNameUseCase/Service/DTO`는 유지
   - 라우팅/문서/테스트만 신규 endpoint 기준으로 정합성 반영

## 4) 테스트 계획

### 4.1 Domain (`domain/planning-domain`)

- 성공
  - full context 생성 성공
  - begin/end 조합별 생성/수정 성공
  - remindDays/hours/minutes 조합별 생성/수정 성공
- 실패
  - 시작시간 > 종료시간
  - remind 입력 음수
  - remind 입력 존재 + beginAt 없음
  - 계산된 remindAt이 현재 이전

### 4.2 Application (`application/planning-application`)

- 생성 실패
  - 로그인 사용자 없음(404)
- 수정 실패
  - 로그인 사용자 없음(404)
  - 뚜두 없음(404)
  - 사용자 불일치(403)
- 이벤트
  - 기존 remindAt 존재 시 cancel 이벤트 발행
  - 신규/재설정 remindAt 존재 시 set 이벤트 발행

### 4.3 테스트 규칙 반영

- 테스트 메서드명은 한국어로 작성
- 실패 케이스 `//when`은 `ThrowingCallable` 사용
- `//given`, `//when`, `//then` 주석 명시
- 픽스처는 `BaseFixtures` 확장 기반 랜덤 입력 사용

## 5) 작업 순서(권장)

1. DTO/Command/UseCase 인터페이스 정리
2. 도메인 `Ddudu` + `DduduDomainService` 확장
3. `CreateDduduService` 수정 및 `UpdateDduduService` 추가
4. 컨트롤러/문서 반영
5. domain/application 테스트 보강 및 회귀 검증

## 6) 리스크 및 체크포인트

- 제목 수정 API endpoint 변경(`/api/ddudus/{id}` → `/api/ddudus/{id}/name`)에 대한 클라이언트 공지/배포 순서 점검 필요
- remind 재설정 정책(기존 remindAt 유지 vs 덮어쓰기) 경계조건 점검 필요
- `LocalDateTime.now()` 기반 검증으로 테스트가 flaky하지 않도록 픽스처 시간 오프셋 안전하게 확보 필요
- 이벤트 중복 발행 방지를 위해 수정 전후 상태 비교 로직을 명확히 유지
