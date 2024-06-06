package com.ddudu.application.domain.repeatable_ddudu.domain;

import static com.google.common.base.Preconditions.checkArgument;

import com.ddudu.application.domain.repeatable_ddudu.exception.RepeatableDduduErrorCode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
import lombok.Builder;

public class WeeklyRepeatPattern implements RepeatPattern {

  private final List<DayOfWeek> repeatedDaysOfWeek;

  @Builder
  public WeeklyRepeatPattern(List<DayOfWeek> repeatedDaysOfWeek) {
    checkArgument(
        !repeatedDaysOfWeek.isEmpty(),
        RepeatableDduduErrorCode.EMPTY_REPEAT_DAYS_OF_WEEK.getCodeName()
    );
    this.repeatedDaysOfWeek = repeatedDaysOfWeek;
  }

  @Override
  public List<LocalDate> calculateRepeatDates(LocalDate startDate, LocalDate endDate) {
    return Stream.iterate(startDate, date -> date.plusDays(1))
        .limit(countDaysBetween(startDate, endDate))
        .filter(date -> repeatedDaysOfWeek.contains(date.getDayOfWeek()))
        .toList();
  }

  private long countDaysBetween(LocalDate startDate, LocalDate endDate) {
    return startDate.until(endDate)
        .getDays() + 1;
  }

}
