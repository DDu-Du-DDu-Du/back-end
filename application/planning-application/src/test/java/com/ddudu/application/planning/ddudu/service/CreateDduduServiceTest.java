package com.ddudu.application.planning.ddudu.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.ddudu.request.CreateDduduRequest;
import com.ddudu.application.common.dto.ddudu.response.BasicDduduResponse;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.ddudu.out.DduduLoaderPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.common.exception.DduduErrorCode;
import com.ddudu.common.exception.GoalErrorCode;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.ddudu.aggregate.enums.DduduStatus;
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
class CreateDduduServiceTest {

  @Autowired
  CreateDduduService createDduduService;

  @Autowired
  DduduLoaderPort dduduLoaderPort;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  User user;
  Goal goal;
  String name;
  LocalDate scheduledOn;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    name = DduduFixture.getRandomSentenceWithMax(50);
    scheduledOn = LocalDate.now();
  }


  @Test
  void 할_일_생성에_성공한다() {
    // given
    CreateDduduRequest request = new CreateDduduRequest(goal.getId(), name, scheduledOn);

    // when
    BasicDduduResponse response = createDduduService.create(user.getId(), request);

    // then
    Ddudu actual = dduduLoaderPort.getDduduOrElseThrow(response.id(), "할 일이 생성되지 않았습니다.");
    assertThat(actual).extracting(
            "name",
            "scheduledOn",
            "goalId",
            "userId",
            "status",
            "isPostponed"
        )
        .containsExactly(
            name, scheduledOn, goal.getId(), user.getId(), DduduStatus.UNCOMPLETED, false);
  }

  @Test
  void 날짜를_설정하지_않은_경우_기본값이_적용된다() {
    // given
    CreateDduduRequest request = new CreateDduduRequest(goal.getId(), name, null);

    // when
    BasicDduduResponse response = createDduduService.create(user.getId(), request);

    // then
    Ddudu actual = dduduLoaderPort.getDduduOrElseThrow(response.id(), "할 일이 생성되지 않았습니다.");
    assertThat(actual.getScheduledOn()).isEqualTo(LocalDate.now());
  }

  @Test
  void 사용자_아이디가_유효하지_않으면_예외가_발생한다() {
    // give
    Long invalidUserId = UserFixture.getRandomId();
    CreateDduduRequest request = new CreateDduduRequest(goal.getId(), name, scheduledOn);

    // when
    ThrowingCallable create = () -> createDduduService.create(invalidUserId, request);

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(create)
        .withMessage(DduduErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
  }

  @Test
  void 목표_아이디가_유효하지_않으면_예외가_발생한다() {
    // given
    Long invalidGoalId = GoalFixture.getRandomId();
    CreateDduduRequest request = new CreateDduduRequest(invalidGoalId, name, scheduledOn);

    // when
    ThrowingCallable create = () -> createDduduService.create(user.getId(), request);

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(create)
        .withMessage(DduduErrorCode.GOAL_NOT_EXISTING.getCodeName());
  }

  @Test
  void 본인의_목표가_아닌_경우_예외가_발생한다() {
    // given
    User anotherUser = signUpPort.save(UserFixture.createRandomUserWithId());
    Goal goalOfAnotherUser = saveGoalPort.save(
        GoalFixture.createRandomGoalWithUser(
            anotherUser.getId()
        )
    );
    CreateDduduRequest request = new CreateDduduRequest(
        goalOfAnotherUser.getId(),
        name,
        scheduledOn
    );

    // when
    ThrowingCallable create = () -> createDduduService.create(user.getId(), request);

    // then
    Assertions.assertThatExceptionOfType(SecurityException.class)
        .isThrownBy(create)
        .withMessage(GoalErrorCode.INVALID_AUTHORITY.getCodeName());
  }

}
