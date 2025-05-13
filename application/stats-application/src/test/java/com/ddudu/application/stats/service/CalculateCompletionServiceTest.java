package com.ddudu.application.stats.service;

import static com.ddudu.domain.planning.goal.aggregate.enums.PrivacyType.PRIVATE;
import static com.ddudu.domain.planning.goal.aggregate.enums.PrivacyType.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.stats.response.DduduCompletionResponse;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.ddudu.out.SaveDduduPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.common.exception.DduduErrorCode;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.repeatddudu.util.DayOfWeekUtil;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.DduduFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.MissingResourceException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
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
class CalculateCompletionServiceTest {

  @Autowired
  CalculateCompletionService calculateCompletionService;
  @Autowired
  SignUpPort signUpPort;
  @Autowired
  SaveGoalPort saveGoalPort;
  @Autowired
  SaveDduduPort saveDduduPort;

  LocalDate today;
  YearMonth thisMonth;
  User user;
  Goal privateGoal;
  Goal publicGoal;
  Ddudu privateDdudu;
  Ddudu publicDdudu;

  @BeforeEach
  void setUp() {
    today = LocalDate.now();
    thisMonth = YearMonth.now();
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    privateGoal = saveGoalPort.save(GoalFixture.createRandomGoalWithUserAndPrivacyType(
        user.getId(),
        PRIVATE
    ));
    publicGoal = saveGoalPort.save(GoalFixture.createRandomGoalWithUserAndPrivacyType(
        user.getId(),
        PUBLIC
    ));
    privateDdudu = saveDduduPort.save(DduduFixture.createRandomDduduWithGoal(privateGoal));
    publicDdudu = saveDduduPort.save(DduduFixture.createRandomDduduWithGoal(publicGoal));
  }

  @Test
  void 자신의_주간_할_일_달성률_조회를_성공한다() {
    // given
    LocalDate firstDayOfWeek = DayOfWeekUtil.getFirstDayOfWeek(today);
    int indexOfToday = (int) ChronoUnit.DAYS.between(firstDayOfWeek, today);

    // when
    List<DduduCompletionResponse> responses = calculateCompletionService.calculateWeekly(
        user.getId(),
        user.getId(),
        today
    );

    // then
    Assertions.assertThat(responses)
        .hasSize(7);
    assertThat(responses.get(indexOfToday))
        .extracting("date", "totalCount", "uncompletedCount")
        .containsExactly(today, 2, 2);
  }

  @Test
  void 다른_사용자의_주간_할_일_달성률_조회를_성공한다() {
    // given
    User anotherUser = signUpPort.save(UserFixture.createRandomUserWithId());
    LocalDate firstDayOfWeek = DayOfWeekUtil.getFirstDayOfWeek(today);
    int indexOfToday = (int) ChronoUnit.DAYS.between(firstDayOfWeek, today);

    // when
    List<DduduCompletionResponse> responses = calculateCompletionService.calculateWeekly(
        anotherUser.getId(),
        user.getId(),
        today
    );

    // then
    Assertions.assertThat(responses)
        .hasSize(7);
    assertThat(responses.get(indexOfToday)).extracting("date", "totalCount", "uncompletedCount")
        .containsExactly(today, 1, 1);
  }

  @Test
  void 자신의_월간_할_일_달성률_조회를_성공한다() {
    // given
    int indexOfToday = today.getDayOfMonth() - 1;

    // when
    List<DduduCompletionResponse> responses = calculateCompletionService.calculateMonthly(
        user.getId(),
        user.getId(),
        thisMonth
    );

    // then
    int days = thisMonth.atEndOfMonth()
        .getDayOfMonth();

    Assertions.assertThat(responses)
        .hasSize(days);
    assertThat(responses.get(indexOfToday))
        .extracting("date", "totalCount", "uncompletedCount")
        .containsExactly(today, 2, 2);
  }

  @Test
  void 다른_사용자의_월간_할_일_달성률_조회를_성공한다() {
    // given
    User anotherUser = signUpPort.save(UserFixture.createRandomUserWithId());
    int indexOfToday = today.getDayOfMonth() - 1;

    // when
    List<DduduCompletionResponse> responses = calculateCompletionService.calculateMonthly(
        anotherUser.getId(),
        user.getId(),
        thisMonth
    );

    // then
    int days = thisMonth.atEndOfMonth()
        .getDayOfMonth();

    Assertions.assertThat(responses)
        .hasSize(days);
    assertThat(responses.get(indexOfToday)).extracting("date", "totalCount", "uncompletedCount")
        .containsExactly(today, 1, 1);
  }

  @Test
  void 로그인_아이디가_존재하지_않아_주간_할_일_달성률_조회를_실패한다() {
    // given
    Long invalidLoginId = UserFixture.getRandomId();

    // when
    ThrowingCallable findWeeklyCompletions = () -> calculateCompletionService.calculateWeekly(
        invalidLoginId,
        user.getId(),
        today
    );

    // then
    AssertionsForClassTypes.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(findWeeklyCompletions)
        .withMessage(DduduErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
  }

  @Test
  void 사용자_아이디가_존재하지_않아_주간_할_일_달성률_조회를_실패한다() {
    // given
    Long invalidUserId = UserFixture.getRandomId();

    // when
    ThrowingCallable findWeeklyCompletions = () -> calculateCompletionService.calculateWeekly(
        user.getId(),
        invalidUserId,
        today
    );

    // then
    AssertionsForClassTypes.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(findWeeklyCompletions)
        .withMessage(DduduErrorCode.USER_NOT_EXISTING.getCodeName());
  }

  @Test
  void 로그인_아이디가_존재하지_않아_월간_할_일_달성률_조회를_실패한다() {
    // given
    Long invalidLoginId = UserFixture.getRandomId();

    // when
    ThrowingCallable findMonthlyCompletions = () -> calculateCompletionService.calculateMonthly(
        invalidLoginId,
        user.getId(),
        thisMonth
    );

    // then
    AssertionsForClassTypes.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(findMonthlyCompletions)
        .withMessage(DduduErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
  }

  @Test
  void 사용자_아이디가_존재하지_않아_월간_할_일_달성률_조회를_실패한다() {
    // given
    Long invalidUserId = UserFixture.getRandomId();

    // when
    ThrowingCallable findMonthlyCompletions = () -> calculateCompletionService.calculateMonthly(
        user.getId(),
        invalidUserId,
        thisMonth
    );

    // then
    AssertionsForClassTypes.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(findMonthlyCompletions)
        .withMessage(DduduErrorCode.USER_NOT_EXISTING.getCodeName());
  }

}
