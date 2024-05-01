package com.ddudu.goal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.common.exception.ForbiddenException;
import com.ddudu.goal.domain.Goal;
import com.ddudu.goal.domain.GoalStatus;
import com.ddudu.goal.domain.PrivacyType;
import com.ddudu.goal.dto.requset.CreateGoalRequest;
import com.ddudu.goal.dto.requset.UpdateGoalRequest;
import com.ddudu.goal.dto.response.CreateGoalResponse;
import com.ddudu.goal.dto.response.GoalResponse;
import com.ddudu.goal.dto.response.GoalSummaryResponse;
import com.ddudu.goal.exception.GoalErrorCode;
import com.ddudu.persistence.dao.goal.GoalDao;
import com.ddudu.persistence.dao.user.UserDao;
import com.ddudu.user.domain.User;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import net.datafaker.Faker;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
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
  private GoalService goalService;

  @Autowired
  private GoalDao goalRepository;

  @Autowired
  private UserDao userRepository;

  @Autowired
  private EntityManager entityManager;

  private User user;
  private String name;
  private String color;
  private PrivacyType privacyType;

  @BeforeEach
  void setUp() {
    user = createUser();
    name = faker.lorem()
        .word();
    color = faker.color()
        .hex()
        .substring(1);
    privacyType = provideRandomPrivacy();
  }

  @Nested
  class 목표_생성_테스트 {

    @Test
    void 목표명_색상_공개_설정을_입력해_목표_생성에_성공한다() {
      // when
      CreateGoalRequest request = new CreateGoalRequest(name, color, privacyType);
      CreateGoalResponse expected = goalService.create(user.getId(), request);

      // then
      Optional<Goal> actual = goalRepository.findById(expected.id());
      assertThat(actual.get()).extracting("name", "color", "privacyType")
          .containsExactly(expected.name(), expected.color(), privacyType);
    }

    @Test
    void 목표_생성_시_ID가_자동_생성된다() {
      // given
      CreateGoalRequest request = new CreateGoalRequest(name, color, privacyType);

      // when
      CreateGoalResponse expected = goalService.create(user.getId(), request);

      // then
      Optional<Goal> actual = goalRepository.findById(expected.id());
      assertThat(actual.get()
          .getId()).isNotNull();
    }

    @Test
    void 목표_생성_시_목표_상태는_IN_PROGRESS가_된다() {
      // given
      CreateGoalRequest request = new CreateGoalRequest(name, color, privacyType);

      // when
      CreateGoalResponse expected = goalService.create(user.getId(), request);

      // then
      Optional<Goal> actual = goalRepository.findById(expected.id());
      assertThat(actual.get()
          .getStatus()).isEqualTo(GoalStatus.IN_PROGRESS);
    }

    @ParameterizedTest(name = "유효하지 않은 색상 : {0}")
    @NullAndEmptySource
    void 색상을_설정하지_않거나_빈_문자열이면_기본값이_적용된다(String invalidColor) {
      // given
      String defaultColor = "191919";

      CreateGoalRequest request = new CreateGoalRequest(
          name, invalidColor, privacyType);

      // when
      CreateGoalResponse expected = goalService.create(user.getId(), request);

      // then
      Optional<Goal> actual = goalRepository.findById(expected.id());
      assertThat(actual.get()
          .getColor()).isEqualTo(defaultColor);
    }

    @Test
    void 보기_설정을_설정하지_않으면_PRIVATE이_적용된다() {
      // given
      PrivacyType defaultPrivacyType = PrivacyType.PRIVATE;
      CreateGoalRequest request = new CreateGoalRequest(name, color, null);

      // when
      CreateGoalResponse expected = goalService.create(user.getId(), request);

      // then
      Optional<Goal> actual = goalRepository.findById(expected.id());
      assertThat(actual.get()
          .getPrivacyType()).isEqualTo(defaultPrivacyType);
    }

    @Test
    void 사용자ID가_유효하지_않으면_예외가_발생한다() {
      // given
      Long invalidUserId = faker.random()
          .nextLong();
      CreateGoalRequest request = new CreateGoalRequest(name, color, privacyType);

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
      Goal expected = createGoal(user, name);
      Long id = expected.getId();
      Long loginId = user.getId();

      // when
      GoalResponse actual = goalService.findById(loginId, id);

      // then
      assertThat(actual).extracting("id", "name", "status", "color", "privacyType")
          .containsExactly(
              id, expected.getName(), expected.getStatus(), expected.getColor(),
              expected.getPrivacyType()
          );
    }

    @Test
    void 삭제된_목표의_ID인_경우_조회에_실패한다() {
      // given
      Long loginId = user.getId();
      Goal goal = createGoal(user, name);

      goal.delete();
      flushAndClearPersistence();

      // when
      ThrowingCallable findById = () -> goalService.findById(loginId, goal.getId());

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(findById)
          .withMessage(GoalErrorCode.ID_NOT_EXISTING.getMessage());
    }

    @Test
    void 유효하지_않은_ID인_경우_조회에_실패한다() {
      // given
      Long invalidId = faker.random()
          .nextLong();
      Long loginId = user.getId();

      // when
      ThrowingCallable findById = () -> goalService.findById(loginId, invalidId);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(findById)
          .withMessage(GoalErrorCode.ID_NOT_EXISTING.getMessage());
    }

    @Test
    void 로그인_사용자가_권한이_없는_경우_조회에_실패한다() {
      // given
      Goal goal = createGoal(user, name);
      Long invalidLoginId = faker.random()
          .nextLong();

      // when
      ThrowingCallable findById = () -> goalService.findById(invalidLoginId, goal.getId());

      // then
      assertThatExceptionOfType(ForbiddenException.class).isThrownBy(findById)
          .withMessage(GoalErrorCode.INVALID_AUTHORITY.getMessage());
    }

  }

  @Nested
  class 전체_목표_조회_테스트 {

    @Test
    void 사용자의_전체_목표를_조회_할_수_있다() {
      // given
      List<Goal> expected = createGoals(user, List.of(name));
      Long loginId = user.getId();

      // when
      List<GoalSummaryResponse> actual = goalService.findAllByUser(loginId, user.getId());

      // then
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
    void 로그인_사용자가_권한이_없는_경우_조회에_실패한다() {
      // given
      Long invalidLoginId = faker.random()
          .nextLong();

      createGoals(user, List.of(name));

      // when
      ThrowingCallable findAllByUser = () -> goalService.findAllByUser(
          invalidLoginId, user.getId());

      // then
      assertThatExceptionOfType(ForbiddenException.class).isThrownBy(findAllByUser)
          .withMessage(GoalErrorCode.INVALID_AUTHORITY.getMessage());
    }

  }

  @Nested
  class 목표_수정_테스트 {

    String changedName;
    String changedColor;
    GoalStatus changedStatus;
    PrivacyType changedPrivacyType;

    @BeforeEach
    void setUp() {
      changedName = faker.lorem()
          .word();
      changedColor = faker.color()
          .hex()
          .substring(1);
      changedStatus = provideRandomStatus();
      changedPrivacyType = provideRandomPrivacy();
    }

    @Test
    void 목표를_수정_할_수_있다() {
      // given
      Goal goal = createGoal(user, name);
      Long loginId = user.getId();
      UpdateGoalRequest request = new UpdateGoalRequest(
          changedName, changedStatus, changedColor, changedPrivacyType);

      // when
      goalService.update(loginId, goal.getId(), request);

      // then
      Goal actual = goalRepository.findById(goal.getId())
          .get();
      assertThat(actual).extracting("name", "status", "color", "privacyType")
          .containsExactly(changedName, changedStatus, changedColor, changedPrivacyType);
    }

    @Test
    void 유효하지_않은_ID인_경우_수정에_실패한다() {
      // given
      createGoal(user, name);

      Long invalidId = faker.random()
          .nextLong();
      UpdateGoalRequest request = new UpdateGoalRequest(
          changedName, changedStatus, changedColor, changedPrivacyType);

      // when
      ThrowingCallable update = () -> goalService.update(user.getId(), invalidId, request);

      // then
      assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(update)
          .withMessage(GoalErrorCode.ID_NOT_EXISTING.getMessage());
    }

    @Test
    void 로그인_사용자가_권한이_없는_경우_수정에_실패한다() {
      // given
      Goal goal = createGoal(user, name);
      Long invalidLoginId = faker.random()
          .nextLong();
      UpdateGoalRequest request = new UpdateGoalRequest(
          changedName, changedStatus, changedColor, changedPrivacyType);

      // when
      ThrowingCallable update = () -> goalService.update(invalidLoginId, goal.getId(), request);

      // then
      assertThatExceptionOfType(ForbiddenException.class).isThrownBy(update)
          .withMessage(GoalErrorCode.INVALID_AUTHORITY.getMessage());
    }

    private static GoalStatus provideRandomStatus() {
      GoalStatus[] goalStatuses = {GoalStatus.IN_PROGRESS, GoalStatus.DONE};
      return goalStatuses[faker.random()
          .nextInt(goalStatuses.length)];
    }

  }

  @Nested
  class 목표_삭제_테스트 {

    @Test
    void 목표를_삭제_할_수_있다() {
      // given
      Long loginId = user.getId();
      Goal goal = createGoal(user, name);
      Optional<Goal> found = goalRepository.findById(goal.getId());
      assertThat(found).isNotEmpty();

      // when
      goalService.delete(loginId, goal.getId());
      flushAndClearPersistence();

      // then
      Optional<Goal> foundAfterDeleted = goalRepository.findById(goal.getId());
      assertThat(foundAfterDeleted).isEmpty();
    }

    @Test
    void 목표가_존재하지_않는_경우_예외를_발생시키지_않는다() {
      // given
      Long loginId = user.getId();
      Long invalidId = faker.random()
          .nextLong();

      // when
      Executable delete = () -> goalService.delete(loginId, invalidId);

      // then
      Assertions.assertDoesNotThrow(delete);
    }

    @Test
    void 목표가_이미_삭제된_경우_예외를_발생시키지_않는다() {
      // given
      Long loginId = user.getId();
      Goal goal = createGoal(user, name);

      goalService.delete(loginId, goal.getId());
      flushAndClearPersistence();

      // when
      Executable delete = () -> goalService.delete(loginId, goal.getId());

      // then
      Assertions.assertDoesNotThrow(delete);
      Optional<Goal> foundAfterDeleted = goalRepository.findById(goal.getId());
      assertThat(foundAfterDeleted).isEmpty();
    }

    @Test
    void 로그인_사용자가_권한이_없는_경우_삭제에_실패한다() {
      // given
      Goal goal = createGoal(user, name);
      Long invalidLoginId = faker.random()
          .nextLong();

      // when
      ThrowingCallable delete = () -> goalService.delete(invalidLoginId, goal.getId());

      // then
      assertThatExceptionOfType(ForbiddenException.class).isThrownBy(delete)
          .withMessage(GoalErrorCode.INVALID_AUTHORITY.getMessage());
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

  private void flushAndClearPersistence() {
    entityManager.flush();
    entityManager.clear();
  }

  private static PrivacyType provideRandomPrivacy() {
    PrivacyType[] privacyTypes = {PrivacyType.PRIVATE, PrivacyType.FOLLOWER, PrivacyType.PUBLIC};
    return privacyTypes[faker.random()
        .nextInt(privacyTypes.length)];
  }

}
