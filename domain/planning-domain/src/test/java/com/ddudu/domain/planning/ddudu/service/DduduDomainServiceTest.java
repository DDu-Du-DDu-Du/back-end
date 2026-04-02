package com.ddudu.domain.planning.todo.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.planning.todo.dto.CreateTodoCommand;
import com.ddudu.domain.planning.todo.dto.UpdateTodoCommand;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.TodoFixture;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class TodoDomainServiceTest {

  static TodoDomainService dduduDomainService;

  @BeforeAll
  static void setUp() {
    dduduDomainService = new TodoDomainService();
  }

  @Nested
  class 투두_생성_테스트 {

    Long userId;
    Long goalId;
    String name;
    String memo;
    LocalDate scheduledOn;
    CreateTodoCommand command;

    @BeforeEach
    void setUp() {
      userId = GoalFixture.getRandomId();
      goalId = GoalFixture.getRandomId();
      name = TodoFixture.getRandomSentenceWithMax(50);
      memo = TodoFixture.createValidMemo();
      scheduledOn = LocalDate.now();
      command = new CreateTodoCommand(
          goalId, name, memo, scheduledOn, null, null);
    }

    @Test
    void 투두를_생성한다() {
      // given

      // when
      Todo actual = dduduDomainService.create(userId, command);

      // then
      assertThat(actual).extracting("userId", "goalId", "name", "memo", "scheduledOn")
          .containsExactly(userId, goalId, name, memo, scheduledOn);
    }

    @Test
    void 날짜가_설정되지_않으면_투두의_날짜가_생성_날짜가_된다() {
      // given
      CreateTodoCommand request = new CreateTodoCommand(
          goalId,
          name,
          memo,
          null,
          null,
          null
      );

      // when
      Todo actual = dduduDomainService.create(userId, request);

      // then
      assertThat(actual).extracting("userId", "goalId", "name", "memo", "scheduledOn")
          .containsExactly(userId, goalId, name, memo, LocalDate.now());
    }

  }

  @Test
  void 투두를_수정한다() {
    // given
    Todo todo = TodoFixture.getTodoBuilder()
        .userId(GoalFixture.getRandomId())
        .goalId(GoalFixture.getRandomId())
        .build();
    UpdateTodoCommand command = UpdateTodoCommand.builder()
        .goalId(GoalFixture.getRandomId())
        .name(TodoFixture.getRandomSentenceWithMax(50))
        .memo(TodoFixture.createValidMemo())
        .scheduledOn(LocalDate.now().plusDays(1))
        .beginAt(LocalTime.of(10, 0))
        .endAt(LocalTime.of(11, 0))
        .build();

    // when
    Todo actual = dduduDomainService.update(todo, command);

    // then
    assertThat(actual).extracting("goalId", "name", "memo", "scheduledOn", "beginAt", "endAt")
        .containsExactly(
            command.goalId(),
            command.name(),
            command.memo(),
            command.scheduledOn(),
            command.beginAt(),
            command.endAt());
  }

}
