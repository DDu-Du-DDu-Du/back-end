package com.ddudu.domain.planning.repeattodo.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.planning.repeattodo.aggregate.RepeatTodo;
import com.ddudu.domain.planning.repeattodo.aggregate.enums.RepeatType;
import com.ddudu.domain.planning.repeattodo.dto.CreateRepeatTodoCommand;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.RepeatTodoFixture;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class RepeatTodoDomainServiceTest {

  static RepeatTodoDomainService repeatTodoDomainService;

  @BeforeAll
  static void setUp() {
    repeatTodoDomainService = new RepeatTodoDomainService();
  }

  @Nested
  class 반복_투두_생성_테스트 {

    String name;
    Long goalId;
    LocalDate startDate;
    LocalDate endDate;

    @BeforeEach
    void setUp() {
      name = RepeatTodoFixture.getRandomSentenceWithMax(50);
      goalId = GoalFixture.getRandomId();
      startDate = LocalDate.now();
      endDate = LocalDate.now()
          .plusMonths(1);
    }

    @Test
    void 데일리_반복_투두를_생성한다() {
      // given
      CreateRepeatTodoCommand command = new CreateRepeatTodoCommand(
          name,
          goalId,
          RepeatType.DAILY.name(),
          null,
          null,
          null,
          startDate,
          endDate,
          null,
          null
      );

      // when
      RepeatTodo actual = repeatTodoDomainService.create(null, command);

      // then
      assertThat(actual).extracting("name", "goalId", "repeatType", "startDate", "endDate")
          .containsExactly(name, goalId, RepeatType.DAILY, startDate, endDate);
    }

    @Test
    void 위클리_반복_투두를_생성한다() {
      // given
      CreateRepeatTodoCommand command = new CreateRepeatTodoCommand(
          name,
          goalId,
          RepeatType.WEEKLY.name(),
          RepeatTodoFixture.getRandomRepeatDaysOfWeek(),
          null,
          null,
          startDate,
          endDate,
          null,
          null
      );

      // when
      RepeatTodo actual = repeatTodoDomainService.create(null, command);

      // then
      assertThat(actual).extracting("name", "goalId", "repeatType", "startDate", "endDate")
          .containsExactly(name, goalId, RepeatType.WEEKLY, startDate, endDate);
    }

    @Test
    void 먼슬리_반복_투두를_생성한다() {
      // given
      CreateRepeatTodoCommand command = new CreateRepeatTodoCommand(
          name,
          goalId,
          RepeatType.MONTHLY.name(),
          null,
          RepeatTodoFixture.getRandomRepeatDaysOfMonth(1),
          true,
          startDate,
          endDate,
          null,
          null
      );

      // when
      RepeatTodo actual = repeatTodoDomainService.create(goalId, command);

      // then
      assertThat(actual).extracting("name", "goalId", "repeatType", "startDate", "endDate")
          .containsExactly(name, goalId, RepeatType.MONTHLY, startDate, endDate);
    }

  }

  @Nested
  class 투두_생성_테스트 {

    Long userId;
    String name;
    Long goalId;
    LocalDate startDate;
    LocalDate endDate;

    @BeforeEach
    void setUp() {
      userId = GoalFixture.getRandomId();
      name = RepeatTodoFixture.getRandomSentenceWithMax(50);
      goalId = GoalFixture.getRandomId();
      startDate = LocalDate.now();
      endDate = LocalDate.now()
          .plusMonths(1);
    }

    @Test
    void 데일리_반복_투두의_투두를_생성한다() {
      // given
      RepeatTodo dailyRepeatTodo = RepeatTodoFixture.createRepeatTodo(
          RepeatType.DAILY,
          RepeatTodoFixture.createDailyRepeatPattern(),
          startDate,
          endDate
      );

      // when
      List<Todo> ddudus = repeatTodoDomainService.createRepeatedTodos(userId, dailyRepeatTodo);

      // then
      long expectedCount = ChronoUnit.DAYS.between(startDate, endDate) + 1;

      Assertions.assertThat(ddudus)
          .hasSize((int) expectedCount);
      ddudus.stream()
          .map(Todo::getScheduledOn)
          .forEach(date -> Assertions.assertThat(date)
              .isBetween(startDate, endDate));
    }

    @Test
    void 위클리_반복_투두의_투두를_생성한다() {
      // given
      List<String> repeatDayOfWeek = RepeatTodoFixture.getRandomRepeatDaysOfWeek(1);
      RepeatTodo weeklyRepeatTodo = RepeatTodoFixture.createRepeatTodo(
          RepeatType.WEEKLY,
          RepeatTodoFixture.createWeeklyRepeatPattern(repeatDayOfWeek),
          startDate,
          endDate
      );

      // when
      List<Todo> ddudus = repeatTodoDomainService.createRepeatedTodos(userId, weeklyRepeatTodo);

      // then
      ddudus.stream()
          .map(Todo::getScheduledOn)
          .forEach(date -> assertThat(date.getDayOfWeek()
              .name()).isEqualTo(repeatDayOfWeek.get(0)));
    }

    @Test
    void 먼슬리_반복_투두의_투두를_생성한다() {
      // given
      int repeatDayOfMonth = RepeatTodoFixture.getRandomInt(1, 31);
      RepeatTodo monthlyRepeatTodo = RepeatTodoFixture.createRepeatTodo(
          RepeatType.MONTHLY,
          RepeatTodoFixture.createMonthlyRepeatPattern(List.of(repeatDayOfMonth), true),
          startDate,
          endDate
      );

      // when
      List<Todo> ddudus = repeatTodoDomainService.createRepeatedTodos(
          userId,
          monthlyRepeatTodo
      );

      // then
      ddudus.stream()
          .map(Todo::getScheduledOn)
          .forEach(date -> assertThat(date.getDayOfMonth())
              .isIn(repeatDayOfMonth, startDate.lengthOfMonth()));
    }

  }

}
