package com.ddudu.application.planning.goal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.goal.aggregate.enums.GoalStatus;
import com.ddudu.domain.planning.goal.exception.GoalErrorCode;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.application.planning.goal.dto.request.ChangeGoalStatusRequest;
import com.ddudu.application.user.auth.port.out.SignUpPort;
import com.ddudu.application.planning.goal.port.out.GoalLoaderPort;
import com.ddudu.application.planning.goal.port.out.SaveGoalPort;
import com.ddudu.application.user.user.port.out.UserLoaderPort;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import jakarta.transaction.Transactional;
import java.util.MissingResourceException;
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
class ChangeGoalStatusServiceTest {

  @Autowired
  ChangeGoalStatusService changeGoalStatusService;

  @Autowired
  UserLoaderPort userLoaderPort;

  @Autowired
  GoalLoaderPort goalLoaderPort;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  Long userId;
  Goal goal;
  ChangeGoalStatusRequest request;
  GoalStatus newStatus;

  @BeforeEach
  void setUp() {
    User user = createAndSaveUser();
    userId = user.getId();
    goal = createAndSaveGoal(user);
    newStatus = GoalFixture.getRandomGoalStatus();
    request = new ChangeGoalStatusRequest(newStatus.name());
  }

  @Test
  void 목표_상태를_변경할_수_있다() {
    // when
    changeGoalStatusService.changeStatus(userId, goal.getId(), request);

    // then
    Goal actual = goalLoaderPort.getOptionalGoal(goal.getId())
        .get();
    assertThat(actual.getStatus()).isEqualTo(newStatus);
  }

  @Test
  void 유효하지_않은_ID인_경우_수정에_실패한다() {
    // given
    Long invalidId = GoalFixture.getRandomId();

    // when
    ThrowingCallable update = () -> changeGoalStatusService.changeStatus(
        userId, invalidId, request);

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class).isThrownBy(update)
        .withMessage(GoalErrorCode.ID_NOT_EXISTING.getCodeName());
  }

  @Test
  void 로그인_사용자가_권한이_없는_경우_수정에_실패한다() {
    // given
    User anotherUser = createAndSaveUser();

    // when
    ThrowingCallable update = () -> changeGoalStatusService.changeStatus(
        anotherUser.getId(), goal.getId(), request);

    // then
    Assertions.assertThatExceptionOfType(SecurityException.class).isThrownBy(update)
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
