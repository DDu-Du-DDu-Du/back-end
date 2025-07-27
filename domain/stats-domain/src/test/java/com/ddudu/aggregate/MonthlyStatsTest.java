package com.ddudu.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatRuntimeException;

import com.ddudu.common.exception.StatsErrorCode;
import com.ddudu.fixtures.BaseStatsFixture;
import com.ddudu.fixtures.MonthlyStatsFixture;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class MonthlyStatsTest {

  long userId;
  long goalId;
  int totalSize;
  int completedSize;
  int uncompletedSize;
  List<BaseStats> stats;
  MonthlyStats monthlyStats;

  @BeforeEach
  void setUp() {
    userId = BaseStatsFixture.getRandomId();
    goalId = BaseStatsFixture.getRandomId();
    stats = new ArrayList<>();
  }

  @Nested
  class 월_통계_목표_그룹핑_테스트 {

    Long anotherGoalId;

    @BeforeEach
    void setUp() {
      anotherGoalId = BaseStatsFixture.getRandomId();
      totalSize = BaseStatsFixture.getRandomInt(10, 100);
      completedSize = BaseStatsFixture.getRandomInt(1, totalSize);
      uncompletedSize = totalSize - completedSize;

      stats.addAll(BaseStatsFixture.createNotPostponedStats(goalId, completedSize));
      stats.addAll(BaseStatsFixture.createNotPostponedStats(anotherGoalId, uncompletedSize));

      monthlyStats = MonthlyStatsFixture.createMonthlyStats(userId, YearMonth.now(), stats);
    }

    @Test
    void 목표_별_그룹핑을_성공한다() {
      // when
      Map<Long, MonthlyStats> actual = monthlyStats.groupByGoal();

      // then
      assertThat(actual).containsKeys(goalId, anotherGoalId);
      assertThat(actual.get(goalId).getStats()).hasSize(completedSize);
      assertThat(actual.get(anotherGoalId).getStats()).hasSize(uncompletedSize);
    }

    @Test
    void 목표로_그룹핑_된_통계의_목표_아이디를_반환한다() {
      // given
      Map<Long, MonthlyStats> byGoal = monthlyStats.groupByGoal();

      // when
      Long actual = byGoal.get(goalId)
          .getGoalId();

      // then
      assertThat(actual).isEqualTo(goalId);
    }

    @Test
    void 통계_데이터가_없는_월_통계의_목표_아이디_반환을_실패한다() {
      // given
      MonthlyStats empty = MonthlyStats.empty(userId, YearMonth.now());

      // when
      ThrowingCallable getGoalId = () -> empty.getGoalId();

      // then
      assertThatRuntimeException().isThrownBy(getGoalId)
          .withMessage(StatsErrorCode.MONTHLY_STATS_EMPTY.getCodeName());
    }

    @Test
    void 목표가_여러개인_월_통계의_목표_아이디_반환을_실패한다() {
      // given

      // when
      ThrowingCallable getGoalId = () -> monthlyStats.getGoalId();

      // then
      assertThatRuntimeException().isThrownBy(getGoalId)
          .withMessage(StatsErrorCode.MONTHLY_STATS_NOT_GROUPED_BY_GOAL.getCodeName());
    }

    @Test
    void 통계_데이터가_없는_월_통계의_목표명_반환을_실패한다() {
      // given
      MonthlyStats empty = MonthlyStats.empty(userId, YearMonth.now());

      // when
      ThrowingCallable getGoalName = () -> empty.getGoalName();

      // then
      assertThatRuntimeException().isThrownBy(getGoalName)
          .withMessage(StatsErrorCode.MONTHLY_STATS_EMPTY.getCodeName());
    }

    @Test
    void 목표가_여러개인_월_통계의_목표명_반환을_실패한다() {
      // given

      // when
      ThrowingCallable getGoalName = () -> monthlyStats.getGoalName();

      // then
      assertThatRuntimeException().isThrownBy(getGoalName)
          .withMessage(StatsErrorCode.MONTHLY_STATS_NOT_GROUPED_BY_GOAL.getCodeName());
    }

    @Test
    void 목표로_그룹핑_된_통계의_목표명을_반환한다() {
      // given
      Map<Long, MonthlyStats> byGoal = monthlyStats.groupByGoal();
      String expected = String.valueOf(goalId);

      // when
      String actual = byGoal.get(goalId)
          .getGoalName();

      // then
      assertThat(actual).isEqualTo(expected);
    }

  }

  @Nested
  class 뚜두_달성도_테스트 {

    @BeforeEach
    void setUp() {
      totalSize = BaseStatsFixture.getRandomInt(100, 1000);
      completedSize = BaseStatsFixture.getRandomInt(1, totalSize);
      uncompletedSize = totalSize - completedSize;

      stats.addAll(BaseStatsFixture.createCompletedStats(goalId, completedSize));
      stats.addAll(BaseStatsFixture.createUncompletedStats(goalId, uncompletedSize));

      monthlyStats = MonthlyStatsFixture.createMonthlyStats(userId, YearMonth.now(), stats);
    }

    @Test
    void 달성도를_계산한다() {
      // given
      int expected = Math.round((float) completedSize / totalSize * 100);

      // when
      int actual = monthlyStats.calculateAchievementRate();

      // then
      assertThat(actual)
          .isEqualTo(expected);
    }

    @Test
    void 대상_스탯이_비었으면_달성도로_0을_반환한다() {
      // given
      MonthlyStats emptyStats = MonthlyStatsFixture.createEmptyStats(userId, YearMonth.now());

      // when
      int actual = emptyStats.calculateAchievementRate();

      // then
      assertThat(actual)
          .isZero();
    }

  }

  @Nested
  class 뚜두_지속도_테스트 {

    @BeforeEach
    void setUp() {
      totalSize = 14;
      completedSize = 10;
      uncompletedSize = totalSize - completedSize;
      LocalDate from = YearMonth.now()
          .atDay(1);

      stats.addAll(BaseStatsFixture.createConsecutiveCompletedStats(goalId, from, completedSize));
      stats.addAll(BaseStatsFixture.createUncompletedStats(goalId, uncompletedSize));

      monthlyStats = MonthlyStatsFixture.createMonthlyStats(userId, YearMonth.now(), stats);
    }

    @Test
    void 지속도를_계산한다() {
      // given

      // when
      int actual = monthlyStats.calculateSustenanceCount();

      // then
      assertThat(actual)
          .isEqualTo(completedSize);
    }

    @Test
    void 대상_스탯이_비었으면_0을_반환한다() {
      // given
      MonthlyStats emptyStats = MonthlyStatsFixture.createEmptyStats(userId, YearMonth.now());

      // when
      int actual = emptyStats.calculateSustenanceCount();

      // then
      assertThat(actual)
          .isZero();
    }

  }

  @Nested
  class 미룬_뚜두_테스트 {

    @BeforeEach
    void setUp() {
      totalSize = BaseStatsFixture.getRandomInt(100, 1000);
      completedSize = BaseStatsFixture.getRandomInt(1, totalSize);
      uncompletedSize = totalSize - completedSize;

      stats.addAll(BaseStatsFixture.createPostponedStats(goalId, uncompletedSize));
      stats.addAll(BaseStatsFixture.createNotPostponedStats(goalId, completedSize));

      monthlyStats = MonthlyStatsFixture.createMonthlyStats(userId, YearMonth.now(), stats);
    }

    @Test
    void 미루기_통계를_계산한다() {
      // given

      // when
      int actual = monthlyStats.calculatePostponementCount();

      // then
      assertThat(actual)
          .isEqualTo(uncompletedSize);
    }

    @Test
    void 대상_스탯이_비었으면_0을_반환한다() {
      // given
      MonthlyStats emptyStats = MonthlyStatsFixture.createEmptyStats(userId, YearMonth.now());

      // when
      int actual = emptyStats.calculatePostponementCount();

      // then
      assertThat(actual)
          .isZero();
    }

  }

  @Nested
  class 뚜두_재달성률_테스트 {

    @BeforeEach
    void setUp() {
      totalSize = BaseStatsFixture.getRandomInt(100, 1000);
      completedSize = BaseStatsFixture.getRandomInt(1, totalSize);
      uncompletedSize = totalSize - completedSize;

      stats.addAll(BaseStatsFixture.createPostponedCompleteStats(goalId, completedSize));
      stats.addAll(BaseStatsFixture.createUncompletedStats(goalId, uncompletedSize));

      monthlyStats = MonthlyStatsFixture.createMonthlyStats(userId, YearMonth.now(), stats);
    }

    @Test
    void 재달성률을_계산한다() {
      // given
      int expected = Math.round((float) completedSize / totalSize * 100);

      // when
      int actual = monthlyStats.calculateReattainmentRate();

      // then
      assertThat(actual)
          .isEqualTo(expected);
    }

    @Test
    void 대상_스탯이_비었으면_0을_반환한다() {
      // given
      MonthlyStats emptyStats = MonthlyStatsFixture.createEmptyStats(userId, YearMonth.now());

      // when
      int actual = emptyStats.calculateReattainmentRate();

      // then
      assertThat(actual)
          .isZero();
    }

  }

}