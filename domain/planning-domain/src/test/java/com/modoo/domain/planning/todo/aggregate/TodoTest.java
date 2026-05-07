package com.modoo.domain.planning.todo.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.modoo.common.exception.TodoErrorCode;
import com.modoo.domain.planning.todo.aggregate.Todo.TodoBuilder;
import com.modoo.domain.planning.todo.aggregate.enums.TodoStatus;
import com.modoo.fixture.TodoFixture;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
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
      assertThat(todo.getTimeZone()).isEqualTo(ZoneOffset.UTC);
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
    void 목표가_없어도_생성을_성공한다() {
      // given

      // when
      Todo todo = Todo.builder()
          .userId(userId)
          .name(name)
          .build();

      // then
      assertThat(todo.getGoalId()).isNull();
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
    class 타임존_변환_테스트 {

      @Test
      void 타임존에_맞춰_일정일자와_시간을_변환한다() {
        // given
        Todo utcTodo = Todo.builder()
            .goalId(goalId)
            .userId(userId)
            .name(TodoFixture.getRandomSentenceWithMax(50))
            .scheduledOn(LocalDate.of(2026, 5, 7))
            .beginAt(LocalTime.of(23, 30))
            .endAt(LocalTime.of(23, 59))
            .timeZone(ZoneOffset.UTC)
            .build();

        // when
        Todo actual = utcTodo.convert(ZoneId.of("Asia/Seoul"));

        // then
        assertThat(actual.getScheduledOn()).isEqualTo(LocalDate.of(2026, 5, 8));
        assertThat(actual.getBeginAt()).isEqualTo(LocalTime.of(8, 30));
        assertThat(actual.getEndAt()).isEqualTo(LocalTime.of(8, 59));
        assertThat(actual.getTimeZone()).isEqualTo(ZoneId.of("Asia/Seoul"));
      }

      @Test
      void 시간이_없으면_일정일자는_유지하고_타임존만_변경한다() {
        // given
        LocalDate scheduledOn = LocalDate.of(2026, 5, 7);
        Todo dateOnlyTodo = Todo.builder()
            .goalId(goalId)
            .userId(userId)
            .name(TodoFixture.getRandomSentenceWithMax(50))
            .scheduledOn(scheduledOn)
            .timeZone(ZoneOffset.UTC)
            .build();

        // when
        Todo actual = dateOnlyTodo.convert("Asia/Seoul");

        // then
        assertThat(actual.getScheduledOn()).isEqualTo(scheduledOn);
        assertThat(actual.getBeginAt()).isNull();
        assertThat(actual.getEndAt()).isNull();
        assertThat(actual.getTimeZone()).isEqualTo(ZoneId.of("Asia/Seoul"));
      }

      @Test
      void 유효하지_않은_타임존이면_변환을_실패한다() {
        // given
        Todo todo = TodoFixture.createRandomTodoWithReference(goalId, userId, false, null);

        // when
        ThrowingCallable convert = () -> todo.convert("Invalid/TimeZone");

        // then
        Assertions.assertThatExceptionOfType(DateTimeException.class)
            .isThrownBy(convert);
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
    class 일정_조회_테스트 {

      @Test
      void 시작시간과_일정일자가_있으면_일정_일시를_반환한다() {
        // given
        LocalDate scheduledOn = LocalDate.now()
            .plusDays(1);
        LocalTime beginAt = LocalTime.of(10, 30);
        Todo scheduledTodo = TodoFixture.createRandomTodoWithSchedule(userId, goalId, scheduledOn)
            .setUpPeriod(beginAt, null);

        // when
        LocalDateTime actual = scheduledTodo.getScheduleDatetime();

        // then
        assertThat(actual).isEqualTo(scheduledOn.atTime(beginAt));
      }

      @Test
      void 시작시간이_없으면_일정_일시는_null을_반환한다() {
        // given
        Todo noBeginTimeTodo = TodoFixture.createRandomTodoWithSchedule(
            userId,
            goalId,
            LocalDate.now()
                .plusDays(1)
        );

        // when
        LocalDateTime actual = noBeginTimeTodo.getScheduleDatetime();

        // then
        assertThat(actual).isNull();
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
        LocalDate newDate = LocalDate.now()
            .plusDays(1);

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
