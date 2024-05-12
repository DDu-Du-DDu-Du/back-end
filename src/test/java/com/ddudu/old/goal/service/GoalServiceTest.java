package com.ddudu.old.goal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.GoalStatus;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.goal.dto.response.GoalSummaryResponse;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.old.goal.domain.OldGoalRepository;
import com.ddudu.old.goal.dto.requset.UpdateGoalRequest;
import com.ddudu.old.goal.dto.response.GoalResponse;
import com.ddudu.old.user.domain.UserRepository;
import com.ddudu.presentation.api.exception.DataNotFoundException;
import com.ddudu.presentation.api.exception.ForbiddenException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class GoalServiceTest {

  static final Faker faker = new Faker();

  @Autowired
  private GoalService goalService;

  @Autowired
  private OldGoalRepository goalRepository;

  @Autowired
  private UserRepository userRepository;

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
      Goal goal = createGoal(user, name);
      goalRepository.delete(goal);
      flushAndClearPersistence();

      Long loginId = user.getId();

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
