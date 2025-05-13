package com.ddudu.application.planning.goal.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.goal.request.UpdateGoalRequest;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.goal.out.GoalLoaderPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.common.exception.GoalErrorCode;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.goal.aggregate.enums.PrivacyType;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import java.util.MissingResourceException;
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
class UpdateGoalServiceTest {

  @Autowired
  UpdateGoalService updateGoalService;

  @Autowired
  GoalLoaderPort goalLoaderPort;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  Long userId;
  Goal goal;
  UpdateGoalRequest request;
  String newName;
  String newColor;
  PrivacyType newPrivacyType;

  @BeforeEach
  void setUp() {
    User user = createAndSaveUser();
    userId = user.getId();
    goal = createAndSaveGoal(user);
    newName = GoalFixture.getRandomSentenceWithMax(50);
    newColor = GoalFixture.getRandomColor();
    newPrivacyType = GoalFixture.getRandomPrivacyType();
    request = new UpdateGoalRequest(newName, newColor, newPrivacyType.name());
  }

  @Test
  void 목표를_수정_할_수_있다() {
    // when
    updateGoalService.update(userId, goal.getId(), request);

    // then
    Goal actual = goalLoaderPort.getOptionalGoal(goal.getId())
        .get();
    assertThat(actual).extracting("name", "color", "privacyType")
        .containsExactly(newName, newColor, newPrivacyType);
  }

  @Test
  void 유효하지_않은_ID인_경우_수정에_실패한다() {
    // given
    Long invalidId = GoalFixture.getRandomId();

    // when
    ThrowingCallable update = () -> updateGoalService.update(userId, invalidId, request);

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(update)
        .withMessage(GoalErrorCode.ID_NOT_EXISTING.getCodeName());
  }

  @Test
  void 로그인_사용자가_권한이_없는_경우_수정에_실패한다() {
    // given
    User anotherUser = createAndSaveUser();

    // when
    ThrowingCallable update = () -> updateGoalService.update(
        anotherUser.getId(),
        goal.getId(),
        request
    );

    // then
    Assertions.assertThatExceptionOfType(SecurityException.class)
        .isThrownBy(update)
        .withMessage(GoalErrorCode.INVALID_AUTHORITY.getCodeName());
  }


  private User createAndSaveUser() {
    User user = UserFixture.createRandomUserWithId();
    return signUpPort.save(user);
  }

  private Goal createAndSaveGoal(User user) {
    Goal goal = GoalFixture.createRandomGoalWithUser(user.getId());
    return saveGoalPort.save(goal);
  }

}
