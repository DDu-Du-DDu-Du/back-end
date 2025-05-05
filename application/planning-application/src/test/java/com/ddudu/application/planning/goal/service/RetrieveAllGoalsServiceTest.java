package com.ddudu.application.planning.goal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.goal.exception.GoalErrorCode;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.application.planning.goal.dto.response.BasicGoalResponse;
import com.ddudu.application.user.auth.port.out.SignUpPort;
import com.ddudu.application.planning.goal.port.out.GoalLoaderPort;
import com.ddudu.application.planning.goal.port.out.SaveGoalPort;
import com.ddudu.application.user.user.port.out.UserLoaderPort;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import jakarta.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.stream.IntStream;
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
class RetrieveAllGoalsServiceTest {

  @Autowired
  RetrieveAllGoalsService retrieveAllGoalsService;

  @Autowired
  UserLoaderPort userLoaderPort;

  @Autowired
  GoalLoaderPort goalLoaderPort;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  Long userId;

  List<Goal> goals;

  @BeforeEach
  void setUp() {
    User user = createAndSaveUser();
    userId = user.getId();
    goals = createAndSaveGoals(user);
  }

  @Test
  void 사용자의_전체_목표를_조회_할_수_있다() {
    // when
    List<BasicGoalResponse> actual = retrieveAllGoalsService.findAllByUser(userId);

    // then
    Assertions.assertThat(actual.size()).isEqualTo(goals.size());
    for (int i = 0; i < goals.size(); i++) {
      assertThat(actual.get(i))
          .extracting("id", "name", "status", "color")
          .containsExactly(goals.get(i)
              .getId(), goals.get(i)
              .getName(), goals.get(i)
              .getStatus(), goals.get(i)
              .getColor());
    }
  }

  @Test
  void 사용자가_존재하지_않는_경우_조회에_실패한다() {
    // given
    Long invalidLoginId = GoalFixture.getRandomId();

    // when
    ThrowingCallable findAllByUser = () -> retrieveAllGoalsService.findAllByUser(
        invalidLoginId);

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class).isThrownBy(findAllByUser)
        .withMessage(GoalErrorCode.USER_NOT_EXISTING.getCodeName());
  }

  private User createAndSaveUser() {
    User user = UserFixture.createRandomUserWithId();
    return signUpPort.save(user);
  }

  private List<Goal> createAndSaveGoals(User user) {
    return IntStream.range(0, 3)
        .mapToObj(i -> GoalFixture.createRandomGoalWithUser(user))
        .map(saveGoalPort::save)
        .sorted(Comparator.comparingLong(Goal::getId)
            .reversed())
        .toList();
  }

}
