package com.modoo.application.planning.todo.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.modoo.application.common.dto.todo.response.TodoDashboardContent;
import com.modoo.application.common.dto.todo.response.TodoDashboardItem;
import com.modoo.application.common.dto.todo.response.TodoDashboardResponse;
import com.modoo.application.common.port.auth.out.SignUpPort;
import com.modoo.application.common.port.goal.out.SaveGoalPort;
import com.modoo.application.common.port.todo.out.SaveTodoPort;
import com.modoo.common.exception.TodoErrorCode;
import com.modoo.domain.planning.goal.aggregate.Goal;
import com.modoo.domain.planning.goal.aggregate.enums.PrivacyType;
import com.modoo.domain.planning.todo.aggregate.Todo;
import com.modoo.domain.planning.todo.aggregate.enums.TodoStatus;
import com.modoo.domain.user.user.aggregate.User;
import com.modoo.fixture.GoalFixture;
import com.modoo.fixture.TodoFixture;
import com.modoo.fixture.UserFixture;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.MissingResourceException;
import org.assertj.core.api.AssertionsForClassTypes;
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
class GetTodoDashboardServiceTest {

  @Autowired
  GetTodoDashboardService getTodoDashboardService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveTodoPort saveTodoPort;

  User user;
  Goal goal;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goal = saveGoalPort.save(
        GoalFixture.createRandomGoalWithUserAndPrivacyType(user.getId(), PrivacyType.PRIVATE));
  }

  @Test
  void 투두가_없어도_오늘_그룹을_포함해_대시보드를_조회한다() {
    // given

    // when
    TodoDashboardResponse response = getTodoDashboardService.get(user.getId());

    // then
    assertThat(response.isEmpty()).isTrue();
    assertThat(response.contents()).hasSize(1);
    assertThat(response.contents().get(0).date()).isEqualTo(LocalDate.now());
    assertThat(response.contents().get(0).todos()).isEmpty();
    assertThat(response.todayIndex()).isEqualTo(0);
  }

  @Test
  void 대시보드_날짜_그룹은_최신순으로_정렬하고_오늘_그룹을_포함해_조회한다() {
    // given
    LocalDate today = LocalDate.now();
    LocalDate tomorrow = today.plusDays(1);
    LocalDate yesterday = today.minusDays(1);

    Todo todayTodo = TodoFixture.getTodoBuilder()
        .goalId(goal.getId())
        .userId(user.getId())
        .scheduledOn(today)
        .build();
    Todo tomorrowTodo = TodoFixture.getTodoBuilder()
        .goalId(goal.getId())
        .userId(user.getId())
        .scheduledOn(tomorrow)
        .build();
    Todo yesterdayTodo = TodoFixture.getTodoBuilder()
        .goalId(goal.getId())
        .userId(user.getId())
        .scheduledOn(yesterday)
        .build();
    saveTodoPort.save(todayTodo);
    saveTodoPort.save(tomorrowTodo);
    saveTodoPort.save(yesterdayTodo);

    // when
    TodoDashboardResponse response = getTodoDashboardService.get(user.getId());

    // then
    assertThat(response.isEmpty()).isFalse();
    assertThat(response.contents())
        .extracting(TodoDashboardContent::date)
        .containsExactly(tomorrow, today, yesterday);
    assertThat(response.todayIndex()).isEqualTo(1);
  }

  @Test
  void 대시보드_그룹_내_정렬_기준을_보장한다() {
    // given
    LocalDate scheduleDate = LocalDate.now();
    Todo first = saveTodoPort.save(TodoFixture.getTodoBuilder()
        .id(1000L)
        .goalId(goal.getId())
        .userId(user.getId())
        .scheduledOn(scheduleDate)
        .status(TodoStatus.UNCOMPLETED)
        .beginAt(LocalTime.of(9, 0))
        .endAt(LocalTime.of(10, 0))
        .build());
    Todo second = saveTodoPort.save(TodoFixture.getTodoBuilder()
        .id(2000L)
        .goalId(goal.getId())
        .userId(user.getId())
        .scheduledOn(scheduleDate)
        .status(TodoStatus.UNCOMPLETED)
        .beginAt(null)
        .endAt(null)
        .build());
    Todo third = saveTodoPort.save(TodoFixture.getTodoBuilder()
        .id(3000L)
        .goalId(goal.getId())
        .userId(user.getId())
        .scheduledOn(scheduleDate)
        .status(TodoStatus.COMPLETE)
        .beginAt(LocalTime.of(8, 0))
        .endAt(LocalTime.of(9, 0))
        .build());

    // when
    TodoDashboardResponse response = getTodoDashboardService.get(user.getId());

    // then
    List<TodoDashboardItem> todos = response.contents().stream()
        .filter(content -> content.date().isEqual(scheduleDate))
        .findFirst()
        .orElseThrow()
        .todos();

    Comparator<TodoDashboardItem> comparator =
        Comparator.comparing(TodoDashboardItem::status)
            .thenComparing(
                TodoDashboardItem::beginAt,
                Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(
                TodoDashboardItem::endAt,
                Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(TodoDashboardItem::id);

    List<Long> expectedOrder = List.of(first, second, third).stream()
        .map(TodoDashboardItem::from)
        .sorted(comparator)
        .map(TodoDashboardItem::id)
        .toList();

    assertThat(todos)
        .extracting(TodoDashboardItem::id)
        .containsExactlyElementsOf(expectedOrder);
    assertThat(todos).isSortedAccordingTo(comparator);
  }

  @Test
  void 로그인_사용자가_없으면_대시보드_조회에_실패한다() {
    // given
    Long invalidLoginId = UserFixture.getRandomId();

    // when
    ThrowingCallable getDashboard = () -> getTodoDashboardService.get(invalidLoginId);

    // then
    AssertionsForClassTypes.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(getDashboard)
        .withMessage(TodoErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName());
  }

}
