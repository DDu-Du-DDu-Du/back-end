package com.ddudu.application.service.goal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.goal.response.GoalResponse;
import com.ddudu.application.port.out.auth.SignUpPort;
import com.ddudu.application.port.out.goal.GoalLoaderPort;
import com.ddudu.application.port.out.goal.SaveGoalPort;
import com.ddudu.application.port.out.user.UserLoaderPort;
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
class RetrieveGoalServiceTest {

  @Autowired
  RetrieveGoalService retrieveGoalService;

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

  @BeforeEach
  void setUp() {
    User user = createAndSaveUser();
    userId = user.getId();
    goal = createAndSaveGoal(user);
  }

  @Test
  void ID를_통해_목표를_조회_할_수_있다() {
    // when
    GoalResponse actual = retrieveGoalService.getById(userId, goal.getId());

    // then
    assertThat(actual).extracting("id", "name", "status", "color", "privacyType")
        .containsExactly(
            goal.getId(), goal.getName(), goal.getStatus(), goal.getColor(), goal.getPrivacyType());
  }

  @Test
  void 유효하지_않은_ID인_경우_조회에_실패한다() {
    // given
    Long invalidId = UserFixture.getRandomId();

    // when
    ThrowingCallable getById = () -> retrieveGoalService.getById(userId, invalidId);

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(getById)
        .withMessage(GoalErrorCode.ID_NOT_EXISTING.getCodeName());
  }

  @Test
  void 로그인_사용자가_목표의_주인이_아닌_경우_조회에_실패한다() {
    // given
    User anotherUser = createAndSaveUser();

    // when
    ThrowingCallable getById = () -> retrieveGoalService.getById(anotherUser.getId(), goal.getId());

    // then
    assertThatExceptionOfType(SecurityException.class).isThrownBy(getById)
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
