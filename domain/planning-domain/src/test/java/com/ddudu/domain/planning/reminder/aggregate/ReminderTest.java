package com.ddudu.domain.planning.reminder.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.common.exception.ReminderErrorCode;
import com.ddudu.fixture.ReminderFixture;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class ReminderTest {

  @Test
  void Reminder_도메인_생성에_성공한다() {
    // given
    Reminder reminder = ReminderFixture.createValidReminder();

    // when

    // then
    assertThat(reminder.getUserId()).isNotNull();
    assertThat(reminder.getTodoId()).isNotNull();
    assertThat(reminder.getRemindsAt()).isNotNull();
  }

  @Test
  void Reminder_도메인_생성_시_userId가_없으면_실패한다() {
    // given

    // when
    ThrowingCallable create = () -> ReminderFixture.createReminderWithUserId(null);

    // then
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(create)
        .withMessage(ReminderErrorCode.NULL_USER.getCodeName());
  }

  @Test
  void Reminder_도메인_생성_시_todoId가_없으면_실패한다() {
    // given

    // when
    ThrowingCallable create = () -> ReminderFixture.createReminderWithTodoId(null);

    // then
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(create)
        .withMessage(ReminderErrorCode.NULL_TODO_VALUE.getCodeName());
  }

  @Test
  void Reminder_도메인_생성_시_remindsAt이_없으면_실패한다() {
    // given

    // when
    ThrowingCallable create = () -> ReminderFixture.createReminderWithRemindsAt(null);

    // then
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(create)
        .withMessage(ReminderErrorCode.NULL_REMINDS_AT.getCodeName());
  }

  @Test
  void Reminder_도메인_팩토리_메서드_생성에_성공한다() {
    // given
    Long userId = ReminderFixture.getRandomId();
    Long todoId = ReminderFixture.getRandomId();
    LocalDateTime scheduledAt = ReminderFixture.getFutureDateTime(5, TimeUnit.DAYS);
    LocalDateTime remindsAt = scheduledAt.minusMinutes(30);

    // when
    Reminder reminder = Reminder.from(userId, todoId, remindsAt, scheduledAt);

    // then
    assertThat(reminder.getUserId()).isEqualTo(userId);
    assertThat(reminder.getTodoId()).isEqualTo(todoId);
    assertThat(reminder.getRemindsAt()).isEqualTo(remindsAt);
  }

  @Test
  void Reminder_도메인_팩토리_메서드_생성_시_remindsAt이_없으면_실패한다() {
    // given
    Long userId = ReminderFixture.getRandomId();
    Long todoId = ReminderFixture.getRandomId();
    LocalDateTime scheduledAt = ReminderFixture.getFutureDateTime(5, TimeUnit.DAYS);

    // when
    ThrowingCallable create = () -> Reminder.from(userId, todoId, null, scheduledAt);

    // then
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(create)
        .withMessage(ReminderErrorCode.NULL_REMINDS_AT.getCodeName());
  }

  @Test
  void Reminder_도메인_팩토리_메서드_생성_시_scheduledAt이_없으면_실패한다() {
    // given
    Long userId = ReminderFixture.getRandomId();
    Long todoId = ReminderFixture.getRandomId();
    LocalDateTime remindsAt = ReminderFixture.getFutureDateTime(5, TimeUnit.DAYS);

    // when
    ThrowingCallable create = () -> Reminder.from(userId, todoId, remindsAt, null);

    // then
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(create)
        .withMessage(ReminderErrorCode.NULL_SCHEDULED_AT.getCodeName());
  }

  @Test
  void Reminder_도메인_팩토리_메서드_생성_시_remindsAt이_scheduledAt보다_늦으면_실패한다() {
    // given
    Long userId = ReminderFixture.getRandomId();
    Long todoId = ReminderFixture.getRandomId();
    LocalDateTime scheduledAt = ReminderFixture.getFutureDateTime(5, TimeUnit.DAYS);
    LocalDateTime remindsAt = scheduledAt.plusMinutes(10);

    // when
    ThrowingCallable create = () -> Reminder.from(userId, todoId, remindsAt, scheduledAt);

    // then
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(create)
        .withMessage(ReminderErrorCode.INVALID_REMINDS_AT.getCodeName());
  }

  @Test
  void remindedAt이_있으면_isReminded가_true를_반환한다() {
    // given
    Reminder reminder = ReminderFixture.createReminderWithRemindedAt(LocalDateTime.now());

    // when
    boolean actual = reminder.isReminded();

    // then
    assertThat(actual).isTrue();
  }

  @Test
  void remindedAt이_없으면_isReminded가_false를_반환한다() {
    // given
    Reminder reminder = ReminderFixture.createValidReminder();

    // when
    boolean actual = reminder.isReminded();

    // then
    assertThat(actual).isFalse();
  }

  @Test
  void 일정과_미리알림_시간차_확인에_성공한다() {
    // given
    LocalDateTime scheduledAt = ReminderFixture.getFutureDateTime(3, TimeUnit.DAYS);
    LocalDateTime remindsAt = scheduledAt.minusMinutes(45);
    Reminder reminder = Reminder.from(
        ReminderFixture.getRandomId(),
        ReminderFixture.getRandomId(),
        remindsAt,
        scheduledAt
    );

    // when
    Duration actual = reminder.getRemindDifference(scheduledAt);

    // then
    assertThat(actual).isEqualTo(Duration.ofMinutes(45));
  }

  @Test
  void 일정과_미리알림_시간차_확인_시_일정시간이_null이면_실패한다() {
    // given
    Reminder reminder = ReminderFixture.createValidReminder();

    // when
    ThrowingCallable getDifference = () -> reminder.getRemindDifference(null);

    // then
    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(getDifference)
        .withMessage(ReminderErrorCode.REMINDER_NOT_AFTER_NOW.getCodeName());
  }

  @Test
  void 일정과_미리알림_시간차_확인_시_일정시간이_미리알림보다_이전이면_실패한다() {
    // given
    LocalDateTime scheduledAt = ReminderFixture.getFutureDateTime(3, TimeUnit.DAYS);
    LocalDateTime remindsAt = scheduledAt.minusMinutes(10);
    Reminder reminder = Reminder.from(
        ReminderFixture.getRandomId(),
        ReminderFixture.getRandomId(),
        remindsAt,
        scheduledAt
    );
    LocalDateTime invalidScheduledAt = remindsAt.minusMinutes(1);

    // when
    ThrowingCallable getDifference = () -> reminder.getRemindDifference(invalidScheduledAt);

    // then
    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(getDifference)
        .withMessage(ReminderErrorCode.REMINDER_NOT_AFTER_NOW.getCodeName());
  }

}
