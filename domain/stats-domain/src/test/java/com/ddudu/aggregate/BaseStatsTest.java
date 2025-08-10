package com.ddudu.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.aggregate.enums.DduduStatus;
import com.ddudu.fixtures.BaseStatsFixture;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class BaseStatsTest {

  long goalId;
  long anotherGoalId;
  LocalDate today;

  @BeforeEach
  void setUp() {
    goalId = BaseStatsFixture.getRandomId();
    anotherGoalId = BaseStatsFixture.getRandomId();
    today = YearMonth.now()
        .atDay(1);
  }

  @Nested
  class 완료_여부_isCompleted_테스트 {

    @Test
    void 상태가_COMPLETE면_true를_반환한다() {
      // given
      BaseStats stat = BaseStatsFixture.createRandomWithGoalAndStatus(goalId, DduduStatus.COMPLETE);

      // when
      boolean actual = stat.isCompleted();

      // then
      assertThat(actual).isTrue();
    }

    @Test
    void 상태가_UNCOMPLETED면_false를_반환한다() {
      // given
      BaseStats stat = BaseStatsFixture.createRandomWithGoalAndStatus(
          goalId,
          DduduStatus.UNCOMPLETED
      );

      // when
      boolean actual = stat.isCompleted();

      // then
      assertThat(actual).isFalse();
    }

  }

  @Nested
  class 동일_목표_판단_isUnderSameGoal_테스트 {

    @Test
    void 같은_goalId면_true를_반환한다() {
      // given
      BaseStats stat = BaseStatsFixture.createRandomWithGoal(goalId);

      // when
      boolean actual = stat.isUnderSameGoal(goalId);

      // then
      assertThat(actual).isTrue();
    }

    @Test
    void 다른_goalId면_false를_반환한다() {
      // given
      BaseStats stat = BaseStatsFixture.createRandomWithGoal(goalId);

      // when
      boolean actual = stat.isUnderSameGoal(anotherGoalId);

      // then
      assertThat(actual).isFalse();
    }

  }

  @Nested
  class 시간대_가중치_getTimePart_테스트 {

    @Test
    void 전부_AM이면_음수값을_반환한다() {
      // given (09:00~11:00)
      BaseStats am = BaseStatsFixture.createAmOnlyStat(goalId, today);

      // when
      long actual = am.getTimePart();

      // then
      assertThat(actual).isLessThan(0L); // AM 우세
    }

    @Test
    void 전부_PM이면_양수값을_반환한다() {
      // given (13:00~15:00)
      BaseStats pm = BaseStatsFixture.createPmOnlyStat(goalId, today);

      // when
      long actual = pm.getTimePart();

      // then
      assertThat(actual).isGreaterThan(0L); // PM 우세
    }

    @Test
    void 정오를_가로지르면_0을_반환한다() {
      // given (11:00~13:00 → AM=PM 동률)
      BaseStats balanced = BaseStatsFixture.createAcrossNoonBalancedStat(goalId, today);

      // when
      long actual = balanced.getTimePart();

      // then
      assertThat(actual).isZero();
    }

    @Test
    void 정오에_시작_종료하면_0을_반환한다() {
      // given (12:00~12:00)
      BaseStats zero = BaseStatsFixture.createNoonZeroStat(goalId, today);

      // when
      long actual = zero.getTimePart();

      // then
      assertThat(actual).isZero();
    }

  }

  @Nested
  class 요일_반환_getDayOfWeek_테스트 {

    @Test
    void scheduledOn의_DayOfWeek를_그대로_반환한다() {
      // given
      LocalDate monday = LocalDate.of(2025, 8, 4);
      BaseStats stat = BaseStatsFixture.createPmOnlyStat(goalId, monday);

      // when
      DayOfWeek actual = stat.getDayOfWeek();

      // then
      assertThat(actual).isEqualTo(DayOfWeek.MONDAY);
    }

  }

}
