package com.ddudu.goal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.goal.domain.Goal;
import com.ddudu.goal.domain.GoalStatus;
import com.ddudu.goal.domain.PrivacyType;
import com.ddudu.goal.dto.requset.CreateGoalRequest;
import com.ddudu.goal.dto.requset.UpdateGoalRequest;
import com.ddudu.goal.dto.response.CreateGoalResponse;
import com.ddudu.goal.dto.response.GoalResponse;
import com.ddudu.goal.dto.response.GoalSummaryResponse;
import com.ddudu.goal.exception.GoalErrorCode;
import com.ddudu.goal.repository.GoalRepository;
import com.ddudu.user.domain.User;
import com.ddudu.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import net.datafaker.Faker;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class GoalServiceTest {

  static final Faker faker = new Faker();

  @Autowired
  GoalService goalService;

  @Autowired
  GoalRepository goalRepository;

  @Autowired
  UserRepository userRepository;

  User user;
  String validName;
  String validColor;

  @BeforeEach
  void setUp() {
    user = createUser();
    validName = faker.lorem()
        .word();
    validColor = faker.color()
        .hex()
        .substring(1);
  }

  @Nested
  class 목표_생성_테스트 {

    @Test
    void 목표를_생성할_수_있다() {
      // given
      CreateGoalRequest request = new CreateGoalRequest(validName, validColor, PrivacyType.PUBLIC);

      // when
      CreateGoalResponse expected = goalService.create(user.getId(), request);

      // then
      Optional<Goal> actual = goalRepository.findById(expected.id());
      assertThat(actual).isNotEmpty();
      assertThat(actual.get()).extracting("name", "color")
          .containsExactly(validName, validColor);
    }

    @Test
    void 목표_생성_시_ID가_자동_생성된다() {
      // given
      CreateGoalRequest request = new CreateGoalRequest(validName, validColor, PrivacyType.PUBLIC);

      // when
      CreateGoalResponse expected = goalService.create(user.getId(), request);

      // then
      Optional<Goal> actual = goalRepository.findById(expected.id());
      assertThat(actual).isNotEmpty();
      assertThat(actual.get()
          .getId()).isNotNull();
    }

    @Test
    void 목표_생성_시_IN_PROGRESS_상태가_된다() {
      // given
      CreateGoalRequest request = new CreateGoalRequest(validName, validColor, PrivacyType.PUBLIC);

      // when
      CreateGoalResponse expected = goalService.create(user.getId(), request);

      // then
      Optional<Goal> actual = goalRepository.findById(expected.id());
      assertThat(actual).isNotEmpty();
      assertThat(actual.get()
          .getStatus()).isEqualTo(GoalStatus.IN_PROGRESS);
    }

    @ParameterizedTest(name = "유효하지 않은 색상 : {0}")
    @NullAndEmptySource
    void 색상을_설정하지_않거나_빈_문자열이면_기본값이_적용된다(String invalidColor) {
      // given
      String defaultColor = "191919";

      CreateGoalRequest request = new CreateGoalRequest(
          validName, invalidColor, PrivacyType.PUBLIC);

      // when
      CreateGoalResponse expected = goalService.create(user.getId(), request);

      // then
      Optional<Goal> actual = goalRepository.findById(expected.id());
      assertThat(actual).isNotEmpty();
      assertThat(actual.get()
          .getColor()).isEqualTo(defaultColor);
    }

    @Test
    void 보기_설정을_설정하지_않으면_PRIVATE이_적용된다() {
      // given
      CreateGoalRequest request = new CreateGoalRequest(validName, validColor, null);

      // when
      CreateGoalResponse expected = goalService.create(user.getId(), request);

      // then
      Optional<Goal> actual = goalRepository.findById(expected.id());
      assertThat(actual).isNotEmpty();
      assertThat(actual.get()
          .getPrivacyType()).isEqualTo(PrivacyType.PRIVATE);
    }

    @Test
    void 사용자ID가_유효하지_않으면_예외가_발생한다() {
      // given
      Long invalidUserId = 1234567890L;
      CreateGoalRequest request = new CreateGoalRequest(validName, validColor, null);

      // when
      ThrowingCallable create = () -> goalService.create(invalidUserId, request);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(create)
          .withMessage(GoalErrorCode.USER_NOT_EXISTING.getMessage());
    }

  }

  @Nested
  class 단일_목표_조회_테스트 {

    @Test
    void ID를_통해_목표를_조회_할_수_있다() {
      // given
      Goal expected = createGoal(user, validName);
      Long id = expected.getId();

      // when
      GoalResponse actual = goalService.getById(id);

      // then
      GoalStatus expectedStatus = expected.getStatus();
      PrivacyType expectedPrivacyType = expected.getPrivacyType();

      assertThat(actual).extracting("id", "name", "status", "color", "privacyType")
          .containsExactly(
              id, expected.getName(), expectedStatus, expected.getColor(), expectedPrivacyType);
    }

    @Test
    void 유효하지_않은_ID인_경우_조회에_실패한다() {
      // given
      Long invalidId = -1L;

      // when
      ThrowingCallable getGoal = () -> goalService.getById(invalidId);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(getGoal)
          .withMessage(GoalErrorCode.ID_NOT_EXISTING.getMessage());
    }

  }

  @Nested
  class 전체_목표_조회_테스트 {

    @Test
    void 사용자의_전체_목표를_조회_할_수_있다() {
      // given
      List<Goal> expected = createGoals(user, List.of(validName));

      // when
      List<GoalSummaryResponse> actual = goalService.getAllById(user.getId());

      // then
      assertThat(actual).isNotEmpty();
      assertThat(actual.size()).isEqualTo(expected.size());
      assertThat(actual.get(0))
          .extracting("id", "name", "status", "color")
          .containsExactly(expected.get(0)
              .getId(), expected.get(0)
              .getName(), expected.get(0)
              .getStatus()
              .name(), expected.get(0)
              .getColor());
    }

    @Test
    void 유효하지_않은_사용자_ID인_경우_조회에_실패한다() {
      // given
      Long invalidUserId = 1234567890L;

      // when
      ThrowingCallable getGoals = () -> goalService.getAllById(invalidUserId);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(getGoals)
          .withMessage(GoalErrorCode.USER_NOT_EXISTING.getMessage());
    }

  }

  @Nested
  class 목표_수정_테스트 {

    @Test
    void 목표를_수정_할_수_있다() {
      // given
      Goal goal = createGoal(user, validName);

      String changedName = "데브 코스";
      String changedColor = "999999";
      GoalStatus changedStatus = GoalStatus.DONE;
      PrivacyType changedPrivacyType = PrivacyType.PUBLIC;

      UpdateGoalRequest request = new UpdateGoalRequest(
          changedName, changedStatus, changedColor, changedPrivacyType);

      // when
      goalService.update(goal.getId(), request);

      // then
      Goal actual = goalRepository.findById(goal.getId())
          .get();
      assertThat(actual).extracting("name", "status", "color", "privacyType")
          .containsExactly(changedName, changedStatus, changedColor, changedPrivacyType);
    }

  }

  private List<Goal> createGoals(User user, List<String> names) {
    return names.stream()
        .map(name -> createGoal(user, name))
        .toList();
  }

  private Goal createGoal(User user, String name) {
    Goal goal = Goal.builder()
        .name(name)
        .user(user)
        .build();

    return goalRepository.save(goal);
  }

  private User createUser() {
    String email = faker.internet()
        .emailAddress();
    String password = faker.internet()
        .password(8, 40, true, true, true);
    String nickname = faker.oscarMovie()
        .character();

    User user = User.builder()
        .passwordEncoder(new BCryptPasswordEncoder())
        .email(email)
        .password(password)
        .nickname(nickname)
        .build();

    return userRepository.save(user);
  }

}
