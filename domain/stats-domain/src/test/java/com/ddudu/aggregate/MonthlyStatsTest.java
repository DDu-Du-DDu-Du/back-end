package com.ddudu.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatRuntimeException;

import com.ddudu.aggregate.enums.DduduStatus;
import com.ddudu.common.exception.StatsErrorCode;
import com.ddudu.common.util.AmPmType;
import com.ddudu.fixtures.BaseStatsFixture;
import com.ddudu.fixtures.MonthlyStatsFixture;
import java.time.DayOfWeek;
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
      assertThat(actual.get(goalId)
          .getStats()).hasSize(completedSize);
      assertThat(actual.get(anotherGoalId)
          .getStats()).hasSize(uncompletedSize);
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

  @Nested
  class 월간_통계_병합_merge_테스트 {

    int size;

    @BeforeEach
    void setUp() {
      int size = BaseStatsFixture.getRandomInt(5, 20);
    }

    @Test
    void 두개의_월간_통계를_병합하면_stats가_연결되고_userId와_yearMonth는_유지된다() {
      // given
      int rightSize = BaseStatsFixture.getRandomInt(5, 20);

      List<BaseStats> leftStats = new ArrayList<>(
          BaseStatsFixture.createCompletedStats(
              goalId,
              size
          )
      );

      List<BaseStats> rightStats = new ArrayList<>(
          BaseStatsFixture.createUncompletedStats(
              goalId,
              rightSize
          )
      );

      YearMonth yearMonth = YearMonth.now();
      MonthlyStats left = MonthlyStatsFixture.createMonthlyStats(userId, yearMonth, leftStats);
      MonthlyStats right = MonthlyStatsFixture.createMonthlyStats(userId, yearMonth, rightStats);

      // when
      MonthlyStats merged = left.merge(right);

      // then
      assertThat(merged.getUserId()).isEqualTo(userId);
      assertThat(merged.getYearMonth()).isEqualTo(yearMonth);
      assertThat(merged.getStats()).hasSize(size + rightSize);
    }

    @Test
    void 빈_stats와_병합하면_변경없는_동일_크기의_stats를_가진다() {
      // given
      List<BaseStats> some = BaseStatsFixture.createCompletedStats(goalId, size);

      MonthlyStats nonEmpty = MonthlyStatsFixture.createMonthlyStats(userId, YearMonth.now(), some);
      MonthlyStats empty = MonthlyStatsFixture.createEmptyStats(userId, YearMonth.now());

      // when
      MonthlyStats merged = nonEmpty.merge(empty);

      // then
      assertThat(merged.getStats()).hasSize(size);
    }

  }

  @Nested
  class 가장_활동적인_시간대_getMostActiveTime_테스트 {

    YearMonth yearMonth;
    List<BaseStats> statsInAm;
    List<BaseStats> statsInPm;
    int size;

    @BeforeEach
    void setUp() {
      yearMonth = YearMonth.now();
      statsInAm = new ArrayList<>();
      size = MonthlyStatsFixture.getRandomInt(1, 100);
      statsInPm = new ArrayList<>();
      LocalDate today = LocalDate.now();

      for (int i = 0; i < size; i++) {
        statsInAm.add(BaseStatsFixture.createAmOnlyStat(goalId, today));
      }

      for (int i = 0; i < size; i++) {
        statsInPm.add(BaseStatsFixture.createPmOnlyStat(goalId, today));
      }
    }

    @Test
    void 대상_스탯이_비었으면_NONE을_반환한다() {
      // given
      MonthlyStats empty = MonthlyStatsFixture.createEmptyStats(userId, yearMonth);

      // when
      AmPmType actual = empty.getMostActiveTime();

      // then
      assertThat(actual).isEqualTo(AmPmType.NONE);
    }

    @Test
    void 전부_AM_활동이면_AM을_반환한다() {
      // given
      MonthlyStats ms = MonthlyStatsFixture.createMonthlyStats(userId, yearMonth, statsInAm);

      // when
      AmPmType actual = ms.getMostActiveTime();

      // then
      assertThat(actual).isEqualTo(AmPmType.AM);
    }

    @Test
    void 전부_PM_활동이면_PM을_반환한다() {
      // given
      MonthlyStats ms = MonthlyStatsFixture.createMonthlyStats(userId, yearMonth, statsInPm);

      // when
      AmPmType actual = ms.getMostActiveTime();

      // then
      assertThat(actual).isEqualTo(AmPmType.PM);
    }

    @Test
    void AM과_PM_가중치가_같으면_BOTH를_반환한다() {
      // given
      List<BaseStats> stats = List.of(
          BaseStatsFixture.createAcrossNoonBalancedStat(goalId, LocalDate.now()),
          BaseStatsFixture.createAcrossNoonBalancedStat(goalId, LocalDate.now())
      );
      MonthlyStats ms = MonthlyStatsFixture.createMonthlyStats(userId, yearMonth, stats);

      // when
      AmPmType actual = ms.getMostActiveTime();

      // then
      assertThat(actual).isEqualTo(AmPmType.BOTH);
    }

  }

  @Nested
  class 요일_통계_테스트 {

    @Test
    void 완료된_항목의_요일만_카운트하고_나머지는_0으로_채운다() {
      // given
      // 월/화는 COMPLETE, 수는 UNCOMPLETED → 월:1, 화:1, 수:0, 그 외:0
      LocalDate monday = LocalDate.of(2025, 8, 4);
      LocalDate tuesday = LocalDate.of(2025, 8, 5);
      LocalDate wednesday = LocalDate.of(2025, 8, 6);

      List<BaseStats> list = new ArrayList<>();
      list.add(BaseStatsFixture.createAmOnlyStat(goalId, monday));   // COMPLETE
      list.add(BaseStatsFixture.createPmOnlyStat(goalId, tuesday));  // COMPLETE

      // UNCOMPLETED 은 카운트에 포함되지 않아야 함
      list.add(
          BaseStatsFixture.createRandomWithGoalAndPostponedAndStatusAndScheduled(
              goalId,
              false,
              DduduStatus.UNCOMPLETED,
              wednesday
          )
      );

      MonthlyStats ms = MonthlyStatsFixture.createMonthlyStats(userId, YearMonth.of(2025, 8), list);

      // when
      Map<DayOfWeek, Integer> actual = ms.collectDayOfWeek();

      // then
      assertThat(actual).hasSize(DayOfWeek.values().length);
      // 기본 0으로 채워졌는지 간접 확인: 특정 요일 외에는 0일 수 있음
      assertThat(actual.get(DayOfWeek.MONDAY)).isEqualTo(1);
      assertThat(actual.get(DayOfWeek.TUESDAY)).isEqualTo(1);
      assertThat(actual.get(DayOfWeek.WEDNESDAY)).isEqualTo(0);
      // 나머지 요일도 key는 있고 값은 0일 수 있음
      assertThat(actual.get(DayOfWeek.THURSDAY)).isZero();
      assertThat(actual.get(DayOfWeek.FRIDAY)).isZero();
      assertThat(actual.get(DayOfWeek.SATURDAY)).isZero();
      assertThat(actual.get(DayOfWeek.SUNDAY)).isZero();
    }

    @Test
    void 대상_스탯이_비었으면_모든_요일이_0으로_채워진_맵을_반환한다() {
      // given
      MonthlyStats empty = MonthlyStatsFixture.createEmptyStats(userId, YearMonth.now());

      // when
      Map<DayOfWeek, Integer> actual = empty.collectDayOfWeek();

      // then
      assertThat(actual).hasSize(DayOfWeek.values().length);
      for (DayOfWeek day : DayOfWeek.values()) {
        assertThat(actual.get(day)).isZero();
      }
    }

  }

}