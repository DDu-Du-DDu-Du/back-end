package com.ddudu.application.stats.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.common.dto.stats.GoalMonthlyStatsSummary;
import com.ddudu.application.common.dto.stats.response.MonthlyStatsSummaryResponse;
import com.ddudu.application.common.port.auth.out.SignUpPort;
import com.ddudu.application.common.port.todo.out.SaveTodoPort;
import com.ddudu.application.common.port.goal.out.SaveGoalPort;
import com.ddudu.common.exception.StatsErrorCode;
import com.ddudu.domain.planning.todo.aggregate.enums.TodoStatus;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.user.user.aggregate.User;
import com.ddudu.fixture.TodoFixture;
import com.ddudu.fixture.GoalFixture;
import com.ddudu.fixture.UserFixture;
import com.ddudu.fixtures.MonthlyStatsFixture;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class CollectMonthlyStatsSummaryServiceTest {

  @Autowired
  private CollectMonthlyStatsSummaryService collectMonthlyStatsSummaryService;

  @Autowired
  private SignUpPort signUpPort;

  @Autowired
  private SaveGoalPort saveGoalPort;

  @Autowired
  private SaveTodoPort saveTodoPort;

  private User user;
  private List<Goal> goals;

  @Nested
  class 만든_수_통계_테스트 {

    private List<Integer> creationCounts;

    @BeforeEach
    void setUp() {
      user = signUpPort.save(UserFixture.createRandomUserWithId());
      goals = saveGoalPort.saveAll(GoalFixture.createRandomGoalsWithUser(user.getId(), 5));
      creationCounts = new ArrayList<>();

      for (int i = 0; i < goals.size(); i++) {
        creationCounts.add(TodoFixture.getRandomInt(1, 100));
      }

      Iterator<Integer> countIterator = creationCounts.iterator();
      goals.forEach(goal -> saveTodoPort.saveAll(TodoFixture.createMultipleTodosWithGoal(
          goal,
          countIterator.next()
      )));

      creationCounts.sort(Comparator.reverseOrder());
    }

    @Test
    void 이번_달_월별_목표들의_투두_생성_수_통계를_내림차순으로_낸다() {
      // given
      YearMonth thisMonth = YearMonth.now();

      // when
      MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryService.collectSummary(
          user.getId(),
          null,
          thisMonth
      );

      // then
      List<Integer> actualCreationCounts = response.summaries().stream()
          .map(GoalMonthlyStatsSummary::creationCount)
          .toList();

      assertThat(actualCreationCounts).isEqualTo(creationCounts);
    }

    @Test
    void 목표에_해당_달_생성_투두가_없으면_통계에서_제외한다() {
      // given
      YearMonth lastMonth = YearMonth.now().minusMonths(1);

      // when
      MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryService.collectSummary(
          user.getId(),
          null,
          lastMonth
      );

      // then
      assertThat(response.summaries()).isEmpty();
    }

    @Test
    void 날짜가_없으면_이번_달_통계를_낸다() {
      // given

      // when
      MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryService.collectSummary(
          user.getId(),
          null,
          null
      );

      // then
      List<Integer> actualCreationCounts = response.summaries().stream()
          .map(GoalMonthlyStatsSummary::creationCount)
          .toList();

      assertThat(actualCreationCounts).isEqualTo(creationCounts);
    }

  }

  @Nested
  class 달성도_통계_테스트 {

    @BeforeEach
    void setUp() {
      user = signUpPort.save(UserFixture.createRandomUserWithId());
      goals = saveGoalPort.saveAll(GoalFixture.createRandomGoalsWithUser(user.getId(), 5));

      goals.forEach(goal -> saveTodoPort.saveAll(TodoFixture.createDifferentTodosWithGoal(
          goal,
          TodoFixture.getRandomInt(1, 10),
          TodoFixture.getRandomInt(1, 10)
      )));
    }

    @Test
    void 이번_달_목표별_투두_달성_수_통계를_반환한다() {
      // given
      YearMonth thisMonth = YearMonth.now();

      // when
      MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryService.collectSummary(
          user.getId(),
          null,
          thisMonth
      );

      // then
      assertThat(response.summaries())
          .hasSize(goals.size())
          .allMatch(summary -> summary.achievementCount() >= 0);
    }

    @Test
    void 목표에_해당_달의_투두_데이터가_없으면_통계에_포함되지_않는다() {
      // given
      YearMonth lastMonth = YearMonth.now().minusMonths(1);

      // when
      MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryService.collectSummary(
          user.getId(),
          null,
          lastMonth
      );

      // then
      assertThat(response.summaries()).isEmpty();
    }

    @ParameterizedTest
    @NullSource
    void 날짜가_null이면_이번_달_통계를_반환한다(YearMonth yearMonth) {
      // given

      // when
      MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryService.collectSummary(
          user.getId(),
          null,
          yearMonth
      );

      // then
      assertThat(response.summaries()).hasSize(goals.size());
    }

  }

  @Nested
  class 지속도_통계_테스트 {

    private List<Integer> expectedSustenanceCounts;

    @BeforeEach
    void setUp() {
      user = signUpPort.save(UserFixture.createRandomUserWithId());
      goals = saveGoalPort.saveAll(GoalFixture.createRandomGoalsWithUser(user.getId(), 5));
      expectedSustenanceCounts = new ArrayList<>();

      for (int i = 0; i < goals.size(); i++) {
        expectedSustenanceCounts.add(MonthlyStatsFixture.getRandomInt(1, 10));
      }

      Iterator<Integer> iterator = expectedSustenanceCounts.iterator();
      goals.forEach(goal -> saveTodoPort.saveAll(TodoFixture.createConsecutiveCompletedTodos(
          goal,
          iterator.next()
      )));

      expectedSustenanceCounts.sort(Comparator.reverseOrder());
    }

    @Test
    void 이번_달_지속한_투두_수_통계를_내림차순으로_반환한다() {
      // given
      YearMonth thisMonth = YearMonth.now();

      // when
      MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryService.collectSummary(
          user.getId(),
          null,
          thisMonth
      );

      // then
      List<Integer> actualSustenanceCounts = response.summaries().stream()
          .map(GoalMonthlyStatsSummary::sustainedCount)
          .sorted(Comparator.reverseOrder())
          .toList();

      assertThat(actualSustenanceCounts).isEqualTo(expectedSustenanceCounts);
    }

    @Test
    void 목표에_해당_월_지속한_투두가_없으면_통계에서_제외한다() {
      // given
      YearMonth lastMonth = YearMonth.now().minusMonths(1);

      // when
      MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryService.collectSummary(
          user.getId(),
          null,
          lastMonth
      );

      // then
      assertThat(response.summaries()).isEmpty();
    }

    @Test
    void 날짜가_null이면_이번_달_통계를_반환한다() {
      // given

      // when
      MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryService.collectSummary(
          user.getId(),
          null,
          null
      );

      // then
      assertThat(response.summaries()).hasSameSizeAs(goals);
    }

  }

  @Nested
  class 미루기_통계_테스트 {

    private List<Integer> expectedPostponedCounts;

    @BeforeEach
    void setUp() {
      user = signUpPort.save(UserFixture.createRandomUserWithId());
      goals = saveGoalPort.saveAll(GoalFixture.createRandomGoalsWithUser(user.getId(), 5));
      expectedPostponedCounts = new ArrayList<>();

      for (int i = 0; i < goals.size(); i++) {
        expectedPostponedCounts.add(TodoFixture.getRandomInt(0, 100));
      }

      Iterator<Integer> iterator = expectedPostponedCounts.iterator();
      goals.forEach(goal -> saveTodoPort.saveAll(TodoFixture.createTodosWithPostponedFlag(
          goal,
          iterator.next(),
          TodoFixture.getRandomInt(1, 100)
      )));

      expectedPostponedCounts.sort(Comparator.reverseOrder());
    }

    @Test
    void 월_목표_별_미루기_통계를_반환한다() {
      // given
      YearMonth thisMonth = YearMonth.now();

      // when
      MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryService.collectSummary(
          user.getId(),
          null,
          thisMonth
      );

      // then
      List<Integer> actualPostponedCounts = response.summaries().stream()
          .map(GoalMonthlyStatsSummary::postponedCount)
          .sorted(Comparator.reverseOrder())
          .toList();

      assertThat(actualPostponedCounts).isEqualTo(expectedPostponedCounts);
    }

    @Test
    void 미루기_투두가_없는_달이면_통계는_비어있다() {
      // given
      YearMonth noTodoMonth = YearMonth.now().minusMonths(2);

      // when
      MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryService.collectSummary(
          user.getId(),
          null,
          noTodoMonth
      );

      // then
      assertThat(response.summaries()).isEmpty();
    }

  }

  @Nested
  class 재달성_통계_테스트 {

    private List<Integer> expectedReattainmentCounts;

    @BeforeEach
    void setUp() {
      user = signUpPort.save(UserFixture.createRandomUserWithId());
      goals = saveGoalPort.saveAll(GoalFixture.createRandomGoalsWithUser(user.getId(), 5));
      expectedReattainmentCounts = new ArrayList<>();

      for (int i = 0; i < goals.size(); i++) {
        int totalPostponedCount = TodoFixture.getRandomInt(1, 100);
        int reattainedCount = MonthlyStatsFixture.getRandomInt(1, totalPostponedCount);

        saveTodoPort.saveAll(TodoFixture.createReattainedTodos(
            goals.get(i),
            reattainedCount,
            totalPostponedCount
        ));

        expectedReattainmentCounts.add(reattainedCount);
      }

      expectedReattainmentCounts.sort(Comparator.reverseOrder());
    }

    @Test
    void 월_목표_별_재달성_수_통계를_반환한다() {
      // given
      YearMonth thisMonth = YearMonth.now();

      // when
      MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryService.collectSummary(
          user.getId(),
          null,
          thisMonth
      );

      // then
      List<Integer> actualReattainmentCounts = response.summaries().stream()
          .map(GoalMonthlyStatsSummary::reattainedCount)
          .sorted(Comparator.reverseOrder())
          .toList();

      assertThat(actualReattainmentCounts).isEqualTo(expectedReattainmentCounts);
    }

    @Test
    void 목표에_해당_월_투두가_없으면_통계에서_제외한다() {
      // given
      YearMonth lastMonth = YearMonth.now().minusMonths(1);

      // when
      MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryService.collectSummary(
          user.getId(),
          null,
          lastMonth
      );

      // then
      assertThat(response.summaries()).isEmpty();
    }

  }

  @Test
  void 요청_사용자_아이디가_있으면_로그인_사용자_대신_해당_사용자_기준으로_월_요약을_조회한다() {
    // given
    User loginUser = signUpPort.save(UserFixture.createRandomUserWithId());
    User targetUser = signUpPort.save(UserFixture.createRandomUserWithId());
    Goal targetGoal = saveGoalPort.save(GoalFixture.createRandomGoalWithUser(targetUser.getId()));

    saveTodoPort.saveAll(List.of(
        TodoFixture.createRandomTodoWithReference(targetGoal.getId(), targetUser.getId(), true,
            TodoStatus.COMPLETE),
        TodoFixture.createRandomTodoWithReference(targetGoal.getId(), targetUser.getId(), false,
            TodoStatus.UNCOMPLETED)
    ));

    // when
    MonthlyStatsSummaryResponse response = collectMonthlyStatsSummaryService.collectSummary(
        loginUser.getId(),
        targetUser.getId(),
        YearMonth.now()
    );

    // then
    assertThat(response.summaries()).hasSize(1);
    assertThat(response.summaries().get(0).goalColor()).isEqualTo(targetGoal.getColor());
  }

  @Test
  void 로그인_사용자가_없으면_월_통계_계산을_실패한다() {
    // given
    long invalidId = GoalFixture.getRandomId();

    // when
    ThrowingCallable collect = () -> collectMonthlyStatsSummaryService.collectSummary(
        invalidId,
        null,
        YearMonth.now()
    );

    // then
    Assertions.assertThatExceptionOfType(MissingResourceException.class)
        .isThrownBy(collect)
        .withMessage(StatsErrorCode.USER_NOT_EXISTING.getCodeName());
  }

}
