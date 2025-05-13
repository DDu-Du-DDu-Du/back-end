package com.ddudu.aggregate;

import com.ddudu.fixtures.BaseStatsFixture;
import com.ddudu.fixtures.MonthlyStatsFixture;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
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
      int actual = monthlyStats.calculateAchievementPercentage();

      // then
      Assertions.assertThat(actual)
          .isEqualTo(expected);
    }

    @Test
    void 대상_스탯이_비었으면_달성도로_0을_반환한다() {
      // given
      MonthlyStats emptyStats = MonthlyStatsFixture.createEmptyStats(userId, YearMonth.now());

      // when
      int actual = emptyStats.calculateAchievementPercentage();

      // then
      Assertions.assertThat(actual)
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
      Assertions.assertThat(actual)
          .isEqualTo(completedSize);
    }

    @Test
    void 대상_스탯이_비었으면_0을_반환한다() {
      // given
      MonthlyStats emptyStats = MonthlyStatsFixture.createEmptyStats(userId, YearMonth.now());

      // when
      int actual = emptyStats.calculateSustenanceCount();

      // then
      Assertions.assertThat(actual)
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
      Assertions.assertThat(actual)
          .isEqualTo(uncompletedSize);
    }

    @Test
    void 대상_스탯이_비었으면_0을_반환한다() {
      // given
      MonthlyStats emptyStats = MonthlyStatsFixture.createEmptyStats(userId, YearMonth.now());

      // when
      int actual = emptyStats.calculatePostponementCount();

      // then
      Assertions.assertThat(actual)
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
      int actual = monthlyStats.calculateReattainmentCount();

      // then
      Assertions.assertThat(actual)
          .isEqualTo(expected);
    }

    @Test
    void 대상_스탯이_비었으면_0을_반환한다() {
      // given
      MonthlyStats emptyStats = MonthlyStatsFixture.createEmptyStats(userId, YearMonth.now());

      // when
      int actual = emptyStats.calculateReattainmentCount();

      // then
      Assertions.assertThat(actual)
          .isZero();
    }

  }

}