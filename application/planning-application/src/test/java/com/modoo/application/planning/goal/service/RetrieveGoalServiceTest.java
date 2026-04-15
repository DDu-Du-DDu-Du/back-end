package com.modoo.application.planning.goal.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.modoo.application.common.dto.goal.response.GoalWithRepeatTodoResponse;
import com.modoo.application.common.dto.repeattodo.RepeatTodoDto;
import com.modoo.application.common.port.auth.out.SignUpPort;
import com.modoo.application.common.port.goal.out.SaveGoalPort;
import com.modoo.application.common.port.repeattodo.out.RepeatTodoLoaderPort;
import com.modoo.application.common.port.repeattodo.out.SaveRepeatTodoPort;
import com.modoo.common.exception.GoalErrorCode;
import com.modoo.domain.planning.goal.aggregate.Goal;
import com.modoo.domain.planning.repeattodo.aggregate.RepeatTodo;
import com.modoo.domain.planning.repeattodo.aggregate.vo.RepeatPattern;
import com.modoo.domain.user.user.aggregate.User;
import com.modoo.fixture.GoalFixture;
import com.modoo.fixture.RepeatTodoFixture;
import com.modoo.fixture.UserFixture;
import java.time.LocalDate;
import java.util.MissingResourceException;
import org.assertj.core.api.Assertions;
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
class RetrieveGoalServiceTest {

  @Autowired
  RetrieveGoalService retrieveGoalService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveRepeatTodoPort saveRepeatTodoPort;

  @Autowired
  RepeatTodoLoaderPort repeatTodoLoaderPort;

  Long userId;
  Goal goal;

  @BeforeEach
  void setUp() {
    User user = createAndSaveUser();
    userId = user.getId();
    goal = createAndSaveGoal(user);
  }

  @Test
  void ID를_통해_목표를_조회_할_수_있다() {
    // when
    GoalWithRepeatTodoResponse actual = retrieveGoalService.getById(userId, goal.getId());

    // then
    assertThat(actual).extracting(
            "id",
            "name",
            "status",
            "color",
            "privacyType",
            "priority"
        )
        .containsExactly(
            goal.getId(),
            goal.getName(),
            goal.getStatus(),
            goal.getColor(),
            goal.getPrivacyType(),
            goal.getPriority()
        );
  }

  @Test
  void 목표_조회_시_해당_목표의_반복_투두도_함께_조회된다() {
    // given
    LocalDate startDate = LocalDate.now();
    LocalDate endDate = LocalDate.now()
        .plusMonths(1);
    RepeatTodo repeatTodo = RepeatTodoFixture.createRepeatTodoWithGoal(
        goal,
        startDate,
        endDate
    );

    saveRepeatTodoPort.save(repeatTodo);

    // when
    GoalWithRepeatTodoResponse response = retrieveGoalService.getById(userId, goal.getId());

    // then
    RepeatTodoDto first = response.repeatTodos()
        .get(0);
    RepeatTodo actual = repeatTodoLoaderPort.getOptionalRepeatTodo(first.id())
        .get();

    assertThat(first.repeatType()).isEqualTo(repeatTodo.getRepeatType());

    assertThat(actual).extracting(
            "name",
            "repeatType",
            "startDate",
            "endDate"
        )
        .containsExactly(
            repeatTodo.getName(),
            repeatTodo.getRepeatType(),
            repeatTodo.getStartDate(),
            repeatTodo.getEndDate()
        );

    RepeatPattern actualPattern = actual.getRepeatPattern();
    RepeatPattern expectedPattern = repeatTodo.getRepeatPattern();

    assertThat(actualPattern).isInstanceOf(expectedPattern.getClass());
  }

  @Test
  void 유효하지_않은_ID인_경우_조회에_실패한다() {
    // given
    Long invalidId = UserFixture.getRandomId();

    // when
    ThrowingCallable getById = () -> retrieveGoalService.getById(userId, invalidId);

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(getById)
        .withMessage(GoalErrorCode.ID_NOT_EXISTING.getCodeName());
  }

  @Test
  void 로그인_사용자가_목표의_주인이_아닌_경우_조회에_실패한다() {
    // given
    User anotherUser = createAndSaveUser();

    // when
    ThrowingCallable getById = () -> retrieveGoalService.getById(anotherUser.getId(), goal.getId());

    // then
    Assertions.assertThatExceptionOfType(SecurityException.class)
        .isThrownBy(getById)
        .withMessage(GoalErrorCode.INVALID_AUTHORITY.getCodeName());
  }

  private User createAndSaveUser() {
    User user = UserFixture.createRandomUserWithId();
    return signUpPort.save(user);
  }

  private Goal createAndSaveGoal(User user) {
    Goal goal = GoalFixture.createRandomGoalWithUser(user.getId());
    return saveGoalPort.save(goal);
  }

}
