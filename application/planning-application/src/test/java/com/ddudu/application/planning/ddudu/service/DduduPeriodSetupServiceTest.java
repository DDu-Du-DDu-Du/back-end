package com.ddudu.application.planning.ddudu.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.ddudu.request.PeriodSetupRequest;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.ddudu.out.DduduLoaderPort;
import com.ddudu.application.common.port.ddudu.out.SaveDduduPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.common.exception.DduduErrorCode;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.DduduFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import java.time.LocalTime;
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
class DduduPeriodSetupServiceTest {

  @Autowired
  PeriodSetupService dduduPeriodSetupService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveDduduPort saveDduduPort;

  @Autowired
  DduduLoaderPort dduduLoaderPort;

  User user;
  Ddudu ddudu;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    Goal goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    ddudu = saveDduduPort.save(DduduFixture.createRandomDduduWithGoal(goal));
  }

  @Test
  void 뚜두_시작_및_종료시간을_설정한다() {
    // given
    LocalTime now = LocalTime.now();
    PeriodSetupRequest request = new PeriodSetupRequest(now, LocalTime.MAX);

    // when
    dduduPeriodSetupService.setUpPeriod(user.getId(), ddudu.getId(), request);

    // then
    Ddudu actual = dduduLoaderPort.getDduduOrElseThrow(ddudu.getId(), "not found");

    assertThat(actual.getBeginAt()).isEqualTo(now);
    assertThat(actual.getEndAt()).isEqualTo(LocalTime.MAX);
  }

  @Test
  void 뚜두가_없으면_시간_설정을_실패한다() {
    // given
    LocalTime now = LocalTime.now();
    PeriodSetupRequest request = new PeriodSetupRequest(now, LocalTime.MAX);
    long invalidId = DduduFixture.getRandomId();

    // when
    ThrowingCallable setUpPeriod = () -> dduduPeriodSetupService.setUpPeriod(
        user.getId(),
        invalidId,
        request
    );

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(setUpPeriod)
        .withMessage(DduduErrorCode.ID_NOT_EXISTING.getCodeName());
  }

}
