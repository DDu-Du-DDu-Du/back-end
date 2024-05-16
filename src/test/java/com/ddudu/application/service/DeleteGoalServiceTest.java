package com.ddudu.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.port.out.SignUpPort;
import com.ddudu.application.port.out.UserLoaderPort;
import com.ddudu.application.port.out.goal.DeleteGoalPort;
import com.ddudu.application.port.out.goal.GoalLoaderPort;
import com.ddudu.application.port.out.goal.SaveGoalPort;
import com.ddudu.application.service.goal.DeleteGoalService;
import com.ddudu.fixture.BaseFixture;
import com.ddudu.fixture.DduduFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import com.ddudu.old.todo.domain.Todo;
import com.ddudu.old.todo.domain.TodoRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.MissingResourceException;
import java.util.Optional;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class DeleteGoalServiceTest {

  @Autowired
  DeleteGoalService deleteGoalService;

  @Autowired
  UserLoaderPort userLoaderPort;

  @Autowired
  GoalLoaderPort goalLoaderPort;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  DeleteGoalPort deleteGoalPort;

  @Autowired
  TodoRepository todoRepository;

  @Autowired
  EntityManager entityManager;

  Long userId;
  Goal goal;

  @BeforeEach
  void setUp() {
    User user = createAndSaveUser();
    userId = user.getId();
    goal = createAndSaveGoal(user);
  }

  @Test
  void 목표를_삭제_할_수_있다() {
    // when
    deleteGoalService.delete(userId, goal.getId());

    // then
    Optional<Goal> foundAfterDeleted = goalLoaderPort.findById(goal.getId());
    assertThat(foundAfterDeleted).isEmpty();
  }

  @Test
  void 목표_삭제_시_해당_목표의_뚜두도_삭제된다() {
    //given
    Todo todo = DduduFixture.createRandomDduduWithGoal(goal);
    todo = todoRepository.save(todo);

    //when
    deleteGoalService.delete(userId, goal.getId());
    flushAndClear();

    //then
    assertThat(todoRepository.findById(todo.getId())).isEmpty();
  }

  @Test
  void 목표가_존재하지_않는_경우_예외가_발생한다() {
    // given
    Long invalidId = BaseFixture.getRandomId();

    // when
    ThrowingCallable delete = () -> deleteGoalService.delete(userId, invalidId);

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(delete)
        .withMessage(GoalErrorCode.ID_NOT_EXISTING.getCodeName());
  }

  @Test
  void 로그인_사용자가_권한이_없는_경우_삭제에_실패한다() {
    // given
    User anotherUser = createAndSaveUser();

    // when
    ThrowingCallable delete = () -> deleteGoalService.delete(anotherUser.getId(), goal.getId());

    // then
    assertThatExceptionOfType(SecurityException.class).isThrownBy(delete)
        .withMessage(GoalErrorCode.INVALID_AUTHORITY.getCodeName());
  }

  private User createAndSaveUser() {
    User user = UserFixture.createRandomUserWithId();
    return signUpPort.save(user);
  }

  private Goal createAndSaveGoal(User user) {
    Goal goal = GoalFixture.createRandomGoalWithUser(user);
    return saveGoalPort.save(goal);
  }

  private void flushAndClear() {
    entityManager.flush();
    entityManager.clear();
  }

}
