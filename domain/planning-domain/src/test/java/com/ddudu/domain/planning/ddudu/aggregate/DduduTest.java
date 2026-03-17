package com.ddudu.domain.planning.ddudu.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import com.ddudu.common.exception.DduduErrorCode;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu.DduduBuilder;
import com.ddudu.domain.planning.ddudu.aggregate.enums.DduduStatus;
import com.ddudu.fixture.DduduFixture;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(ReplaceUnderscores.class)
class DduduTest {

  Long goalId;
  Long userId;

  @BeforeEach
  void setUp() {
    goalId = DduduFixture.getRandomId();
    userId = DduduFixture.getRandomId();
  }

  @Nested
  class 생성_테스트 {

    String name;

    @BeforeEach
    void setUp() {
      name = DduduFixture.getRandomSentenceWithMax(50);
    }

    @Test
    void 뚜두_생성을_성공한다() {
      // given

      // when
      Ddudu ddudu = Ddudu.builder()
          .goalId(goalId)
          .userId(userId)
          .name(name)
          .status(DduduStatus.COMPLETE)
          .isPostponed(true)
          .build();

      // then
      assertThat(ddudu).isNotNull();
    }

    @Test
    void 뚜두_생성_시_디폴트_값이_적용된다() {
      // given

      // when
      Ddudu ddudu = Ddudu.builder()
          .goalId(goalId)
          .userId(userId)
          .name(name)
          .build();

      // then
      assertThat(ddudu.getStatus()).isEqualTo(DduduStatus.UNCOMPLETED);
      assertThat(ddudu.isPostponed()).isFalse();
      assertThat(ddudu.getScheduledOn()).isEqualTo(LocalDate.now());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void 이름이_빈_값이면_생성을_실패한다(String blankName) {
      // given
      DduduBuilder builder = Ddudu.builder()
          .goalId(goalId)
          .userId(userId)
          .name(blankName);

      // when
      ThrowingCallable create = builder::build;

      // then
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(create)
          .withMessage(DduduErrorCode.BLANK_NAME.getCodeName());
    }

    @Test
    void 이름이_50자를_넘으면_생성을_실패한다() {
      // given
      String over50 = DduduFixture.getRandomSentence(51, 100);
      DduduBuilder builder = Ddudu.builder()
          .goalId(goalId)
          .userId(userId)
          .name(over50);

      // when
      ThrowingCallable create = builder::build;

      // then
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(create)
          .withMessage(DduduErrorCode.EXCESSIVE_NAME_LENGTH.getCodeName());
    }

    @Test
    void 메모가_2000자_이하면_생성을_성공한다() {
      // given
      String validMemo = DduduFixture.createValidMemo();

      // when
      Ddudu ddudu = Ddudu.builder()
          .goalId(goalId)
          .userId(userId)
          .name(name)
          .memo(validMemo)
          .build();

      // then
      assertThat(ddudu.getMemo()).isEqualTo(validMemo);
    }

    @Test
    void 메모가_2000자를_넘으면_생성을_실패한다() {
      // given
      String overLengthMemo = DduduFixture.createOverLengthMemo();
      DduduBuilder builder = Ddudu.builder()
          .goalId(goalId)
          .userId(userId)
          .name(name)
          .memo(overLengthMemo);

      // when
      ThrowingCallable create = builder::build;

      // then
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(create)
          .withMessage(DduduErrorCode.EXCESSIVE_MEMO_LENGTH.getCodeName());
    }

    @Test
    void 목표가_없으면_생성을_실패한다() {
      // given
      DduduBuilder builder = Ddudu.builder()
          .userId(userId)
          .name(name);

      // when
      ThrowingCallable create = builder::build;

      // then
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(create)
          .withMessage(DduduErrorCode.NULL_GOAL_VALUE.getCodeName());
    }

    @Test
    void 사용자가_없으면_생성을_실패한다() {
      // given
      DduduBuilder builder = Ddudu.builder()
          .goalId(goalId)
          .name(name);

      // when
      ThrowingCallable create = builder::build;

      // then
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(create)
          .withMessage(DduduErrorCode.NULL_USER.getCodeName());
    }

    @Test
    void 시작_시간이_종료_시간보다_뒤면_생성을_실패한다() {
      // given
      DduduBuilder builder = Ddudu.builder()
          .goalId(goalId)
          .userId(userId)
          .name(name)
          .beginAt(LocalTime.now()
              .plusMinutes(1))
          .endAt(LocalTime.now());

      // when
      ThrowingCallable create = builder::build;

      // then
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(create)
          .withMessage(DduduErrorCode.UNABLE_TO_FINISH_BEFORE_BEGIN.getCodeName());
    }

  }

  @Nested
  class 기능_테스트 {

    Long userId;
    Long goalId;
    Ddudu ddudu;

    @BeforeEach
    void setUp() {
      userId = DduduFixture.getRandomId();
      goalId = DduduFixture.getRandomId();
      ddudu = DduduFixture.createRandomDduduWithReference(goalId, userId, false, null);
    }

    @Nested
    class 미리알림_설정_테스트 {

      Ddudu futureDdudu;
      LocalTime beginAt;
      int days;
      int hours;
      int minutes;

      @BeforeEach
      void setUp() {
        LocalDate futureDate = DduduFixture.getFutureDate(10);
        beginAt = DduduFixture.getFutureTime();
        Ddudu beforeTimeSet = DduduFixture.createRandomDduduWithSchedule(
            userId,
            goalId,
            futureDate
        );
        futureDdudu = beforeTimeSet.setUpPeriod(beginAt, null);
        int dayDifference = (int) ChronoUnit.DAYS.between(LocalDate.now(), futureDate);
        int hourDifference = (int) ChronoUnit.HOURS.between(LocalTime.now(), beginAt);
        days = DduduFixture.getRandomInt(0, Math.max(dayDifference - 1, 0));
        hours = DduduFixture.getRandomInt(0, Math.max(hourDifference - 1, 0));
        minutes = DduduFixture.getRandomInt(1, 59);
      }

      @Test
      void 시작_시간이_있는_뚜두에_미리알림을_설정한다() {
        // given

        // when
        Ddudu updated = futureDdudu.setReminder(days, hours, minutes);

        // then
        LocalDateTime actual = updated.getRemindAt();
        LocalDateTime expected = futureDdudu.getScheduledOn()
            .atTime(beginAt)
            .minusDays(days)
            .minusHours(hours)
            .minusMinutes(minutes);

        assertThat(actual).isEqualTo(expected);
      }

      @Test
      void 미리알림_입력값이_음수면_미리알림_설정을_실패한다() {
        // given
        int negativeDays = DduduFixture.getRandomNegative();
        int negativeHours = DduduFixture.getRandomNegative();
        int negativeMins = DduduFixture.getRandomNegative();

        // when
        ThrowingCallable setReminder = () -> futureDdudu.setReminder(
            negativeDays,
            negativeHours,
            negativeMins
        );

        // then
        Assertions.assertThatIllegalArgumentException()
            .isThrownBy(setReminder)
            .withMessage(DduduErrorCode.NEGATIVE_REMINDER_INPUT_EXISTS.getCodeName());
      }

      @Test
      void 시작_시간이_없으면_미리알림_설정을_실패한다() {
        // given
        Ddudu noTime = DduduFixture.createRandomDduduWithSchedule(userId, goalId, LocalDate.now());

        // when
        ThrowingCallable setReminder = () -> noTime.setReminder(0, 1, 0);

        // then
        Assertions.assertThatIllegalArgumentException()
            .isThrownBy(setReminder)
            .withMessage(DduduErrorCode.BEGIN_AT_REQUIRED_FOR_REMINDER.getCodeName());
      }

      @Test
      void 미리알림_간격이_0이면_설정을_실패한다() {
        // given

        // when
        ThrowingCallable setReminder = () -> futureDdudu.setReminder(0, 0, 0);

        // then
        Assertions.assertThatIllegalArgumentException()
            .isThrownBy(setReminder)
            .withMessage(DduduErrorCode.ZERO_REMINDER.getCodeName());
      }

      @Test
      void 미리알림_시간이_현재보다_이전이면_설정을_실패한다() {
        // given
        // keep begin time close to now to ensure reminder before now
        Ddudu todayDdudu = DduduFixture.createRandomDduduWithSchedule(
            userId,
            goalId,
            LocalDate.now()
        );
        Ddudu urgent = todayDdudu.setUpPeriod(LocalTime.now(), null);

        // when
        ThrowingCallable setReminder = () -> urgent.setReminder(0, 0, 15);

        // then
        Assertions.assertThatIllegalArgumentException()
            .isThrownBy(setReminder)
            .withMessage(DduduErrorCode.REMINDER_NOT_AFTER_NOW.getCodeName());
      }

      @Test
      void 미리알림이_있으면_hasReminder가_true를_반환한다() {
        // given
        LocalDate futureDate = LocalDate.now().plusDays(2);
        LocalTime beginAt = LocalTime.of(23, 30);
        Ddudu withReminder = DduduFixture.createRandomDduduWithSchedule(userId, goalId, futureDate)
            .setUpPeriod(beginAt, null)
            .setReminder(0, 0, 15);

        // when
        boolean actual = withReminder.hasReminder();

        // then
        assertThat(actual).isTrue();
      }

      @Test
      void 미리알림이_없으면_hasReminder가_false를_반환한다() {
        // given
        Ddudu withoutReminder = DduduFixture.createRandomDduduWithReference(
            goalId,
            userId,
            false,
            null
        );

        // when
        boolean actual = withoutReminder.hasReminder();

        // then
        assertThat(actual).isFalse();
      }

      @Test
      void 미리알림_취소를_성공한다() {
        // given
        LocalDate futureDate = LocalDate.now()
            .plusDays(2);
        LocalTime beginAt = LocalTime.of(23, 30);
        Ddudu scheduled = DduduFixture.createRandomDduduWithSchedule(userId, goalId, futureDate)
            .setUpPeriod(beginAt, null);
        Ddudu withReminder = scheduled.setReminder(0, 0, 15);

        // when
        Ddudu canceled = withReminder.cancelReminder();

        // then
        assertThat(canceled.getRemindAt()).isNull();
      }

    }

    @Nested
    class 권한_테스트 {

      @Test
      void 권한_확인을_성공한다() {
        // given

        // when
        ThrowingCallable check = () -> ddudu.validateDduduCreator(userId);

        // then
        Assertions.assertThatNoException()
            .isThrownBy(check);
      }

      @Test
      void 사용자의_아이디가_다르면_권한_확인을_실패한다() {
        // given
        long wrongUserId = DduduFixture.getRandomId();

        // when
        ThrowingCallable check = () -> ddudu.validateDduduCreator(wrongUserId);

        // then
        Assertions.assertThatExceptionOfType(SecurityException.class)
            .isThrownBy(check)
            .withMessage(DduduErrorCode.INVALID_AUTHORITY.getCodeName());
      }

    }

    @Nested
    class 기간_설정_테스트 {

      @Test
      void 기간_설정을_성공한다() {
        // given
        LocalTime now = LocalTime.now();

        // when
        Ddudu actual = ddudu.setUpPeriod(now, LocalTime.MAX);

        // then
        assertThat(actual)
            .hasFieldOrPropertyWithValue("id", ddudu.getId())
            .hasFieldOrPropertyWithValue("userId", ddudu.getUserId())
            .hasFieldOrPropertyWithValue("name", ddudu.getName())
            .hasFieldOrPropertyWithValue("postponedAt", ddudu.getPostponedAt())
            .hasFieldOrPropertyWithValue("status", ddudu.getStatus())
            .hasFieldOrPropertyWithValue("goalId", ddudu.getGoalId())
            .hasFieldOrPropertyWithValue("beginAt", now)
            .hasFieldOrPropertyWithValue("endAt", LocalTime.MAX);
      }

      @Test
      void 시작_시간만_설정할_수_있다() {
        // given
        LocalTime now = LocalTime.now();
        LocalTime expectedEndTime = ddudu.getEndAt();

        // when
        Ddudu actual = ddudu.setUpPeriod(now, null);

        // then
        assertThat(actual.getBeginAt()).isEqualTo(now);
        assertThat(actual.getEndAt()).isEqualTo(expectedEndTime);
      }

      @Test
      void 종료_시간만_설정할_수_있다() {
        // given
        LocalTime now = LocalTime.now();
        LocalTime expectedBeginAt = ddudu.getBeginAt();

        // when
        Ddudu actual = ddudu.setUpPeriod(null, now);

        // then
        assertThat(actual.getBeginAt()).isEqualTo(expectedBeginAt);
        assertThat(actual.getEndAt()).isEqualTo(now);
      }

    }

    @Nested
    class 날짜_변경_테스트 {

      @Test
      void 이미_완료한_뚜두에_미루기_요청하면_실패한다() {
        // given
        LocalDate newDate = LocalDate.now()
            .plusDays(1);

        ddudu = ddudu.switchStatus(); // 완료 상태로 변경
        assertThat(ddudu.getStatus()).isEqualTo(DduduStatus.COMPLETE);

        // when
        ThrowingCallable moveDate = () -> ddudu.moveDate(newDate, true);

        // then
        Assertions.assertThatIllegalArgumentException()
            .isThrownBy(moveDate)
            .withMessage(DduduErrorCode.UNABLE_TO_POSTPONE_COMPLETED_DDUDU.getCodeName());
      }

      @Test
      void 완료_하지_않은_뚜두의_날짜를_기존_날짜_이후로_변경하면_미루기_상태가_된다() {
        // given
        LocalDate previousScheduledOn = ddudu.getScheduledOn();
        LocalDate newDate = LocalDate.now()
            .plusDays(1);

        // when
        Ddudu actual = ddudu.moveDate(newDate, true);

        // then
        assertThat(actual.getScheduledOn()).isEqualTo(newDate);
        assertThat(actual.isPostponed()).isTrue();
        assertThat(actual.getPostponedAt()).isEqualTo(previousScheduledOn.atStartOfDay());
      }

      @Test
      void 미루기_요청이_false면_날짜만_변경된다() {
        // given
        LocalDate newDate = LocalDate.now().plusDays(1);

        // when
        Ddudu actual = ddudu.moveDate(newDate, false);

        // then
        assertThat(actual.getScheduledOn()).isEqualTo(newDate);
        assertThat(actual.isPostponed()).isFalse();
      }

      @Test
      void 변경할_날짜가_누락되면_변경을_실패한다() {
        // given

        // when
        ThrowingCallable moveDate = () -> ddudu.moveDate(null);

        // then
        Assertions.assertThatIllegalArgumentException()
            .isThrownBy(moveDate)
            .withMessage(DduduErrorCode.NULL_DATE_TO_MOVE.getCodeName());
      }

      @Test
      void postponedAt이_존재하면_isPostponed는_true를_반환한다() {
        // given
        Ddudu postponedDdudu = DduduFixture.createRandomDduduWithReference(
            goalId, userId, true, null);
        Ddudu actual = postponedDdudu;

        // then
        assertThat(actual.isPostponed()).isTrue();
      }

      @Test
      void postponedAt이_없으면_isPostponed는_false를_반환한다() {
        // given

        // when
        Ddudu actual = ddudu;

        // then
        assertThat(actual.isPostponed()).isFalse();
      }

    }

    @Nested
    class 상태_변경_테스트 {

      @Test
      void 미완료_뚜두는_완료_상태로_변경된다() {
        // given
        DduduStatus before = ddudu.getStatus();
        assertThat(before).isEqualTo(DduduStatus.UNCOMPLETED);

        // when
        Ddudu actual = ddudu.switchStatus();

        // then
        assertThat(actual.getStatus()).isEqualTo(DduduStatus.COMPLETE);
      }

      @Test
      void 완료_뚜두는_미완료_상태로_변경된다() {
        // given
        Ddudu completeDdudu = DduduFixture.createRandomDduduWithReference(
            goalId, userId, false, DduduStatus.COMPLETE);

        // when
        Ddudu actual = completeDdudu.switchStatus();

        // then
        assertThat(actual.getStatus()).isEqualTo(DduduStatus.UNCOMPLETED);
      }

    }

    @Nested
    class 이름_변경_테스트 {

      @Test
      void 뚜두_이름_변경을_성공한다() {
        // given
        String expected = DduduFixture.getRandomSentenceWithMax(50);

        // when
        Ddudu actual = ddudu.changeName(expected);

        // then
        assertThat(actual.getName()).isEqualTo(expected);
      }

      @Test
      void 이름이_50자를_넘으면_변경을_실패한다() {
        // given
        String longName = DduduFixture.getRandomSentence(51, 100);

        // when
        ThrowingCallable changeName = () -> ddudu.changeName(longName);

        // then
        Assertions.assertThatIllegalArgumentException()
            .isThrownBy(changeName)
            .withMessage(DduduErrorCode.EXCESSIVE_NAME_LENGTH.getCodeName());
      }

    }

    @Nested
    class 미리알림_시간차_테스트 {

      LocalDateTime scheduledAt;
      LocalDateTime remindAt;

      @BeforeEach
      void setUp() {
        scheduledAt = DduduFixture.getFutureDateTime(10, TimeUnit.DAYS);
        remindAt = scheduledAt.minusDays(DduduFixture.getRandomInt(1, 8));
        ddudu = DduduFixture.createDduduWithReminder(
            userId,
            goalId,
            scheduledAt.toLocalDate(),
            scheduledAt.toLocalTime(),
            remindAt
        );
      }

      @Test
      void 미리알림_시간차_계산을_성공한다() {
        // given
        Duration expected = Duration.between(remindAt, scheduledAt);

        // when
        Duration actual = ddudu.getRemindDifference();

        // then
        assertThat(actual).isEqualTo(expected);
      }

      @Test
      void 뚜두_시작시간이_없으면_미리알림_시간차_계산을_실패한다() {
        // given
        Ddudu dduduWithoutTime = DduduFixture.createRandomDduduWithSchedule(
            userId,
            goalId,
            scheduledAt.toLocalDate()
        );

        // when
        ThrowingCallable getDifference = dduduWithoutTime::getRemindDifference;

        // then
        assertThatIllegalStateException().isThrownBy(getDifference)
            .withMessage(DduduErrorCode.UNABLE_TO_GET_REMINDER.getCodeName());
      }

      @Test
      void 뚜두_미리알림_시간이_없으면_미리알림_시간차_계산을_실패한다() {
        // given
        Ddudu dduduWithoutReminder = DduduFixture.createRandomDduduWithSchedule(
                userId,
                goalId,
                scheduledAt.toLocalDate()
            )
            .setUpPeriod(scheduledAt.toLocalTime(), null);

        // when
        ThrowingCallable getDifference = dduduWithoutReminder::getRemindDifference;

        // then
        assertThatIllegalStateException().isThrownBy(getDifference)
            .withMessage(DduduErrorCode.UNABLE_TO_GET_REMINDER.getCodeName());
      }

      @Test
      void 뚜두_미리알림_시간이_시작시간보다_늦으면_시간차_계산을_실패한다() {
        // given
        int futureReminderDays = DduduFixture.getRandomInt(1, 10);
        LocalDateTime futureReminder = scheduledAt.plusDays(futureReminderDays);
        ddudu = DduduFixture.createDduduWithReminder(
            userId,
            goalId,
            scheduledAt.toLocalDate(),
            scheduledAt.toLocalTime(),
            futureReminder
        );

        // when
        ThrowingCallable getDifference = ddudu::getRemindDifference;

        // then
        assertThatIllegalStateException().isThrownBy(getDifference)
            .withMessage(DduduErrorCode.REMINDER_NOT_AFTER_NOW.getCodeName());
      }

    }

  }


  @Nested
  class 수정_테스트 {

    Long userId;
    Long goalId;

    @BeforeEach
    void setUp() {
      userId = DduduFixture.getRandomId();
      goalId = DduduFixture.getRandomId();
    }

    @Test
    void 미리알림_입력이_없으면_기존_미리알림을_유지한다() {
      // given
      LocalDate scheduledOn = LocalDate.now().plusDays(2);
      LocalTime beginAt = LocalTime.of(10, 0);
      LocalDateTime remindAt = scheduledOn.atTime(beginAt).minusMinutes(30);
      Ddudu ddudu = DduduFixture.createDduduWithReminder(
          userId,
          goalId,
          scheduledOn,
          beginAt,
          remindAt
      );

      // when
      Ddudu updated = ddudu.update(
          goalId,
          DduduFixture.getRandomSentenceWithMax(50),
          DduduFixture.createValidMemo(),
          scheduledOn,
          beginAt,
          LocalTime.of(11, 0),
          null,
          null,
          null
      );

      // then
      assertThat(updated.getRemindAt()).isEqualTo(remindAt);
    }

    @Test
    void 미리알림_입력이_있으면_미리알림을_재계산한다() {
      // given
      LocalDate scheduledOn = LocalDate.now().plusDays(2);
      LocalTime beginAt = LocalTime.of(10, 0);
      LocalDateTime oldReminder = scheduledOn.atTime(beginAt).minusMinutes(30);
      Ddudu ddudu = DduduFixture.createDduduWithReminder(
          userId,
          goalId,
          scheduledOn,
          beginAt,
          oldReminder
      );

      // when
      Ddudu updated = ddudu.update(
          goalId,
          DduduFixture.getRandomSentenceWithMax(50),
          DduduFixture.createValidMemo(),
          scheduledOn,
          beginAt,
          LocalTime.of(11, 0),
          0,
          null,
          10
      );

      // then
      assertThat(updated.getRemindAt()).isEqualTo(scheduledOn.atTime(beginAt).minusMinutes(10));
    }

  }


  @Nested
  class 복제_테스트 {

    Long userId;
    Long goalId;
    Ddudu ddudu;

    @BeforeEach
    void setUp() {
      userId = DduduFixture.getRandomId();
      goalId = DduduFixture.getRandomId();
      ddudu = DduduFixture.createRandomDduduWithReference(goalId, userId, false, null);
    }

    @Test
    void 뚜두_복제를_성공한다() {
      // given
      LocalDate tomorrow = LocalDate.now()
          .plusDays(1);

      // when
      Ddudu replica = ddudu.reproduceOnDate(tomorrow);

      // then
      assertThat(replica).isNotEqualTo(ddudu);
      assertThat(replica)
          .hasFieldOrPropertyWithValue("goalId", ddudu.getGoalId())
          .hasFieldOrPropertyWithValue("userId", ddudu.getUserId())
          .hasFieldOrPropertyWithValue("name", ddudu.getName())
          .hasFieldOrPropertyWithValue("status", DduduStatus.UNCOMPLETED)
          .hasFieldOrPropertyWithValue("postponedAt", null)
          .hasFieldOrPropertyWithValue("scheduledOn", tomorrow)
          .hasFieldOrPropertyWithValue("beginAt", ddudu.getBeginAt())
          .hasFieldOrPropertyWithValue("endAt", ddudu.getEndAt());
    }

    @Test
    void 같은_날로_복제를_시도하면_실패한다() {
      // given
      LocalDate newDate = ddudu.getScheduledOn();

      // when
      ThrowingCallable reproduce = () -> ddudu.reproduceOnDate(newDate);

      // then
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(reproduce)
          .withMessage(DduduErrorCode.UNABLE_TO_REPRODUCE_ON_SAME_DATE.getCodeName());
    }

  }

}
