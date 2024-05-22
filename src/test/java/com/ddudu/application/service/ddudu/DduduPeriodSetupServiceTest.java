package com.ddudu.application.service.ddudu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.dto.request.PeriodSetupRequest;
import com.ddudu.application.domain.ddudu.exception.DduduErrorCode;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.port.out.auth.SignUpPort;
import com.ddudu.application.port.out.ddudu.DduduLoaderPort;
import com.ddudu.application.port.out.ddudu.SaveDduduPort;
import com.ddudu.application.port.out.goal.SaveGoalPort;
import com.ddudu.fixture.DduduFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.time.LocalTime;
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

  @Autowired
  EntityManager entityManager;

  User user;
  Ddudu ddudu;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    Goal goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user));
    ddudu = saveDduduPort.save(DduduFixture.createRandomDduduWithGoal(goal));
  }

  @Test
  void 뚜두_시작_및_종료시간을_설정한다() {
    // given
    LocalTime now = LocalTime.now();
    PeriodSetupRequest request = new PeriodSetupRequest(now, now.plusHours(1));

    // when
    dduduPeriodSetupService.setUpPeriod(user.getId(), ddudu.getId(), request);
    entityManager.flush();

    // then
    Ddudu actual = dduduLoaderPort.getDduduOrElseThrow(ddudu.getId(), "not found");

    assertThat(actual.getBeginAt()).isEqualTo(now);
    assertThat(actual.getEndAt()).isEqualTo(now.plusHours(1));
  }

  @Test
  void 뚜두가_없으면_시간_설정을_실패한다() {
    // given
    LocalTime now = LocalTime.now();
    PeriodSetupRequest request = new PeriodSetupRequest(now, now.plusHours(1));
    long invalidId = DduduFixture.getRandomId();

    // when
    ThrowingCallable setUpPeriod = () -> dduduPeriodSetupService.setUpPeriod(
        user.getId(), invalidId, request);

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(setUpPeriod)
        .withMessage(DduduErrorCode.ID_NOT_EXISTING.getCodeName());
  }

}