# 목표 상세조회 API repeat type 누락 수정 구현 플랜

## 1) 목표

- `GET /api/goals/{id}` 응답의 `repeatDdudus` 각 항목에 `repeatType` 필드를 포함한다.
- 기존 목표 상세조회의 권한/예외/조회 흐름은 유지하고, 응답 스펙만 보강한다.
- Swagger 스키마와 실제 직렬화 응답이 동일하도록 맞춘다.

## 2) 영향 범위 (신규/변경 대상 패키지 및 클래스)

### 2.1 변경 패키지

- `application/application-common/src/main/java/com/ddudu/application/common/dto/repeatddudu`
- `application/planning-application/src/test/java/com/ddudu/application/planning/goal/service`

### 2.2 변경 클래스

- `RepeatDduduDto`
  - `repeatType` 필드(및 스키마 설명) 추가
  - `from(RepeatDdudu)` 매핑에 `repeatDdudu.getRepeatType()` 반영
- `RetrieveGoalServiceTest`
  - 목표 상세조회 응답 검증에 `repeatType` 포함 단언 추가

### 2.3 검토 대상(코드 변경 가능성 낮음)

- `GoalWithRepeatDduduResponse`
  - `repeatDdudus` 매핑이 `RepeatDduduDto::from`을 사용하므로, DTO 변경만으로 반영되는지 확인
- `GoalControllerDoc`
  - `GoalWithRepeatDduduResponse` 반환 스키마가 `RepeatDduduDto`를 참조하므로 문서가 자동 반영되는지 확인

## 3) 구현 상세

### 3.1 DTO 확장

1. `RepeatDduduDto`에 `RepeatType repeatType` 필드 추가
2. `@Schema`에 허용 값(`DAILY|WEEKLY|MONTHLY`) 및 예시 정의
3. 레코드 생성자 순서와 `from(RepeatDdudu)` 매핑 순서를 동일하게 유지

### 3.2 테스트 보강

1. `RetrieveGoalServiceTest`의 "목표_조회_시_해당_목표의_반복_뚜두도_함께_조회된다" 케이스에서
   - 응답 `repeatDdudus().get(0).repeatType()`이 저장한 `RepeatDdudu.getRepeatType()`과 일치하는지 검증
2. 기존 `//given`, `//when`, `//then` 구조는 그대로 유지

## 4) 검증 계획

1. `RetrieveGoalServiceTest` 실행으로 목표 상세조회 응답 필드 포함 여부 검증
2. 필요 시 목표 API 모듈 컴파일로 DTO 변경 전파 확인

## 5) 리스크 및 체크포인트

- 프론트/클라이언트가 응답 필드 순서에 의존한다면 레코드 필드 추가에 따른 역직렬화 영향이 없는지 확인 필요
- OpenAPI 스키마 생성 시 enum 표기 형식이 기존 컨벤션과 다를 수 있으므로 `/v3/api-docs`에서 확인 권장
