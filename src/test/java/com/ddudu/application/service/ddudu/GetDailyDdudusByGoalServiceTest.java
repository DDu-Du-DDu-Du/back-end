package com.ddudu.application.service.ddudu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.dto.GoalGroupedDdudus;
import com.ddudu.application.domain.ddudu.exception.DduduErrorCode;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.port.out.auth.SignUpPort;
import com.ddudu.application.port.out.ddudu.SaveDduduPort;
import com.ddudu.application.port.out.goal.SaveGoalPort;
import com.ddudu.fixture.DduduFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import jakarta.transaction.Transactional;
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

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class GetDailyDdudusByGoalServiceTest {

  @Autowired
  GetDailyDdudusByGoalService getDailyDdudusByGoalService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveDduduPort saveDduduPort;

  User user;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
  }

  @Test
  void 주어진_날짜에_자신의_목표별_뚜두_리스트_조회를_성공한다() {
    // given
    Goal goal = saveGoalPort.save(
        GoalFixture.createRandomGoalWithUserAndPrivacyType(user, PrivacyType.PRIVATE));
    Ddudu ddudu = saveDduduPort.save(DduduFixture.createRandomDduduWithGoal(goal));

    LocalDate date = LocalDate.now();

    // when
    List<GoalGroupedDdudus> responses = getDailyDdudusByGoalService.get(
        user.getId(), user.getId(), date);

    // then
    assertThat(responses).hasSize(1);

    GoalGroupedDdudus firstElement = responses.get(0);
    assertThat(firstElement.goal()).extracting("id")
        .isEqualTo(goal.getId());
    assertThat(firstElement.ddudus()).extracting("id")
        .containsExactly(ddudu.getId());
  }

  @Test
  void 다른_사용자의_목표별_뚜두을_조회할_경우_전체공개_목표의_뚜두만_조회한다() {
    // given
    Goal publicGoal = saveGoalPort.save(
        GoalFixture.createRandomGoalWithUserAndPrivacyType(user, PrivacyType.PUBLIC));
    saveDduduPort.save(DduduFixture.createRandomDduduWithGoal(publicGoal));

    Goal privateGoal = saveGoalPort.save(
        GoalFixture.createRandomGoalWithUserAndPrivacyType(user, PrivacyType.PRIVATE));
    saveDduduPort.save(DduduFixture.createRandomDduduWithGoal(privateGoal));

    User anotherUser = signUpPort.save(UserFixture.createRandomUserWithId());

    LocalDate date = LocalDate.now();

    // when
    List<GoalGroupedDdudus> responses = getDailyDdudusByGoalService.get(
        anotherUser.getId(), user.getId(), date);

    // then
    assertThat(responses).hasSize(1);
    assertThat(responses.get(0)
        .goal()).extracting("id")
        .isEqualTo(publicGoal.getId());
  }


  @Test
  void 로그인_아이디가_존재하지_않아_목표별_뚜두_조회를_실패한다() {
    // given
    Long invalidLoginId = UserFixture.getRandomId();
    LocalDate date = LocalDate.now();

    // when
    ThrowingCallable findAllByDate = () -> getDailyDdudusByGoalService.get(
        invalidLoginId, user.getId(), date);

    // then
    assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(findAllByDate)
        .withMessage(DduduErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
  }

  @Test
  void 사용자_아이디가_존재하지_않아_목표별_뚜두_조회를_실패한다() {
    // given
    Long loginUserId = user.getId();
    Long invalidUserId = UserFixture.getRandomId();
    LocalDate date = LocalDate.now();

    // when
    ThrowingCallable findAllByDate = () -> getDailyDdudusByGoalService.get(
        loginUserId, invalidUserId, date);

    // then
    assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(findAllByDate)
        .withMessage(DduduErrorCode.USER_NOT_EXISTING.getCodeName());
  }

}
