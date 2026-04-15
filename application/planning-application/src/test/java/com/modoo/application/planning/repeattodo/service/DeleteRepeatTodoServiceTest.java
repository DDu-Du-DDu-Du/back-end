package com.modoo.application.planning.repeattodo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.modoo.application.common.port.auth.out.SignUpPort;
import com.modoo.application.common.port.goal.out.SaveGoalPort;
import com.modoo.application.common.port.repeattodo.out.RepeatTodoLoaderPort;
import com.modoo.application.common.port.repeattodo.out.SaveRepeatTodoPort;
import com.modoo.application.common.port.todo.out.SaveTodoPort;
import com.modoo.application.common.port.todo.out.TodoLoaderPort;
import com.modoo.common.exception.GoalErrorCode;
import com.modoo.domain.planning.goal.aggregate.Goal;
import com.modoo.domain.planning.repeattodo.aggregate.RepeatTodo;
import com.modoo.domain.planning.todo.aggregate.Todo;
import com.modoo.domain.user.user.aggregate.User;
import com.modoo.fixture.GoalFixture;
import com.modoo.fixture.RepeatTodoFixture;
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
class DeleteRepeatTodoServiceTest {

  @Autowired
  DeleteRepeatTodoService deleteRepeatTodoService;

  @Autowired
  RepeatTodoLoaderPort repeatTodoLoaderPort;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveRepeatTodoPort saveRepeatTodoPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  TodoLoaderPort todoLoaderPort;

  @Autowired
  SaveTodoPort saveTodoPort;

  Long userId;
  RepeatTodo repeatTodo;

  @BeforeEach
  void setUp() {
    User user = signUpPort.save(UserFixture.createRandomUserWithId());
    userId = user.getId();
    Goal goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    repeatTodo = saveRepeatTodoPort.save(RepeatTodoFixture.createDailyRepeatTodoWithGoal(goal));
  }

  @Test
  void 반복투두를_삭제_할_수_있다() {
    // when
    deleteRepeatTodoService.delete(userId, repeatTodo.getId());

    // then
    Optional<RepeatTodo> foundAfterDeleted = repeatTodoLoaderPort.getOptionalRepeatTodo(
        repeatTodo.getId()
    );
    Assertions.assertThat(foundAfterDeleted)
        .isEmpty();
  }

  @Test
  void 반복투두_삭제_시_해당_반복투두의_투두도_삭제된다() {
    //given
    Todo todo = TodoFixture.createRandomTodoWithRepeatTodo(userId, repeatTodo);
    todo = saveTodoPort.save(todo);

    //when
    deleteRepeatTodoService.delete(userId, repeatTodo.getId());

    //then
    assertThat(todoLoaderPort.getOptionalTodo(todo.getId())).isEmpty();
  }

  @Test
  void 반복투두가_존재하지_않는_경우_예외가_발생하지_않는다() {
    // given
    Long invalidId = RepeatTodoFixture.getRandomId();

    // when / then
    assertDoesNotThrow(() -> deleteRepeatTodoService.delete(userId, invalidId));
  }

  @Test
  void 로그인_사용자가_권한이_없는_경우_삭제에_실패한다() {
    // given
    User anotherUser = signUpPort.save(UserFixture.createRandomUserWithId());

    // when
    ThrowingCallable delete = () -> deleteRepeatTodoService.delete(
        anotherUser.getId(),
        repeatTodo.getId()
    );

    // then
    Assertions.assertThatExceptionOfType(SecurityException.class)
        .isThrownBy(delete)
        .withMessage(GoalErrorCode.INVALID_AUTHORITY.getCodeName());
  }

}
