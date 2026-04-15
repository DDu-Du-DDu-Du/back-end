package com.modoo.application.planning.goal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.modoo.application.common.port.auth.out.SignUpPort;
import com.modoo.application.common.port.goal.out.GoalLoaderPort;
import com.modoo.application.common.port.goal.out.SaveGoalPort;
import com.modoo.application.common.port.todo.out.SaveTodoPort;
import com.modoo.application.common.port.todo.out.TodoLoaderPort;
import com.modoo.common.exception.GoalErrorCode;
import com.modoo.domain.planning.goal.aggregate.Goal;
import com.modoo.domain.planning.todo.aggregate.Todo;
import com.modoo.domain.user.user.aggregate.User;
import com.modoo.fixture.GoalFixture;
import com.modoo.fixture.TodoFixture;
import com.modoo.fixture.UserFixture;
import java.util.Optional;
import org.assertj.core.api.Assertions;
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
class DeleteGoalServiceTest {

  @Autowired
  DeleteGoalService deleteGoalService;

  @Autowired
  GoalLoaderPort goalLoaderPort;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  TodoLoaderPort todoLoaderPort;

  @Autowired
  SaveTodoPort saveTodoPort;

  Long userId;
  Goal goal;

  @BeforeEach
  void setUp() {
    User user = signUpPort.save(UserFixture.createRandomUserWithId());
    userId = user.getId();
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(userId));
  }

  @Test
  void 목표를_삭제_할_수_있다() {
    // when
    deleteGoalService.delete(userId, goal.getId());

    // then
    Optional<Goal> foundAfterDeleted = goalLoaderPort.getOptionalGoal(goal.getId());

    Assertions.assertThat(foundAfterDeleted)
        .isEmpty();
  }

  @Test
  void 목표_삭제_시_해당_목표의_투두도_삭제된다() {
    //given
    Todo todo = TodoFixture.createRandomTodoWithGoal(goal);
    todo = saveTodoPort.save(todo);

    //when
    deleteGoalService.delete(userId, goal.getId());

    //then
    assertThat(todoLoaderPort.getOptionalTodo(todo.getId())).isEmpty();
  }

  @Test
  void 목표가_존재하지_않는_경우_예외가_발생하지_않는다() {
    // given
    Long invalidId = GoalFixture.getRandomId();

    // when / then
    assertDoesNotThrow(() -> deleteGoalService.delete(userId, invalidId));
  }

  @Test
  void 로그인_사용자가_권한이_없는_경우_삭제에_실패한다() {
    // given
    User anotherUser = signUpPort.save(UserFixture.createRandomUserWithId());

    // when
    ThrowingCallable delete = () -> deleteGoalService.delete(anotherUser.getId(), goal.getId());

    // then
    Assertions.assertThatExceptionOfType(SecurityException.class)
        .isThrownBy(delete)
        .withMessage(GoalErrorCode.INVALID_AUTHORITY.getCodeName());
  }


}
