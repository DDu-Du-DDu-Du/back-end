package com.ddudu.application.service.ddudu;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.exception.DduduErrorCode;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.ddudu.DduduForTimetable;
import com.ddudu.application.dto.ddudu.response.TimetableResponse;
import com.ddudu.application.port.out.auth.SignUpPort;
import com.ddudu.application.port.out.ddudu.SaveDduduPort;
import com.ddudu.application.port.out.goal.SaveGoalPort;
import com.ddudu.fixture.DduduFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
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
class GetDailyDdudusByTimeServiceTest {

  @Autowired
  GetTimetableService getTimetableService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveDduduPort saveDduduPort;

  User user;
  LocalTime beginAt;
  LocalTime endAt;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    beginAt = LocalTime.of(10, 00);
    endAt = LocalTime.of(11, 00);
  }

  @Test
  void 주어진_날짜에_자신의_시간별_뚜두_리스트_조회를_성공한다() {
    // given
    Goal goal = saveGoalPort.save(
        GoalFixture.createRandomGoalWithUserAndPrivacyType(user, PrivacyType.PRIVATE));
    Ddudu ddudu = saveDduduPort.save(
        DduduFixture.createRandomDduduWithGoalAndTime(goal, beginAt, endAt));

    LocalDate date = LocalDate.now();

    // when
    TimetableResponse response = getTimetableService.get(
        user.getId(), user.getId(), date);

    // then
    int countOfTime = response.timetable()
        .size();
    LocalTime earliestTime = response.timetable()
        .get(0)
        .beginAt();
    DduduForTimetable firstOfEarliestTime = response.timetable()
        .get(0)
        .ddudus()
        .get(0);

    assertThat(countOfTime).isEqualTo(1);
    assertThat(earliestTime).isEqualTo(beginAt);
    assertThat(firstOfEarliestTime.id()).isEqualTo(ddudu.getId());
    assertThat(response.unassignedDdudus()
        .get(0)
        .ddudus()
        .size()).isEqualTo(0);
  }

  @Test
  void 시간이_설정되지_않은_뚜두_조회에_성공한다() {
    // given
    Goal goal = saveGoalPort.save(
        GoalFixture.createRandomGoalWithUserAndPrivacyType(user, PrivacyType.PRIVATE));
    Ddudu ddudu = saveDduduPort.save(
        DduduFixture.createRandomDduduWithGoal(goal));

    LocalDate date = LocalDate.now();

    // when
    TimetableResponse response = getTimetableService.get(
        user.getId(), user.getId(), date);

    // then
    assertThat(response.unassignedDdudus()
        .get(0)
        .ddudus()
        .size()).isEqualTo(1);
    assertThat(response.unassignedDdudus()
        .get(0)
        .ddudus()
        .get(0)
        .id()).isEqualTo(ddudu.getId());
  }

  @Test
  void 다른_사용자의_시간별_뚜두_리스트를_조회할_경우_전체공개_목표의_뚜두만_조회한다() {
    // given
    Goal publicGoal = saveGoalPort.save(
        GoalFixture.createRandomGoalWithUserAndPrivacyType(user, PrivacyType.PUBLIC));
    Ddudu publicDdudu = saveDduduPort.save(
        DduduFixture.createRandomDduduWithGoalAndTime(publicGoal, beginAt, endAt));

    Goal privateGoal = saveGoalPort.save(
        GoalFixture.createRandomGoalWithUserAndPrivacyType(user, PrivacyType.PRIVATE));
    saveDduduPort.save(
        DduduFixture.createRandomDduduWithGoalAndTime(privateGoal, beginAt, endAt));

    User anotherUser = signUpPort.save(UserFixture.createRandomUserWithId());

    LocalDate date = LocalDate.now();

    // when
    TimetableResponse response = getTimetableService.get(
        anotherUser.getId(), user.getId(), date);

    // then
    int countOfTime = response.timetable()
        .size();
    DduduForTimetable firstOfEarliestTime = response.timetable()
        .get(0)
        .ddudus()
        .get(0);

    assertThat(countOfTime).isEqualTo(1);
    assertThat(firstOfEarliestTime.id()).isEqualTo(publicDdudu.getId());
  }

  @Test
  void 로그인_아이디가_존재하지_않아_목표별_뚜두_조회를_실패한다() {
    // given
    Long invalidLoginId = UserFixture.getRandomId();
    LocalDate date = LocalDate.now();

    // when
    ThrowingCallable findAllByDate = () -> getTimetableService.get(
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
    ThrowingCallable findAllByDate = () -> getTimetableService.get(
        loginUserId, invalidUserId, date);

    // then
    assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(findAllByDate)
        .withMessage(DduduErrorCode.USER_NOT_EXISTING.getCodeName());
  }

}
