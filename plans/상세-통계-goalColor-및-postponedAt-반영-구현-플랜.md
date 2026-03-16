# 상세 통계 goalColor 추가 및 postponedAt 기준 집계 반영 구현 플랜

## 1) 구현 목표

- `GET /api/stats/detail/{goalId}/achieved`, `GET /api/stats/detail/{goalId}/postponed` 응답에 `goalColor`를 추가한다.
- 미루기 중심 상세 통계(`postponed`)의 집계 기준 날짜를 `scheduledOn`이 아니라 `postponedAt`(날짜)로 반영한다.
- 기존 월 범위(fromMonth ~ toMonth) 및 예외 처리 규칙은 유지한다.

## 2) 영향 범위 (패키지/클래스)

### A. API 응답 스키마 확장 (goalColor)

- **변경 패키지**
  - `application/application-common/src/main/java/com/ddudu/application/common/dto/stats/response`
- **변경 클래스**
  - `AchievedStatsDetailResponse` (필드 `goalColor` 추가)
  - `PostponedStatsDetailResponse` (필드 `goalColor` 추가)

### B. 상세 통계 유즈케이스 응답 조립

- **변경 패키지**
  - `application/stats-application/src/main/java/com/ddudu/application/stats/service`
- **변경 클래스**
  - `CollectMonthlyStatsDetailService`
    - `collectAchievedDetail()` 응답 빌더에 `goalColor(goal.getColor())` 추가
    - `collectPostponedDetail()` 응답 빌더에 `goalColor(goal.getColor())` 추가

### C. 미루기 상세 통계 집계 기준(postponedAt) 반영

- **변경 패키지**
  - `infra/planning-infra-mysql/src/main/java/com/ddudu/infra/mysql/planning/ddudu/repository`
- **변경 클래스**
  - `DduduQueryRepositoryImpl`
    - `findStatsBaseOfUser(...)`에서 미루기 상세 통계를 위해 사용할 조건 분기 추가
    - `findDdudusCompletion(...)`의 미루기(`isAchieved=false`) 경로에서 날짜 조건을 `postponedAt` 기준(일 단위 범위)으로 반영

> 구현 방식은 아래 3안 중 택1
>
> 1. `findStatsBaseOfUser(...)` 시그니처 확장(예: `boolean usePostponedAt`) 후 쿼리 분기
> 2. 미루기 전용 신규 메서드 추가(예: `findPostponedStatsBaseOfUser(...)`)
> 3. `MonthlyStatsPort`/`DduduStatsPort`에 미루기 전용 포트 메서드 추가 후 어댑터에서 분리
>
> 유지보수성과 영향도 기준으로 **2번(전용 메서드 분리)**를 우선 권장.

### D. 포트(인터페이스) 계층 (선택한 구현안에 따라)

- **변경 가능 패키지**
  - `application/application-common/src/main/java/com/ddudu/application/common/port/stats/out`
- **변경 가능 클래스**
  - `MonthlyStatsPort`
  - `DduduStatsPort`

## 3) 단계별 구현 계획

1. **응답 DTO 확장**
   - 상세 응답 레코드 2종에 `goalColor` 필드 추가.
   - JSON 직렬화 필드명은 `goalColor`로 유지.

2. **유즈케이스 서비스 반영**
   - `CollectMonthlyStatsDetailService`에서 `Goal` 조회 결과의 색상(`goal.getColor()`)을 응답에 주입.

3. **postponedAt 기준 집계 로직 분리/적용**
   - 미루기 통계 경로에 한해 날짜 필터를 `postponedAt` 기준으로 변경.
   - 달성 통계 경로는 기존 `scheduledOn` 기준 유지.

4. **회귀 영향 검증**
   - 달성/미루기 상세 통계 기존 지표(총계, 요일 통계, 캘린더 응답 여부)가 유지되는지 확인.

## 4) 테스트 계획 (요구사항 반영)

### A. 대상 테스트 클래스

- **변경 패키지**
  - `application/stats-application/src/test/java/com/ddudu/application/stats/service`
- **변경 클래스**
  - `CollectMonthlyStatsDetailServiceTest`

### B. 테스트 케이스 추가/수정

1. 달성 상세 응답에 `goalColor` 포함 검증
2. 미루기 상세 응답에 `goalColor` 포함 검증
3. 미루기 상세 통계가 `postponedAt` 날짜 기준으로 집계됨을 검증
   - `scheduledOn`은 범위 밖이지만 `postponedAt`은 범위 안인 데이터 포함 케이스
   - `scheduledOn`은 범위 안이지만 `postponedAt`은 범위 밖인 데이터 제외 케이스

### C. 테스트 작성 규칙 적용

- `@SpringBootTest`, `@Transactional`, `@BeforeEach` 기반으로 입력 데이터 준비
- `//given`, `//when`, `//then` 주석 명시
- 실패 케이스는 `ThrowingCallable` 사용
- 테스트 메서드명은 한글 사용

## 5) 리스크 및 확인 포인트

- `toMonth > fromMonth`일 때 현재 캘린더 응답 정책(`isExists=false`)이 유지되는지 확인
- `postponedAt`가 null인 데이터의 제외 조건이 명확한지 확인
- DB 인덱스(필요 시 `postponed_at`) 검토
- API 문서 스키마(OpenAPI) 자동 반영 여부 확인

## 6) 작업 완료 정의(DoD)

- 상세 통계 응답 2종에 `goalColor`가 정상 노출된다.
- 미루기 상세 통계 집계 기준이 `postponedAt`로 변경된다.
- 관련 테스트가 통과하며 기존 상세 통계 기능 회귀가 없다.
