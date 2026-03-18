package com.ddudu.domain.planning.todo.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import com.ddudu.common.exception.TodoErrorCode;
import com.ddudu.domain.planning.todo.aggregate.Todo.TodoBuilder;
import com.ddudu.domain.planning.todo.aggregate.enums.TodoStatus;
import com.ddudu.fixture.TodoFixture;
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
class TodoTest {

  Long goalId;
  Long userId;

  @BeforeEach
  void setUp() {
    goalId = TodoFixture.getRandomId();
    userId = TodoFixture.getRandomId();
  }

  @Nested
  class 생성_테스트 {

    String name;

    @BeforeEach
    void setUp() {
      name = TodoFixture.getRandomSentenceWithMax(50);
    }

    @Test
    void 투두_생성을_성공한다() {
      // given

      // when
      Todo todo = Todo.builder()
          .goalId(goalId)
          .userId(userId)
          .name(name)
          .status(TodoStatus.COMPLETE)
          .isPostponed(true)
          .build();

      // then
      assertThat(todo).isNotNull();
    }

    @Test
    void 투두_생성_시_디폴트_값이_적용된다() {
      // given

      // when
      Todo todo = Todo.builder()
          .goalId(goalId)
          .userId(userId)
          .name(name)
          .build();

      // then
      assertThat(todo.getStatus()).isEqualTo(TodoStatus.UNCOMPLETED);
      assertThat(todo.isPostponed()).isFalse();
      assertThat(todo.getScheduledOn()).isEqualTo(LocalDate.now());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void 이름이_빈_값이면_생성을_실패한다(String blankName) {
      // given
      TodoBuilder builder = Todo.builder()
          .goalId(goalId)
          .userId(userId)
          .name(blankName);

      // when
      ThrowingCallable create = builder::build;

      // then
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(create)
          .withMessage(TodoErrorCode.BLANK_NAME.getCodeName());
    }

    @Test
    void 이름이_50자를_넘으면_생성을_실패한다() {
      // given
      String over50 = TodoFixture.getRandomSentence(51, 100);
      TodoBuilder builder = Todo.builder()
          .goalId(goalId)
          .userId(userId)
          .name(over50);

      // when
      ThrowingCallable create = builder::build;

      // then
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(create)
          .withMessage(TodoErrorCode.EXCESSIVE_NAME_LENGTH.getCodeName());
    }

    @Test
    void 메모가_2000자_이하면_생성을_성공한다() {
      // given
      String validMemo = TodoFixture.createValidMemo();

      // when
      Todo todo = Todo.builder()
          .goalId(goalId)
          .userId(userId)
          .name(name)
          .memo(validMemo)
          .build();

      // then
      assertThat(todo.getMemo()).isEqualTo(validMemo);
    }

    @Test
    void 메모가_2000자를_넘으면_생성을_실패한다() {
      // given
      String overLengthMemo = TodoFixture.createOverLengthMemo();
      TodoBuilder builder = Todo.builder()
          .goalId(goalId)
          .userId(userId)
          .name(name)
          .memo(overLengthMemo);

      // when
      ThrowingCallable create = builder::build;

      // then
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(create)
          .withMessage(TodoErrorCode.EXCESSIVE_MEMO_LENGTH.getCodeName());
    }

    @Test
    void 목표가_없으면_생성을_실패한다() {
      // given
      TodoBuilder builder = Todo.builder()
          .userId(userId)
          .name(name);

      // when
      ThrowingCallable create = builder::build;

      // then
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(create)
          .withMessage(TodoErrorCode.NULL_GOAL_VALUE.getCodeName());
    }

    @Test
    void 사용자가_없으면_생성을_실패한다() {
      // given
      TodoBuilder builder = Todo.builder()
          .goalId(goalId)
          .name(name);

      // when
      ThrowingCallable create = builder::build;

      // then
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(create)
          .withMessage(TodoErrorCode.NULL_USER.getCodeName());
    }

    @Test
    void 시작_시간이_종료_시간보다_뒤면_생성을_실패한다() {
      // given
      TodoBuilder builder = Todo.builder()
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
          .withMessage(TodoErrorCode.UNABLE_TO_FINISH_BEFORE_BEGIN.getCodeName());
    }

  }

  @Nested
  class 기능_테스트 {

    Long userId;
    Long goalId;
    Todo todo;

    @BeforeEach
    void setUp() {
      userId = TodoFixture.getRandomId();
      goalId = TodoFixture.getRandomId();
      todo = TodoFixture.createRandomTodoWithReference(goalId, userId, false, null);
    }

    @Nested
    class 미리알림_설정_테스트 {

      Todo futureTodo;
      LocalTime beginAt;
      int days;
      int hours;
      int minutes;

      @BeforeEach
      void setUp() {
        LocalDate futureDate = TodoFixture.getFutureDate(10);
        beginAt = TodoFixture.getFutureTime();
        Todo beforeTimeSet = TodoFixture.createRandomTodoWithSchedule(
            userId,
            goalId,
            futureDate
        );
        futureTodo = beforeTimeSet.setUpPeriod(beginAt, null);
        int dayDifference = (int) ChronoUnit.DAYS.between(LocalDate.now(), futureDate);
        int hourDifference = (int) ChronoUnit.HOURS.between(LocalTime.now(), beginAt);
        days = TodoFixture.getRandomInt(0, Math.max(dayDifference - 1, 0));
        hours = TodoFixture.getRandomInt(0, Math.max(hourDifference - 1, 0));
        minutes = TodoFixture.getRandomInt(1, 59);
      }

      @Test
      void 시작_시간이_있는_투두에_미리알림을_설정한다() {
        // given

        // when
        Todo updated = futureTodo.setReminder(days, hours, minutes);

        // then
        LocalDateTime actual = updated.getRemindAt();
        LocalDateTime expected = futureTodo.getScheduledOn()
            .atTime(beginAt)
            .minusDays(days)
            .minusHours(hours)
            .minusMinutes(minutes);

        assertThat(actual).isEqualTo(expected);
      }

      @Test
      void 미리알림_입력값이_음수면_미리알림_설정을_실패한다() {
        // given
        int negativeDays = TodoFixture.getRandomNegative();
        int negativeHours = TodoFixture.getRandomNegative();
        int negativeMins = TodoFixture.getRandomNegative();

        // when
        ThrowingCallable setReminder = () -> futureTodo.setReminder(
            negativeDays,
            negativeHours,
            negativeMins
        );

        // then
        Assertions.assertThatIllegalArgumentException()
            .isThrownBy(setReminder)
            .withMessage(TodoErrorCode.NEGATIVE_REMINDER_INPUT_EXISTS.getCodeName());
      }

      @Test
      void 시작_시간이_없으면_미리알림_설정을_실패한다() {
        // given
        Todo noTime = TodoFixture.createRandomTodoWithSchedule(userId, goalId, LocalDate.now());

        // when
        ThrowingCallable setReminder = () -> noTime.setReminder(0, 1, 0);

        // then
        Assertions.assertThatIllegalArgumentException()
            .isThrownBy(setReminder)
            .withMessage(TodoErrorCode.BEGIN_AT_REQUIRED_FOR_REMINDER.getCodeName());
      }

      @Test
      void 미리알림_간격이_0이면_설정을_실패한다() {
        // given

        // when
        ThrowingCallable setReminder = () -> futureTodo.setReminder(0, 0, 0);

        // then
        Assertions.assertThatIllegalArgumentException()
            .isThrownBy(setReminder)
            .withMessage(TodoErrorCode.ZERO_REMINDER.getCodeName());
      }

      @Test
      void 미리알림_시간이_현재보다_이전이면_설정을_실패한다() {
        // given
        // keep begin time close to now to ensure reminder before now
        Todo todayTodo = TodoFixture.createRandomTodoWithSchedule(
            userId,
            goalId,
            LocalDate.now()
        );
        Todo urgent = todayTodo.setUpPeriod(LocalTime.now(), null);

        // when
        ThrowingCallable setReminder = () -> urgent.setReminder(0, 0, 15);

        // then
        Assertions.assertThatIllegalArgumentException()
            .isThrownBy(setReminder)
            .withMessage(TodoErrorCode.REMINDER_NOT_AFTER_NOW.getCodeName());
      }

      @Test
      void 미리알림이_있으면_hasReminder가_true를_반환한다() {
        // given
        LocalDate futureDate = LocalDate.now().plusDays(2);
        LocalTime beginAt = LocalTime.of(23, 30);
        Todo withReminder = TodoFixture.createRandomTodoWithSchedule(userId, goalId, futureDate)
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
        Todo withoutReminder = TodoFixture.createRandomTodoWithReference(
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
        Todo scheduled = TodoFixture.createRandomTodoWithSchedule(userId, goalId, futureDate)
            .setUpPeriod(beginAt, null);
        Todo withReminder = scheduled.setReminder(0, 0, 15);

        // when
        Todo canceled = withReminder.cancelReminder();

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
        ThrowingCallable check = () -> todo.validateTodoCreator(userId);

        // then
        Assertions.assertThatNoException()
            .isThrownBy(check);
      }

      @Test
      void 사용자의_아이디가_다르면_권한_확인을_실패한다() {
        // given
        long wrongUserId = TodoFixture.getRandomId();

        // when
        ThrowingCallable check = () -> todo.validateTodoCreator(wrongUserId);

        // then
        Assertions.assertThatExceptionOfType(SecurityException.class)
            .isThrownBy(check)
            .withMessage(TodoErrorCode.INVALID_AUTHORITY.getCodeName());
      }

    }

    @Nested
    class 기간_설정_테스트 {

      @Test
      void 기간_설정을_성공한다() {
        // given
        LocalTime now = LocalTime.now();

        // when
        Todo actual = todo.setUpPeriod(now, LocalTime.MAX);

        // then
        assertThat(actual)
            .hasFieldOrPropertyWithValue("id", todo.getId())
            .hasFieldOrPropertyWithValue("userId", todo.getUserId())
            .hasFieldOrPropertyWithValue("name", todo.getName())
            .hasFieldOrPropertyWithValue("postponedAt", todo.getPostponedAt())
            .hasFieldOrPropertyWithValue("status", todo.getStatus())
            .hasFieldOrPropertyWithValue("goalId", todo.getGoalId())
            .hasFieldOrPropertyWithValue("beginAt", now)
            .hasFieldOrPropertyWithValue("endAt", LocalTime.MAX);
      }

      @Test
      void 시작_시간만_설정할_수_있다() {
        // given
        LocalTime now = LocalTime.now();
        LocalTime expectedEndTime = todo.getEndAt();

        // when
        Todo actual = todo.setUpPeriod(now, null);

        // then
        assertThat(actual.getBeginAt()).isEqualTo(now);
        assertThat(actual.getEndAt()).isEqualTo(expectedEndTime);
      }

      @Test
      void 종료_시간만_설정할_수_있다() {
        // given
        LocalTime now = LocalTime.now();
        LocalTime expectedBeginAt = todo.getBeginAt();

        // when
        Todo actual = todo.setUpPeriod(null, now);

        // then
        assertThat(actual.getBeginAt()).isEqualTo(expectedBeginAt);
        assertThat(actual.getEndAt()).isEqualTo(now);
      }

    }

    @Nested
    class 날짜_변경_테스트 {

      @Test
      void 이미_완료한_투두에_미루기_요청하면_실패한다() {
        // given
        LocalDate newDate = LocalDate.now()
            .plusDays(1);

        todo = todo.switchStatus(); // 완료 상태로 변경
        assertThat(todo.getStatus()).isEqualTo(TodoStatus.COMPLETE);

        // when
        ThrowingCallable moveDate = () -> todo.moveDate(newDate, true);

        // then
        Assertions.assertThatIllegalArgumentException()
            .isThrownBy(moveDate)
            .withMessage(TodoErrorCode.UNABLE_TO_POSTPONE_COMPLETED_TODO.getCodeName());
      }

      @Test
      void 완료_하지_않은_투두의_날짜를_기존_날짜_이후로_변경하면_미루기_상태가_된다() {
        // given
        LocalDate previousScheduledOn = todo.getScheduledOn();
        LocalDate newDate = LocalDate.now()
            .plusDays(1);

        // when
        Todo actual = todo.moveDate(newDate, true);

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
        Todo actual = todo.moveDate(newDate, false);

        // then
        assertThat(actual.getScheduledOn()).isEqualTo(newDate);
        assertThat(actual.isPostponed()).isFalse();
      }

      @Test
      void 변경할_날짜가_누락되면_변경을_실패한다() {
        // given

        // when
        ThrowingCallable moveDate = () -> todo.moveDate(null);

        // then
        Assertions.assertThatIllegalArgumentException()
            .isThrownBy(moveDate)
            .withMessage(TodoErrorCode.NULL_DATE_TO_MOVE.getCodeName());
      }

      @Test
      void postponedAt이_존재하면_isPostponed는_true를_반환한다() {
        // given
        Todo postponedTodo = TodoFixture.createRandomTodoWithReference(
            goalId, userId, true, null);
        Todo actual = postponedTodo;

        // then
        assertThat(actual.isPostponed()).isTrue();
      }

      @Test
      void postponedAt이_없으면_isPostponed는_false를_반환한다() {
        // given

        // when
        Todo actual = todo;

        // then
        assertThat(actual.isPostponed()).isFalse();
      }

    }

    @Nested
    class 상태_변경_테스트 {

      @Test
      void 미완료_투두는_완료_상태로_변경된다() {
        // given
        TodoStatus before = todo.getStatus();
        assertThat(before).isEqualTo(TodoStatus.UNCOMPLETED);

        // when
        Todo actual = todo.switchStatus();

        // then
        assertThat(actual.getStatus()).isEqualTo(TodoStatus.COMPLETE);
      }

      @Test
      void 완료_투두는_미완료_상태로_변경된다() {
        // given
        Todo completeTodo = TodoFixture.createRandomTodoWithReference(
            goalId, userId, false, TodoStatus.COMPLETE);

        // when
        Todo actual = completeTodo.switchStatus();

        // then
        assertThat(actual.getStatus()).isEqualTo(TodoStatus.UNCOMPLETED);
      }

    }

    @Nested
    class 이름_변경_테스트 {

      @Test
      void 투두_이름_변경을_성공한다() {
        // given
        String expected = TodoFixture.getRandomSentenceWithMax(50);

        // when
        Todo actual = todo.changeName(expected);

        // then
        assertThat(actual.getName()).isEqualTo(expected);
      }

      @Test
      void 이름이_50자를_넘으면_변경을_실패한다() {
        // given
        String longName = TodoFixture.getRandomSentence(51, 100);

        // when
        ThrowingCallable changeName = () -> todo.changeName(longName);

        // then
        Assertions.assertThatIllegalArgumentException()
            .isThrownBy(changeName)
            .withMessage(TodoErrorCode.EXCESSIVE_NAME_LENGTH.getCodeName());
      }

    }

    @Nested
    class 미리알림_시간차_테스트 {

      LocalDateTime scheduledAt;
      LocalDateTime remindAt;

      @BeforeEach
      void setUp() {
        scheduledAt = TodoFixture.getFutureDateTime(10, TimeUnit.DAYS);
        remindAt = scheduledAt.minusDays(TodoFixture.getRandomInt(1, 8));
        todo = TodoFixture.createTodoWithReminder(
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
        Duration actual = todo.getRemindDifference();

        // then
        assertThat(actual).isEqualTo(expected);
      }

      @Test
      void 투두_시작시간이_없으면_미리알림_시간차_계산을_실패한다() {
        // given
        Todo dduduWithoutTime = TodoFixture.createRandomTodoWithSchedule(
            userId,
            goalId,
            scheduledAt.toLocalDate()
        );

        // when
        ThrowingCallable getDifference = dduduWithoutTime::getRemindDifference;

        // then
        assertThatIllegalStateException().isThrownBy(getDifference)
            .withMessage(TodoErrorCode.UNABLE_TO_GET_REMINDER.getCodeName());
      }

      @Test
      void 투두_미리알림_시간이_없으면_미리알림_시간차_계산을_실패한다() {
        // given
        Todo dduduWithoutReminder = TodoFixture.createRandomTodoWithSchedule(
                userId,
                goalId,
                scheduledAt.toLocalDate()
            )
            .setUpPeriod(scheduledAt.toLocalTime(), null);

        // when
        ThrowingCallable getDifference = dduduWithoutReminder::getRemindDifference;

        // then
        assertThatIllegalStateException().isThrownBy(getDifference)
            .withMessage(TodoErrorCode.UNABLE_TO_GET_REMINDER.getCodeName());
      }

      @Test
      void 투두_미리알림_시간이_시작시간보다_늦으면_시간차_계산을_실패한다() {
        // given
        int futureReminderDays = TodoFixture.getRandomInt(1, 10);
        LocalDateTime futureReminder = scheduledAt.plusDays(futureReminderDays);
        todo = TodoFixture.createTodoWithReminder(
            userId,
            goalId,
            scheduledAt.toLocalDate(),
            scheduledAt.toLocalTime(),
            futureReminder
        );

        // when
        ThrowingCallable getDifference = todo::getRemindDifference;

        // then
        assertThatIllegalStateException().isThrownBy(getDifference)
            .withMessage(TodoErrorCode.REMINDER_NOT_AFTER_NOW.getCodeName());
      }

    }

  }


  @Nested
  class 수정_테스트 {

    Long userId;
    Long goalId;

    @BeforeEach
    void setUp() {
      userId = TodoFixture.getRandomId();
      goalId = TodoFixture.getRandomId();
    }

    @Test
    void 미리알림_입력이_없으면_기존_미리알림을_유지한다() {
      // given
      LocalDate scheduledOn = LocalDate.now().plusDays(2);
      LocalTime beginAt = LocalTime.of(10, 0);
      LocalDateTime remindAt = scheduledOn.atTime(beginAt).minusMinutes(30);
      Todo todo = TodoFixture.createTodoWithReminder(
          userId,
          goalId,
          scheduledOn,
          beginAt,
          remindAt
      );

      // when
      Todo updated = todo.update(
          goalId,
          TodoFixture.getRandomSentenceWithMax(50),
          TodoFixture.createValidMemo(),
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
      Todo todo = TodoFixture.createTodoWithReminder(
          userId,
          goalId,
          scheduledOn,
          beginAt,
          oldReminder
      );

      // when
      Todo updated = todo.update(
          goalId,
          TodoFixture.getRandomSentenceWithMax(50),
          TodoFixture.createValidMemo(),
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
    Todo todo;

    @BeforeEach
    void setUp() {
      userId = TodoFixture.getRandomId();
      goalId = TodoFixture.getRandomId();
      todo = TodoFixture.createRandomTodoWithReference(goalId, userId, false, null);
    }

    @Test
    void 투두_복제를_성공한다() {
      // given
      LocalDate tomorrow = LocalDate.now()
          .plusDays(1);

      // when
      Todo replica = todo.reproduceOnDate(tomorrow);

      // then
      assertThat(replica).isNotEqualTo(todo);
      assertThat(replica)
          .hasFieldOrPropertyWithValue("goalId", todo.getGoalId())
          .hasFieldOrPropertyWithValue("userId", todo.getUserId())
          .hasFieldOrPropertyWithValue("name", todo.getName())
          .hasFieldOrPropertyWithValue("status", TodoStatus.UNCOMPLETED)
          .hasFieldOrPropertyWithValue("postponedAt", null)
          .hasFieldOrPropertyWithValue("scheduledOn", tomorrow)
          .hasFieldOrPropertyWithValue("beginAt", todo.getBeginAt())
          .hasFieldOrPropertyWithValue("endAt", todo.getEndAt());
    }

    @Test
    void 같은_날로_복제를_시도하면_실패한다() {
      // given
      LocalDate newDate = todo.getScheduledOn();

      // when
      ThrowingCallable reproduce = () -> todo.reproduceOnDate(newDate);

      // then
      Assertions.assertThatIllegalArgumentException()
          .isThrownBy(reproduce)
          .withMessage(TodoErrorCode.UNABLE_TO_REPRODUCE_ON_SAME_DATE.getCodeName());
    }

  }

}
