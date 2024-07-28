package com.ddudu.application.domain.period_goal.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;

import com.ddudu.application.domain.period_goal.domain.enums.PeriodGoalType;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class PeriodGoalDateTest {

  @Nested
  class 생성_테스트 {

    @Test
    void 주간_목표_날짜는_입력_날짜가_속한_주의_화요일로_저장된다() {
      // given
      PeriodGoalType weekType = PeriodGoalType.WEEK;
      LocalDate inputDate = LocalDate.now();

      // when
      PeriodGoalDate periodGoalDate = PeriodGoalDate.of(weekType, inputDate);

      // then
      LocalDate actual = periodGoalDate.getDate();
      assertThat(getWeekOfMonth(actual)).isEqualTo(getWeekOfMonth(inputDate));
      assertThat(actual.getDayOfWeek()).isEqualTo(DayOfWeek.TUESDAY);
    }

    @Test
    void 월간_목표의_날짜는_입력_날짜가_속한_달의_첫날로_저장된다() {
      // given
      PeriodGoalType monthType = PeriodGoalType.MONTH;
      LocalDate inputDate = LocalDate.now();

      // when
      PeriodGoalDate periodGoalDate = PeriodGoalDate.of(monthType, inputDate);

      // then
      LocalDate actual = periodGoalDate.getDate();
      assertThat(actual.getMonth()).isEqualTo(inputDate.getMonth());
      assertThat(actual.getDayOfMonth()).isEqualTo(1);
    }

    private int getWeekOfMonth(LocalDate date) {
      WeekFields weekFields = WeekFields.ISO;
      return date.get(weekFields.weekOfMonth());
    }

  }

}
