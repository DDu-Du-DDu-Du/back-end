package com.ddudu.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.port.out.GoalLoaderPort;
import com.ddudu.application.port.out.SaveGoalPort;
import com.ddudu.application.port.out.SignUpPort;
import com.ddudu.application.port.out.UserLoaderPort;
import com.ddudu.application.port.out.goal.DeleteGoalPort;
import com.ddudu.application.service.goal.DeleteGoalService;
import com.ddudu.fixture.BaseFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;

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
  void 목표가_존재하지_않는_경우_예외가_발생한다() {
    // given
    Long invalidId = BaseFixture.getRandomId();

    // when
    ThrowingCallable delete = () -> deleteGoalService.delete(userId, invalidId);

    // then
    assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(delete)
        .withMessage(GoalErrorCode.ID_NOT_EXISTING.getCodeName());
  }

  @Test
  void 로그인_사용자가_권한이_없는_경우_삭제에_실패한다() {
    // given
    User anotherUser = createAndSaveUser();

    // when
    ThrowingCallable delete = () -> deleteGoalService.delete(anotherUser.getId(), goal.getId());

    // then
    assertThatExceptionOfType(AccessDeniedException.class).isThrownBy(delete)
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

}
