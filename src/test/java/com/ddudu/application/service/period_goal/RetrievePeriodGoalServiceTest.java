package com.ddudu.application.service.period_goal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.ddudu.application.domain.period_goal.domain.PeriodGoal;
import com.ddudu.application.domain.period_goal.domain.enums.PeriodGoalType;
import com.ddudu.application.domain.period_goal.exception.PeriodGoalErrorCode;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.period_goal.response.PeriodGoalSummary;
import com.ddudu.application.port.out.auth.SignUpPort;
import com.ddudu.application.port.out.period_goal.PeriodGoalLoaderPort;
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
class RetrievePeriodGoalServiceTest {

  @Autowired
  RetrievePeriodGoalService retrievePeriodGoalService;
  @Autowired
  UserLoaderPort userLoaderPort;
  @Autowired
  SignUpPort signUpPort;
  @Autowired
  SavePeriodGoalPort savePeriodGoalPort;
  @Autowired
  PeriodGoalLoaderPort periodGoalLoaderPort;

  User user;
  String contents;
  PeriodGoalType type;
  LocalDate date;
  PeriodGoal periodGoal;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    contents = PeriodGoalFixture.getRandomSentenceWithMax(255);
    type = PeriodGoalFixture.getRandomType();
    date = LocalDate.now();
    periodGoal = savePeriodGoalPort.save(
        PeriodGoalFixture.createPeriodGoal(user, contents, type, date));
  }

  @Test
  void 기간_목표_조회를_할_수_있다() {
    // when
    PeriodGoalSummary periodGoalSummary = retrievePeriodGoalService.retrieve(
        user.getId(), date, type.name());

    // then
    assertThat(periodGoalSummary)
        .hasFieldOrPropertyWithValue("id", periodGoal.getId())
        .hasFieldOrPropertyWithValue("contents", periodGoal.getContents())
        .hasFieldOrPropertyWithValue("type", periodGoal.getType());
  }

  @Test
  void 해당_날짜에_기간_목표가_존재하지_않는_경우_빈_응답을_반환한다() {
    // given
    LocalDate nextMonth = LocalDate.now()
        .plusMonths(1);

    // when
    PeriodGoalSummary periodGoalSummary = retrievePeriodGoalService.retrieve(
        user.getId(),
        nextMonth,
        type.name()
    );

    // then
    assertThat(periodGoalSummary)
        .hasFieldOrPropertyWithValue("id", null)
        .hasFieldOrPropertyWithValue("contents", null)
        .hasFieldOrPropertyWithValue("type", null);
  }

  @Test
  void 사용자ID가_유효하지_않으면_예외가_발생한다() {
    // given
    Long invalidUserId = UserFixture.getRandomId();

    // when
    ThrowingCallable retrieve = () -> retrievePeriodGoalService.retrieve(
        invalidUserId, date, type.name());

    // then
    assertThatThrownBy(retrieve)
        .isInstanceOf(MissingResourceException.class)
        .hasMessageContaining(PeriodGoalErrorCode.USER_NOT_EXISTING.getCodeName());
  }

}
