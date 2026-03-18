package com.ddudu.application.planning.ddudu.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.ddudu.response.TodoDetailResponse;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.ddudu.out.SaveTodoPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.common.exception.TodoErrorCode;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.TodoFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import java.time.LocalDate;
import java.util.MissingResourceException;
import org.assertj.core.api.AssertionsForClassTypes;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
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
class RetrieveTodoServiceTest {

  @Autowired
  RetrieveTodoService retrieveTodoService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveTodoPort saveTodoPort;

  User user;
  Goal goal;
  Todo todo;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    todo = saveTodoPort.save(TodoFixture.createRandomTodoWithGoal(goal));
  }

  @Test
  void ID를_통해_투두를_조회할_수_있다() {
    // when
    TodoDetailResponse actual = retrieveTodoService.findById(user.getId(), todo.getId());

    // then
    assertThat(actual)
        .hasFieldOrPropertyWithValue("id", todo.getId())
        .hasFieldOrPropertyWithValue("name", todo.getName())
        .hasFieldOrPropertyWithValue("memo", todo.getMemo())
        .hasFieldOrPropertyWithValue("status", todo.getStatus())
        .hasFieldOrPropertyWithValue("goalId", todo.getGoalId())
        .hasFieldOrPropertyWithValue("repeatTodoId", todo.getRepeatTodoId())
        .hasFieldOrPropertyWithValue("scheduledOn", todo.getScheduledOn())
        .hasFieldOrPropertyWithValue("beginAt", todo.getBeginAt())
        .hasFieldOrPropertyWithValue("endAt", todo.getEndAt())
        .hasFieldOrPropertyWithValue("postponedAt", todo.getPostponedAt())
        .hasFieldOrPropertyWithValue("remindAt", todo.getRemindAt());

  }

  @Test
  void 미룬_투두_상세조회시_미루기_일시를_반환한다() {
    // given
    Todo postponedTodo = saveTodoPort.save(
        TodoFixture.createTodoWithScheduleAndPostponedFlag(goal, true, LocalDate.now())
    );

    // when
    TodoDetailResponse actual = retrieveTodoService.findById(
        user.getId(),
        postponedTodo.getId()
    );

    // then
    assertThat(actual.postponedAt()).isNotNull();
  }

  @Test
  void 유효하지_않은_ID인_경우_조회에_실패한다() {
    // given
    Long invalidId = TodoFixture.getRandomId();

    // when
    ThrowingCallable callable = () -> retrieveTodoService.findById(user.getId(), invalidId);

    // then
    AssertionsForClassTypes.assertThatThrownBy(callable)
        .isInstanceOf(MissingResourceException.class)
        .hasMessageContaining(TodoErrorCode.ID_NOT_EXISTING.getCodeName());
  }

  @Test
  void 로그인_사용자가_투두의_주인이_아닌_경우_조회에_실패한다() {
    // given
    User anotherUser = signUpPort.save(UserFixture.createRandomUserWithId());

    // when
    ThrowingCallable callable = () -> retrieveTodoService.findById(
        anotherUser.getId(),
        todo.getId()
    );

    // then
    AssertionsForClassTypes.assertThatThrownBy(callable)
        .isInstanceOf(SecurityException.class)
        .hasMessageContaining(TodoErrorCode.INVALID_AUTHORITY.getCodeName());
  }

}
