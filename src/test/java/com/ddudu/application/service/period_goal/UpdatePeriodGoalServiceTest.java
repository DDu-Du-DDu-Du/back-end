package com.ddudu.application.service.period_goal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.application.domain.period_goal.domain.PeriodGoal;
import com.ddudu.application.domain.period_goal.exception.PeriodGoalErrorCode;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.period_goal.request.UpdatePeriodGoalRequest;
import com.ddudu.application.port.out.auth.SignUpPort;
import com.ddudu.application.port.out.period_goal.PeriodGoalLoaderPort;
import com.ddudu.application.port.out.period_goal.SavePeriodGoalPort;
import com.ddudu.application.port.out.user.UserLoaderPort;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.PeriodGoalFixture;
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
class UpdatePeriodGoalServiceTest {

  @Autowired
  UpdatePeriodGoalService updatePeriodGoalService;
  @Autowired
  UserLoaderPort userLoaderPort;
  @Autowired
  PeriodGoalLoaderPort periodGoalLoaderPort;
  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SavePeriodGoalPort savePeriodGoalPort;

  User user;
  PeriodGoal periodGoal;
  UpdatePeriodGoalRequest request;
  String newContents;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    periodGoal = savePeriodGoalPort.save(PeriodGoalFixture.createRandomPeriodGoal(user));
    newContents = PeriodGoalFixture.getRandomSentenceWithMax(255);
    request = new UpdatePeriodGoalRequest(newContents);
  }

  @Test
  void 기간_목표를_수정할_수_있다() {
    // when
    Long updatedId = updatePeriodGoalService.update(user.getId(), periodGoal.getId(), request);

    // when
    PeriodGoal actual = periodGoalLoaderPort.getOrElseThrow(
        updatedId, "기간 목표가 존재하지 않습니다.");
    assertThat(actual)
        .hasFieldOrPropertyWithValue("contents", newContents);
  }

  @Test
  void 유효하지_않는_기간_목표인_경우_수정에_실패한다() {
    // given
    Long invalidId = GoalFixture.getRandomId();

    // when
    ThrowingCallable update = () -> updatePeriodGoalService.update(
        user.getId(), invalidId, request);

    // then
    assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(update)
        .withMessage(PeriodGoalErrorCode.PERIOD_GOAL_NOT_EXISTING.getCodeName());
  }

  @Test
  void 로그인_사용자가_권한이_없는_경우_수정에_실패한다() {
    // given
    User anotherUser = signUpPort.save(UserFixture.createRandomUserWithId());

    // when
    ThrowingCallable update = () -> updatePeriodGoalService.update(
        anotherUser.getId(), periodGoal.getId(), request);

    // then
    assertThatExceptionOfType(SecurityException.class).isThrownBy(update)
        .withMessage(PeriodGoalErrorCode.INVALID_AUTHORITY.getCodeName());
  }

}
