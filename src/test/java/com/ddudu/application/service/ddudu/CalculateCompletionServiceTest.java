package com.ddudu.application.service.ddudu;

import static com.ddudu.application.domain.goal.domain.enums.PrivacyType.PRIVATE;
import static com.ddudu.application.domain.goal.domain.enums.PrivacyType.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.exception.DduduErrorCode;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.ddudu.response.DduduCompletionResponse;
import com.ddudu.application.port.out.auth.SignUpPort;
import com.ddudu.application.port.out.ddudu.SaveDduduPort;
import com.ddudu.application.port.out.goal.SaveGoalPort;
import com.ddudu.fixture.DduduFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import java.time.LocalDate;
import java.util.List;
import java.util.MissingResourceException;
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
  LocalDate afterOneWeek;
  User user;
  Goal privateGoal;
  Goal publicGoal;
  Ddudu privateDdudu;
  Ddudu publicDdudu;

  @BeforeEach
  void setUp() {
    today = LocalDate.now();
    afterOneWeek = today.plusDays(7);
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    privateGoal = saveGoalPort.save(
        GoalFixture.createRandomGoalWithUserAndPrivacyType(user, PRIVATE));
    publicGoal = saveGoalPort.save(
        GoalFixture.createRandomGoalWithUserAndPrivacyType(user, PUBLIC));
    privateDdudu = saveDduduPort.save(DduduFixture.createRandomDduduWithGoal(privateGoal));
    publicDdudu = saveDduduPort.save(DduduFixture.createRandomDduduWithGoal(publicGoal));
  }

  @Test
  void 특정_날짜_사이의_자신의_할_일_달성률_조회를_성공한다() {
    // given
    int indexOfToday = 0;

    // when
    List<DduduCompletionResponse> responses = calculateCompletionService.calculate(
        user.getId(), user.getId(), today, afterOneWeek);

    // then
    assertThat(responses).hasSize(7);
    assertThat(responses.get(indexOfToday))
        .extracting("date", "totalCount", "uncompletedCount")
        .containsExactly(today, 2, 2);
  }

  @Test
  void 특정_날짜_사이의_다른_사용자의_할_일_달성률_조회를_성공한다() {
    // given
    User anotherUser = signUpPort.save(UserFixture.createRandomUserWithId());
    int indexOfToday = 0;

    // when
    List<DduduCompletionResponse> responses = calculateCompletionService.calculate(
        anotherUser.getId(), user.getId(), today, afterOneWeek);

    // then
    assertThat(responses).hasSize(7);
    assertThat(responses.get(indexOfToday)).extracting("date", "totalCount", "uncompletedCount")
        .containsExactly(today, 1, 1);
  }

  @Test
  void 로그인_아이디가_존재하지_않아_주간_할_일_달성률_조회를_실패한다() {
    // given
    Long invalidLoginId = UserFixture.getRandomId();

    // when
    ThrowingCallable findWeeklyCompletions = () -> calculateCompletionService.calculate(
        invalidLoginId, user.getId(), today, afterOneWeek);

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(findWeeklyCompletions)
        .withMessage(DduduErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
  }

  @Test
  void 사용자_아이디가_존재하지_않아_주간_할_일_달성률_조회를_실패한다() {
    // given
    Long invalidUserId = UserFixture.getRandomId();

    // when
    ThrowingCallable findWeeklyCompletions = () -> calculateCompletionService.calculate(
        user.getId(), invalidUserId, today, afterOneWeek);

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(findWeeklyCompletions)
        .withMessage(DduduErrorCode.USER_NOT_EXISTING.getCodeName());
  }

}
