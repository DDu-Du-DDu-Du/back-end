package com.ddudu.application.service.period_goal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ddudu.application.domain.period_goal.domain.enums.PeriodGoalType;
import com.ddudu.application.domain.period_goal.exception.PeriodGoalErrorCode;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.period_goal.request.CreatePeriodGoalRequest;
import com.ddudu.application.port.out.auth.SignUpPort;
import com.ddudu.application.port.out.period_goal.SavePeriodGoalPort;
import com.ddudu.application.port.out.user.UserLoaderPort;
import com.ddudu.fixture.PeriodGoalFixture;
import com.ddudu.fixture.UserFixture;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
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
class CreatePeriodGoalServiceTest {

  @Autowired
  CreatePeriodGoalService createPeriodGoalService;
  @Autowired
  UserLoaderPort userLoaderPort;
  @Autowired
  SavePeriodGoalPort savePeriodGoalPort;
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
    assertThat(periodGoalId).isNotNull();
  }

  @Test
  void 사용자ID가_유효하지_않으면_예외가_발생한다() {
    // given
    Long invalidUserId = UserFixture.getRandomId();

    // when
    ThrowingCallable create = () -> createPeriodGoalService.create(invalidUserId, request);

    // then
    assertThatThrownBy(create)
        .isInstanceOf(MissingResourceException.class)
        .hasMessageContaining(PeriodGoalErrorCode.USER_NOT_EXISTING.getCodeName());
  }


}
