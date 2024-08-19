package com.ddudu.application.service.goal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.GoalStatus;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.domain.goal.service.GoalDomainService;
import com.ddudu.application.domain.repeat_ddudu.domain.RepeatDdudu;
import com.ddudu.application.domain.repeat_ddudu.domain.enums.RepeatType;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.dto.goal.request.CreateGoalRequest;
import com.ddudu.application.dto.goal.request.CreateRepeatDduduRequestWithoutGoal;
import com.ddudu.application.dto.goal.response.GoalIdResponse;
import com.ddudu.application.port.out.auth.SignUpPort;
import com.ddudu.application.port.out.ddudu.DduduLoaderPort;
import com.ddudu.application.port.out.goal.GoalLoaderPort;
import com.ddudu.application.port.out.goal.SaveGoalPort;
import com.ddudu.application.port.out.repeat_ddudu.RepeatDduduLoaderPort;
import com.ddudu.application.port.out.user.UserLoaderPort;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import jakarta.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Optional;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class CreateGoalServiceTest {

  @Autowired
  CreateGoalService createGoalService;

  @Autowired
  GoalDomainService goalDomainService;

  @Autowired
  UserLoaderPort userLoaderPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  GoalLoaderPort goalLoaderPort;

  @Autowired
  RepeatDduduLoaderPort repeatDduduLoaderPort;

  @Autowired
  DduduLoaderPort dduduLoaderPort;

  CreateGoalRequest request;
  Long userId;
  String name;
  String color;
  PrivacyType privacyType;

  @BeforeEach
  void setUp() {
    User user = createAndSaveUser();
    userId = user.getId();
    name = GoalFixture.getRandomSentenceWithMax(50);
    color = GoalFixture.getRandomColor();
    privacyType = GoalFixture.getRandomPrivacyType();
    request = new CreateGoalRequest(name, color, privacyType.name(), new ArrayList<>());
  }

  @Test
  void 목표명_색상_공개_설정을_입력해_목표_생성에_성공한다() {
    // when
    GoalIdResponse expected = createGoalService.create(userId, request);

    // then
    Optional<Goal> actual = goalLoaderPort.getOptionalGoal(expected.id());
    assertThat(actual.get()).extracting("name", "color", "privacyType")
        .containsExactly(name, color, privacyType);
  }

  @Test
  void 목표_생성_시_ID가_자동_생성된다() {
    // when
    GoalIdResponse expected = createGoalService.create(userId, request);

    // then
    Optional<Goal> actual = goalLoaderPort.getOptionalGoal(expected.id());
    assertThat(actual.get()
        .getId()).isNotNull();
  }

  @Test
  void 목표_생성_시_목표_상태는_IN_PROGRESS가_된다() {
    // when
    GoalIdResponse expected = createGoalService.create(userId, request);

    // then
    Optional<Goal> actual = goalLoaderPort.getOptionalGoal(expected.id());
    assertThat(actual.get()
        .getStatus()).isEqualTo(GoalStatus.IN_PROGRESS);
  }

  @ParameterizedTest(name = "유효하지 않은 색상 : {0}")
  @NullAndEmptySource
  void 색상을_설정하지_않거나_빈_문자열이면_기본값이_적용된다(String invalidColor) {
    // given
    String defaultColor = "191919";

    CreateGoalRequest request = new CreateGoalRequest(
        name, invalidColor, privacyType.name(), new ArrayList<>());

    // when
    GoalIdResponse expected = createGoalService.create(userId, request);

    // then
    Optional<Goal> actual = goalLoaderPort.getOptionalGoal(expected.id());
    assertThat(actual.get()
        .getColor()).isEqualTo(defaultColor);
  }

  @Test
  void 보기_설정을_설정하지_않으면_PRIVATE이_적용된다() {
    // given
    PrivacyType defaultPrivacyType = PrivacyType.PRIVATE;
    CreateGoalRequest request = new CreateGoalRequest(name, color, null, new ArrayList<>());

    // when
    GoalIdResponse expected = createGoalService.create(userId, request);

    // then
    Optional<Goal> actual = goalLoaderPort.getOptionalGoal(expected.id());
    assertThat(actual.get()
        .getPrivacyType()).isEqualTo(defaultPrivacyType);
  }

  @Test
  void 사용자ID가_유효하지_않으면_예외가_발생한다() {
    // given
    Long invalidUserId = UserFixture.getRandomId();

    // when
    ThrowingCallable create = () -> createGoalService.create(invalidUserId, request);

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(create)
        .withMessage(GoalErrorCode.USER_NOT_EXISTING.getCodeName());
  }

  @Test
  void 목표_생성_시_반복_뚜두도_함께_생성할_수_있다() {
    // given
    LocalDate nextMonday = LocalDate.now()
        .with(DayOfWeek.MONDAY)
        .plusDays(7);
    LocalDate nextSunday = nextMonday.plusDays(6);
    List<CreateRepeatDduduRequestWithoutGoal> requests = List.of(
        new CreateRepeatDduduRequestWithoutGoal(
            "반복 뚜두",
            RepeatType.WEEKLY.name(),
            List.of(DayOfWeek.SUNDAY.name()),
            null,
            null,
            nextMonday,
            nextSunday,
            null,
            null
        )
    );

    CreateGoalRequest request = new CreateGoalRequest(name, color, null, requests);

    // when
    GoalIdResponse response = createGoalService.create(userId, request);

    // then
    Goal goal = goalLoaderPort.getOptionalGoal(response.id())
        .get();
    List<RepeatDdudu> repeatDdudus = repeatDduduLoaderPort.getAllByGoal(goal);
    assertThat(repeatDdudus).hasSize(1);
    assertThat(repeatDdudus.get(0))
        .extracting("name", "repeatType", "startDate", "endDate")
        .containsExactly("반복 뚜두", RepeatType.WEEKLY, nextMonday, nextSunday);

    List<Ddudu> ddudus = dduduLoaderPort.getRepeatedDdudus(repeatDdudus.get(0));
    assertThat(ddudus).hasSize(1);
    assertThat(ddudus.get(0))
        .extracting(ddudu -> ddudu.getScheduledOn()
            .getDayOfWeek())
        .isEqualTo(DayOfWeek.SUNDAY);
  }

  private User createAndSaveUser() {
    User user = UserFixture.createRandomUserWithId();
    return signUpPort.save(user);
  }

}
