package com.modoo.application.notification.dailybriefing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.modoo.application.common.dto.notification.response.DailyBriefingResponse;
import com.modoo.application.common.port.auth.out.SignUpPort;
import com.modoo.application.common.port.goal.out.SaveGoalPort;
import com.modoo.application.common.port.notification.out.DailyBriefingCommandPort;
import com.modoo.application.common.port.todo.out.SaveTodoPort;
import com.modoo.common.exception.DailyBriefingLogErrorCode;
import com.modoo.domain.planning.goal.aggregate.Goal;
import com.modoo.domain.planning.todo.aggregate.Todo;
import com.modoo.domain.user.user.aggregate.User;
import com.modoo.fixture.DailyBriefingLogFixture;
import com.modoo.fixture.GoalFixture;
import com.modoo.fixture.TodoFixture;
import com.modoo.fixture.UserFixture;
import java.time.LocalDate;
import java.util.ArrayList;
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
class BriefTodayPlanningServiceTest {

  @Autowired
  BriefTodayPlanningService briefTodayPlanningService;

  @Autowired
  DailyBriefingCommandPort dailyBriefingCommandPort;

  @Autowired
  SaveTodoPort saveTodoPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SignUpPort signUpPort;

  User user;
  int todoCount;
  List<Todo> todos;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    Goal goal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(user.getId()));
    todos = new ArrayList<>();
    todoCount = TodoFixture.getRandomInt(1, 10);

    for (int i = 0; i < todoCount; i++) {
      todos.add(TodoFixture.createRandomTodoWithSchedule(
          user.getId(),
          goal.getId(),
          LocalDate.now()
      ));
    }

    saveTodoPort.saveAll(todos);
  }

  @Test
  void 오늘_처음_조회하는_경우_투두_개수와_함께_응답을_반환한다() {
    // given

    // when
    DailyBriefingResponse actual = briefTodayPlanningService.getDailyBriefing(user.getId());

    // then
    assertThat(actual.isFirst()).isTrue();
    assertThat(actual.content()
        .count()).isEqualTo(todoCount);
  }

  @Test
  void 이미_오늘_조회한_경우_첫_조회가_아님을_표시한_응답을_반환한다() {
    // given
    dailyBriefingCommandPort.save(DailyBriefingLogFixture.createTodayBriefing(user.getId()));

    // when
    DailyBriefingResponse actual = briefTodayPlanningService.getDailyBriefing(user.getId());

    // then
    assertThat(actual.isFirst()).isFalse();
    assertThat(actual.content()).isNull();
  }

  @Test
  void 존재하지_않는_사용자인_경우_예외가_발생한다() {
    // given
    Long invalidId = UserFixture.getRandomId();

    // when
    ThrowingCallable brief = () -> briefTodayPlanningService.getDailyBriefing(invalidId);

    // then
    assertThatExceptionOfType(MissingResourceException.class).isThrownBy(brief)
        .withMessage(DailyBriefingLogErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
  }

}
