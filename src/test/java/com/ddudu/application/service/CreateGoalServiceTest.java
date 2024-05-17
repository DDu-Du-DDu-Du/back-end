package com.ddudu.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.GoalStatus;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.goal.dto.request.CreateGoalRequest;
import com.ddudu.application.domain.goal.dto.response.GoalIdResponse;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.domain.goal.service.GoalDomainService;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.port.out.goal.GoalLoaderPort;
import com.ddudu.application.port.out.goal.SaveGoalPort;
import com.ddudu.application.port.out.SignUpPort;
import com.ddudu.application.port.out.UserLoaderPort;
import com.ddudu.application.service.goal.CreateGoalService;
import com.ddudu.fixture.BaseFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import jakarta.transaction.Transactional;
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

  CreateGoalRequest request;
  Long userId;
  String name;
  String color;
  PrivacyType privacyType;

  @BeforeEach
  void setUp() {
    User user = createAndSaveUser();
    userId = user.getId();
    name = BaseFixture.getRandomSentenceWithMax(50);
    color = BaseFixture.getRandomColor();
    privacyType = GoalFixture.getRandomPrivacyType();
    request = new CreateGoalRequest(name, color, privacyType.name());
  }

  @Test
  void 목표명_색상_공개_설정을_입력해_목표_생성에_성공한다() {
    // when
    GoalIdResponse expected = createGoalService.create(userId, request);

    // then
    Optional<Goal> actual = goalLoaderPort.findById(expected.id());
    assertThat(actual.get()).extracting("name", "color", "privacyType")
        .containsExactly(name, color, privacyType);
  }

  @Test
  void 목표_생성_시_ID가_자동_생성된다() {
    // when
    GoalIdResponse expected = createGoalService.create(userId, request);

    // then
    Optional<Goal> actual = goalLoaderPort.findById(expected.id());
    assertThat(actual.get()
        .getId()).isNotNull();
  }

  @Test
  void 목표_생성_시_목표_상태는_IN_PROGRESS가_된다() {
    // when
    GoalIdResponse expected = createGoalService.create(userId, request);

    // then
    Optional<Goal> actual = goalLoaderPort.findById(expected.id());
    assertThat(actual.get()
        .getStatus()).isEqualTo(GoalStatus.IN_PROGRESS);
  }

  @ParameterizedTest(name = "유효하지 않은 색상 : {0}")
  @NullAndEmptySource
  void 색상을_설정하지_않거나_빈_문자열이면_기본값이_적용된다(String invalidColor) {
    // given
    String defaultColor = "191919";

    CreateGoalRequest request = new CreateGoalRequest(
        name, invalidColor, privacyType.name());

    // when
    GoalIdResponse expected = createGoalService.create(userId, request);

    // then
    Optional<Goal> actual = goalLoaderPort.findById(expected.id());
    assertThat(actual.get()
        .getColor()).isEqualTo(defaultColor);
  }

  @Test
  void 보기_설정을_설정하지_않으면_PRIVATE이_적용된다() {
    // given
    PrivacyType defaultPrivacyType = PrivacyType.PRIVATE;
    CreateGoalRequest request = new CreateGoalRequest(name, color, null);

    // when
    GoalIdResponse expected = createGoalService.create(userId, request);

    // then
    Optional<Goal> actual = goalLoaderPort.findById(expected.id());
    assertThat(actual.get()
        .getPrivacyType()).isEqualTo(defaultPrivacyType);
  }

  @Test
  void 사용자ID가_유효하지_않으면_예외가_발생한다() {
    // given
    Long invalidUserId = BaseFixture.getRandomId();

    // when
    ThrowingCallable create = () -> createGoalService.create(invalidUserId, request);

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(create)
        .withMessage(GoalErrorCode.USER_NOT_EXISTING.getCodeName());
  }

  private User createAndSaveUser() {
    User user = UserFixture.createRandomUserWithId();
    return signUpPort.save(user);
  }

}
