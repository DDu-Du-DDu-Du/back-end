package com.ddudu.application.planning.periodgoal.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.periodgoal.request.UpdatePeriodGoalRequest;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.periodgoal.out.PeriodGoalLoaderPort;
import com.ddudu.application.common.port.periodgoal.out.SavePeriodGoalPort;
import com.ddudu.common.exception.PeriodGoalErrorCode;
import com.ddudu.domain.planning.periodgoal.aggregate.PeriodGoal;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.PeriodGoalFixture;
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
class UpdatePeriodGoalServiceTest {

  @Autowired
  UpdatePeriodGoalService updatePeriodGoalService;

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
    periodGoal = savePeriodGoalPort.save(PeriodGoalFixture.createRandomPeriodGoal(user.getId()));
    newContents = PeriodGoalFixture.getRandomSentenceWithMax(255);
    request = new UpdatePeriodGoalRequest(newContents);
  }

  @Test
  void 기간_목표를_수정할_수_있다() {
    // when
    Long updatedId = updatePeriodGoalService.update(user.getId(), periodGoal.getId(), request);

    // when
    PeriodGoal actual = periodGoalLoaderPort.getOrElseThrow(updatedId, "기간 목표가 존재하지 않습니다.");

    assertThat(actual)
        .hasFieldOrPropertyWithValue("contents", newContents);
  }

  @Test
  void 유효하지_않는_기간_목표인_경우_수정에_실패한다() {
    // given
    Long invalidId = GoalFixture.getRandomId();

    // when
    ThrowingCallable update = () -> updatePeriodGoalService.update(
        user.getId(),
        invalidId,
        request
    );

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(update)
        .withMessage(PeriodGoalErrorCode.PERIOD_GOAL_NOT_EXISTING.getCodeName());
  }

  @Test
  void 로그인_사용자가_권한이_없는_경우_수정에_실패한다() {
    // given
    User anotherUser = signUpPort.save(UserFixture.createRandomUserWithId());

    // when
    ThrowingCallable update = () -> updatePeriodGoalService.update(
        anotherUser.getId(),
        periodGoal.getId(),
        request
    );

    // then
    Assertions.assertThatExceptionOfType(SecurityException.class)
        .isThrownBy(update)
        .withMessage(PeriodGoalErrorCode.INVALID_AUTHORITY.getCodeName());
  }

}
