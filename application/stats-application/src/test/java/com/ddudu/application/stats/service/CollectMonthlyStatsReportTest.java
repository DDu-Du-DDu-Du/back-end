package com.ddudu.application.stats.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.stats.response.MonthlyStatsReportResponse;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.application.common.port.todo.out.SaveTodoPort;
import com.ddudu.common.exception.StatsErrorCode;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.todo.aggregate.enums.TodoStatus;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.TodoFixture;
import com.ddudu.fixture.UserFixture;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
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
class CollectMonthlyStatsReportTest {

  @Autowired
  CollectMonthlyStatsReportService collectMonthlyStatsService;

  @Autowired
  SignUpPort signUpPort;

  @Autowired
  SaveGoalPort saveGoalPort;

  @Autowired
  SaveTodoPort saveTodoPort;

  User user;
  List<Goal> goals;
  int uncompletedCountPerGoal;
  int completedCountPerGoal;

  @BeforeEach
  void setUp() {
    user = signUpPort.save(UserFixture.createRandomUserWithId());
    goals = saveGoalPort.saveAll(GoalFixture.createRandomGoalsWithUser(user.getId(), 5));
    uncompletedCountPerGoal = 5;
    completedCountPerGoal = 5;
    LocalDate lastMonth = LocalDate.now()
        .minusMonths(1);

    goals.forEach(goal -> saveTodoPort.saveAll(
        TodoFixture.createDifferentTodosWithGoal(
            goal, completedCountPerGoal,
            uncompletedCountPerGoal
        )));

    for (int i = 0; i < completedCountPerGoal; i++) {
      goals.forEach(goal -> saveTodoPort.save(
          TodoFixture.createRandomTodoWithStatusAndSchedule(
              goal, TodoStatus.COMPLETE,
              lastMonth
          )
      ));
    }

    for (int i = 0; i < uncompletedCountPerGoal; i++) {
      goals.forEach(goal -> saveTodoPort.save(
          TodoFixture.createRandomTodoWithStatusAndSchedule(
              goal, TodoStatus.UNCOMPLETED,
              lastMonth
          )
      ));
    }
  }

  @Test
  void 이번달_투두_통합_통계를_낸다() {
    // given
    YearMonth thisMonth = YearMonth.now();

    // when
    MonthlyStatsReportResponse response = collectMonthlyStatsService.collectReport(
        user.getId(), thisMonth);

    // then
    int totalPerGoal = uncompletedCountPerGoal + completedCountPerGoal;

    assertThat(response.lastMonth())
        .hasFieldOrPropertyWithValue("yearMonth", thisMonth.minusMonths(1))
        .hasFieldOrPropertyWithValue("totalCount", totalPerGoal * goals.size());
    assertThat(response.thisMonth())
        .hasFieldOrPropertyWithValue("yearMonth", thisMonth)
        .hasFieldOrPropertyWithValue("totalCount", totalPerGoal * goals.size());
  }

  @Test
  void 요청의_날짜가_없으면_이번달의_투두_통계를_낸다() {
    // given
    YearMonth thisMonth = YearMonth.now();

    // when
    MonthlyStatsReportResponse response = collectMonthlyStatsService.collectReport(
        user.getId(),
        null
    );

    // then
    int totalPerGoal = uncompletedCountPerGoal + completedCountPerGoal;

    assertThat(response.lastMonth())
        .hasFieldOrPropertyWithValue("yearMonth", thisMonth.minusMonths(1))
        .hasFieldOrPropertyWithValue("totalCount", totalPerGoal * goals.size());
    assertThat(response.thisMonth())
        .hasFieldOrPropertyWithValue("yearMonth", thisMonth)
        .hasFieldOrPropertyWithValue("totalCount", totalPerGoal * goals.size());
  }

  @Test
  void 지난달_투두가_없어도_통계는_0으로_계산된다() {
    // given
    YearMonth lastMonth = YearMonth.now()
        .minusMonths(1);

    // when
    MonthlyStatsReportResponse response = collectMonthlyStatsService.collectReport(
        user.getId(),
        lastMonth
    );

    // then
    assertThat(response.lastMonth()
        .totalCount()).isZero();
  }

  @Test
  void 로그인_사용자가_없으면_월_달성_투두_수_통계를_실패한다() {
    // given
    long invalidId = GoalFixture.getRandomId();
    YearMonth thisMonth = YearMonth.now();

    // when
    ThrowingCallable collect = () -> collectMonthlyStatsService.collectReport(
        invalidId, thisMonth);

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(collect)
        .withMessage(StatsErrorCode.USER_NOT_EXISTING.getCodeName());
  }

}