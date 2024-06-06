package com.ddudu.application.domain.repeatable_ddudu.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class WeeklyRepeatPattern implements RepeatPattern {

  private List<DayOfWeek> repeatedDaysOfWeek;

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
