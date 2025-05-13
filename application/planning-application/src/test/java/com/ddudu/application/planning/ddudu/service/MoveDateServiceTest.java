package com.ddudu.application.planning.ddudu.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.ddudu.request.MoveDateRequest;
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
import java.time.LocalDate;
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
class MoveDateServiceTest {

  @Autowired
  MoveDateService moveDateService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveDduduPort saveDduduPort;

  @Autowired
  DduduLoaderPort dduduLoaderPort;

  User user;
  Goal goal;
  Ddudu ddudu;
  LocalDate tomorrow;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    ddudu = saveDduduPort.save(DduduFixture.createRandomDduduWithGoal(goal));
    tomorrow = LocalDate.now()
        .plusDays(1);
  }

  @Test
  void 뚜두를_미루기_한다() {
    // given
    MoveDateRequest request = new MoveDateRequest(tomorrow);

    // when
    moveDateService.moveDate(user.getId(), ddudu.getId(), request);

    // then
    Ddudu actual = dduduLoaderPort.getDduduOrElseThrow(ddudu.getId(), "not found");

    assertThat(actual.getScheduledOn()).isEqualTo(tomorrow);
    assertThat(actual.isPostponed()).isTrue();
  }

  @Test
  void 뚜두를_오늘_다시_하기_한다() {
    // given
    LocalDate yesterday = LocalDate.now()
        .minusDays(1);
    Ddudu pastDdudu = saveDduduPort.save(DduduFixture.createRandomDduduWithSchedule(
        goal,
        yesterday
    ));
    MoveDateRequest request = new MoveDateRequest(LocalDate.now());

    // when
    moveDateService.moveDate(user.getId(), pastDdudu.getId(), request);

    // then
    Ddudu actual = dduduLoaderPort.getDduduOrElseThrow(pastDdudu.getId(), "not found");

    assertThat(actual.getScheduledOn()).isEqualTo(LocalDate.now());
  }

  @Test
  void 완료한_지난_뚜두의_날짜를_바꾼다() {
    // given
    LocalDate twoDaysAgo = LocalDate.now()
        .minusDays(2);
    Ddudu pastDdudu = saveDduduPort.save(DduduFixture.createRandomDduduWithSchedule(
        goal,
        twoDaysAgo
    ));
    LocalDate yesterday = LocalDate.now()
        .minusDays(1);
    MoveDateRequest request = new MoveDateRequest(yesterday);

    // when
    moveDateService.moveDate(user.getId(), pastDdudu.getId(), request);

    // then
    Ddudu actual = dduduLoaderPort.getDduduOrElseThrow(pastDdudu.getId(), "not found");

    assertThat(actual.getScheduledOn()).isEqualTo(yesterday);
  }

  @Test
  void 뚜두가_존재하지_않으면_날짜_변경을_실패한다() {
    // given
    long invalidId = DduduFixture.getRandomId();
    MoveDateRequest request = new MoveDateRequest(tomorrow);

    // when
    ThrowingCallable moveDate = () -> moveDateService.moveDate(user.getId(), invalidId, request);

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(moveDate)
        .withMessage(DduduErrorCode.ID_NOT_EXISTING.getCodeName());
  }

}
