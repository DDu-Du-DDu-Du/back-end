package com.ddudu.application.domain.period_goal.domain.vo;

import com.ddudu.application.domain.period_goal.domain.enums.PeriodGoalType;
import java.time.DayOfWeek;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class PeriodGoalDate {

  private final LocalDate date;

  private PeriodGoalDate(LocalDate date) {
    this.date = date;
  }

  public static PeriodGoalDate of(PeriodGoalType type, LocalDate date) {
    return switch (type) {
      case WEEK -> new PeriodGoalDate(getTuesdayOfWeek(date));
      case MONTH -> new PeriodGoalDate(getFirstDayOfMonth(date));
    };
  }

  private static LocalDate getTuesdayOfWeek(LocalDate date) {
    return date.with(DayOfWeek.TUESDAY);
  }

  private static LocalDate getFirstDayOfMonth(LocalDate date) {
    return date.withDayOfMonth(1);
  }

}
