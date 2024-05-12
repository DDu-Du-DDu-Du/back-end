package com.ddudu.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.GoalStatus;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.goal.dto.request.UpdateGoalRequest;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.port.out.GoalLoaderPort;
import com.ddudu.application.port.out.SaveGoalPort;
import com.ddudu.application.port.out.SignUpPort;
import com.ddudu.application.port.out.UserLoaderPort;
import com.ddudu.fixture.BaseFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
class UpdateGoalServiceTest {

  @Autowired
  UpdateGoalService updateGoalService;

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
  UpdateGoalRequest request;
  String newName;
  GoalStatus newStatus;
  String newColor;
  PrivacyType newPrivacyType;

  @BeforeEach
  void setUp() {
    User user = createAndSaveUser();
    userId = user.getId();
    goal = createAndSaveGoal(user);
    newName = BaseFixture.getRandomSentenceWithMax(50);
    newStatus = GoalFixture.getRandomGoalStatus();
    newColor = BaseFixture.getRandomColor();
    newPrivacyType = GoalFixture.getRandomPrivacyType();
    request = new UpdateGoalRequest(newName, newStatus, newColor, newPrivacyType);
  }

  @Test
  void 목표를_수정_할_수_있다() {
    // when
    updateGoalService.update(userId, goal.getId(), request);

    // then
    Goal actual = goalLoaderPort.findById(goal.getId())
        .get();
    assertThat(actual).extracting("name", "status", "color", "privacyType")
        .containsExactly(newName, newStatus, newColor, newPrivacyType);
  }

  @Test
  void 유효하지_않은_ID인_경우_수정에_실패한다() {
    // given
    Long invalidId = BaseFixture.getRandomId();

    // when
    ThrowingCallable update = () -> updateGoalService.update(userId, invalidId, request);

    // then
    assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(update)
        .withMessage(GoalErrorCode.ID_NOT_EXISTING.getCodeName());
  }

  @Test
  void 로그인_사용자가_권한이_없는_경우_수정에_실패한다() {
    // given
    User anotherUser = createAndSaveUser();

    // when
    ThrowingCallable update = () -> updateGoalService.update(
        anotherUser.getId(), goal.getId(), request);

    // then
    assertThatExceptionOfType(AccessDeniedException.class).isThrownBy(update)
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
