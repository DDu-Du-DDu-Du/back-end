# User Option 확장 구현 플랜

## 1) 목표

- 로그인 시 `User` 생성/복원 과정에서 `option`이 `null`이면, 기존 기본 옵션 + 신규 기본 옵션을 함께 채워 저장한다.
- 외부 로그인 API의 요청/응답 스펙은 변경하지 않는다.
- `User` 하위 `Options`를 JSON Object 구조와 유사한 계층형 VO로 확장한다.

## 2) 신규/영향 클래스 및 패키지 위치

### 영향 클래스

- `domain/user-domain/src/main/java/com/ddudu/domain/user/user/aggregate/User.java`
  - `options == null` 시 기본값 생성 로직 확장
  - 기존 `buildOptions(...)` 시그니처/생성 흐름 조정
- `domain/user-domain/src/main/java/com/ddudu/domain/user/user/aggregate/vo/Options.java`
  - 기존 평면 필드 + 신규 하위 VO 필드 포함하도록 구조 확장
- `domain/user-domain/src/test/java/com/ddudu/domain/user/user/aggregate/UserTest.java`
  - `option null`인 경우의 초기값 검증 강화
- `domain/user-domain/src/test/java/com/ddudu/domain/user/user/aggregate/vo/OptionsTest.java`
  - 확장된 기본값/하위 VO 검증 추가
- `domain/user-domain/src/testFixtures/java/com/ddudu/fixture/UserFixture.java`
  - 신규 옵션 기본값과 부분 변경 케이스를 쉽게 생성하도록 fixture 메서드 추가

### 신규 클래스 (VO)

- 패키지: `com.ddudu.domain.user.user.aggregate.vo`
  - `DisplayOptions`
  - `MenuActivationOptions`
  - `MenuActivationItem`
  - `AppConnectionOptions`
  - `RealtimeSyncOptions`
- 패키지: `com.ddudu.domain.user.user.aggregate.enums`
  - `WeekStartDay` (`SUN`, `MON`)

> 참고: VO는 `Options` 내부 정적 클래스로 둘 수도 있으나, 테스트 가독성과 재사용성을 위해 독립 클래스 분리를 우선 권장.

## 3) 도메인 모델 설계안

- `Options`
  - 기존: `allowingFollowsAfterApproval`, `templateNotification`, `dduduNotification`
  - 신규:
    - `DisplayOptions display`
    - `MenuActivationOptions menuActivation`
    - `AppConnectionOptions appConnection`
- `DisplayOptions`
  - `WeekStartDay weekStartDay` (기본값 `SUN`)
  - `boolean darkMode` (기본값 `false`)
- `MenuActivationOptions`
  - `MenuActivationItem calendar` (기본값 `isActive=true`, `priority=1`)
  - `MenuActivationItem dashboard` (기본값 `isActive=true`, `priority=2`)
  - `MenuActivationItem stats` (기본값 `isActive=true`, `priority=3`)
- `AppConnectionOptions`
  - `RealtimeSyncOptions realtimeSync`
- `RealtimeSyncOptions`
  - `boolean notion` (기본값 `false`)
  - `boolean googleCalendar` (기본값 `false`)
  - `boolean microsoftTodo` (기본값 `false`)

## 4) 구현 단계

1. `WeekStartDay` enum 및 신규 VO 클래스 생성
   - 각 VO에 빌더/생성자 기본값 주입 (`Objects.requireNonNullElse` 패턴 유지)
2. `Options` 확장
   - 신규 VO 필드 추가
   - `builder` 입력이 `null`인 경우 신규 기본값 자동 세팅
3. `User` 기본 옵션 생성 경로 갱신
   - `options == null`이면 확장된 `Options` 기본값을 생성
   - 기존 로그인 흐름 호출부 변경 없이 동작하도록 `User.builder()` 구성 유지
4. 테스트 fixture 확장
   - `createValidUser()`는 확장 옵션 기본값을 내장
   - 타깃 필드만 주입 가능한 메서드 추가 (예: `createUserWithWeekStartDay(...)`)
5. 단위 테스트 추가/보강
   - `OptionsTest`: 기본값/개별 VO 기본값/priority 순서 검증
   - `UserTest`: `options null` 입력 시 전체 기본값 구성 검증

## 5) 테스트 플랜

- 대상 모듈
  - `domain/user-domain`
- 권장 케이스
  - 성공
    - `Options` 생성 시 기본값이 스펙과 일치
    - 각 신규 VO 생성 시 기본값/명시값 적용 정상
    - `User` 생성 시 `options == null`이면 확장 기본 옵션 자동 구성
  - 실패
    - 현재 스펙상 별도 실패 케이스 없음 (필요 시 priority 음수 같은 유효성 규칙 도입 후 추가)

## 6) 리스크 및 확인 포인트

- 직렬화/역직렬화 경로(인프라)에서 `Options` 구조 변경 영향 확인 필요
- 기존 DB JSON 컬럼을 사용 중이라면, 누락 필드는 기본값으로 보정되도록 매핑 전략 점검
- API 계약 무변경 요구사항 확인
  - 로그인 요청/응답 DTO 변경 금지
  - 내부 저장값만 확장
