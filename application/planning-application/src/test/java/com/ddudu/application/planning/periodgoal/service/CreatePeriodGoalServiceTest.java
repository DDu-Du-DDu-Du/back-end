package com.ddudu.application.planning.periodgoal.service;

import com.ddudu.application.common.dto.periodgoal.request.CreatePeriodGoalRequest;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.common.exception.PeriodGoalErrorCode;
import com.ddudu.domain.planning.periodgoal.aggregate.enums.PeriodGoalType;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.PeriodGoalFixture;
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
class CreatePeriodGoalServiceTest {

  @Autowired
  CreatePeriodGoalService createPeriodGoalService;

  @Autowired
  SignUpPort signUpPort;

  User user;
  String contents;
  PeriodGoalType type;
  LocalDate planDate;
  CreatePeriodGoalRequest request;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    contents = PeriodGoalFixture.getRandomSentenceWithMax(255);
    type = PeriodGoalFixture.getRandomType();
    planDate = LocalDate.now();
    request = new CreatePeriodGoalRequest(contents, type.name(), planDate);
  }

  @Test
  void 기간_목표를_생성할_수_있다() {
    // when
    Long periodGoalId = createPeriodGoalService.create(user.getId(), request);

    // then
    Assertions.assertThat(periodGoalId)
        .isNotNull();
  }

  @Test
  void 사용자ID가_유효하지_않으면_예외가_발생한다() {
    // given
    Long invalidUserId = UserFixture.getRandomId();

    // when
    ThrowingCallable create = () -> createPeriodGoalService.create(invalidUserId, request);

    // then
    Assertions.assertThatThrownBy(create)
        .isInstanceOf(MissingResourceException.class)
        .hasMessageContaining(PeriodGoalErrorCode.USER_NOT_EXISTING.getCodeName());
  }


}
