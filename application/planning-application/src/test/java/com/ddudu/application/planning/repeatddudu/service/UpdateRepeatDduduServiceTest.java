package com.ddudu.application.planning.repeattodo.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.repeattodo.request.UpdateRepeatTodoRequest;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.application.common.port.repeattodo.out.RepeatTodoLoaderPort;
import com.ddudu.application.common.port.repeattodo.out.SaveRepeatTodoPort;
import com.ddudu.application.common.port.todo.out.SaveTodoPort;
import com.ddudu.application.common.port.todo.out.TodoLoaderPort;
import com.ddudu.application.common.port.todo.out.TodoUpdatePort;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.repeattodo.aggregate.RepeatTodo;
import com.ddudu.domain.planning.repeattodo.aggregate.enums.RepeatType;
import com.ddudu.domain.planning.repeattodo.service.RepeatTodoDomainService;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class UpdateRepeatDduduServiceTest {

  @Autowired
  UpdateRepeatTodoService updateRepeatTodoService;

  @Autowired
  RepeatTodoDomainService repeatTodoDomainService;

  @Autowired
  RepeatTodoLoaderPort repeatTodoLoaderPort;

  @Autowired
  TodoLoaderPort dduduLoaderPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveRepeatTodoPort saveRepeatTodoPort;

  @Autowired
  SaveTodoPort saveTodoPort;

  @Autowired
  TodoUpdatePort dduduUpdatePort;

  User user;
  Goal goal;
  LocalDate nextMonday;
  LocalDate nextSunday;
  RepeatTodo repeatTodo;
  List<Todo> repeatedTodos;
  String nameToUpdate;
  DayOfWeek originalRepeatDayOfWeek;
  DayOfWeek repeatDayOfWeekToUpdate;
  UpdateRepeatTodoRequest request;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    nextMonday = LocalDate.now()
        .with(DayOfWeek.MONDAY)
        .plusDays(7);
    nextSunday = nextMonday.plusDays(6);
    originalRepeatDayOfWeek = DayOfWeek.MONDAY;
    repeatTodo = saveRepeatTodoPort.save(
        RepeatTodo.builder()
            .name("반복 투두")
            .repeatType(RepeatType.WEEKLY)
            .repeatPattern(RepeatType.WEEKLY.createRepeatPattern(
                List.of(originalRepeatDayOfWeek.name()),
                null,
                null
            ))
            .goalId(goal.getId())
            .startDate(nextMonday)
            .endDate(nextSunday)
            .build()
    );
    repeatedTodos = saveTodoPort.saveAll(
        repeatTodoDomainService.createRepeatedTodos(user.getId(), repeatTodo)
    );
    repeatDayOfWeekToUpdate = DayOfWeek.TUESDAY;
    nameToUpdate = "수정된 반복 투두";
    request = new UpdateRepeatTodoRequest(
        nameToUpdate,
        RepeatType.WEEKLY.name(),
        List.of(repeatDayOfWeekToUpdate.name()),
        null,
        null,
        nextMonday,
        nextSunday,
        null,
        null
    );
  }

  @Test
  void 반복_투두를_업데이트_하면_연결된_투두들도_함께_업데이트된다() {
    // when
    updateRepeatTodoService.update(user.getId(), repeatTodo.getId(), request);

    // then
    RepeatTodo updated = repeatTodoLoaderPort.getOptionalRepeatTodo(repeatTodo.getId())
        .get();
    assertThat(updated.getName()).isEqualTo(nameToUpdate);

    List<Todo> updatedTodos = dduduLoaderPort.getRepeatedTodos(repeatTodo);

    Assertions.assertThat(updatedTodos)
        .hasSize(1);
    Assertions.assertThat(updatedTodos)
        .extracting(Todo::getName)
        .containsExactly(nameToUpdate);
    assertThat(updatedTodos.get(0)
        .getScheduledOn()
        .getDayOfWeek())
        .isEqualTo(repeatDayOfWeekToUpdate);
  }

  @Test
  void 이미_완료된_반복_투두는_변경되지_않는다() {
    // given
    dduduUpdatePort.update(repeatedTodos.get(0)
        .switchStatus());

    // when
    updateRepeatTodoService.update(user.getId(), repeatTodo.getId(), request);

    // then
    List<Todo> updatedTodos = dduduLoaderPort.getRepeatedTodos(repeatTodo);

    Assertions.assertThat(updatedTodos)
        .hasSize(2);
    Assertions.assertThat(updatedTodos)
        .extracting(ddudu -> ddudu.getScheduledOn()
            .getDayOfWeek())
        .containsExactlyInAnyOrder(originalRepeatDayOfWeek, repeatDayOfWeekToUpdate);
  }

}
