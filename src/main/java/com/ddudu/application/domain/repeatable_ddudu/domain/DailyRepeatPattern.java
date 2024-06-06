package com.ddudu.application.domain.repeatable_ddudu.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

public class DailyRepeatPattern implements RepeatPattern {

  @Override
  public List<LocalDate> calculateRepetitionDates(LocalDate startDate, LocalDate endDate) {
    return Stream.iterate(startDate, date -> date.plusDays(1))
        .limit(countDaysBetween(startDate, endDate))
        .toList();
  }

  private long countDaysBetween(LocalDate startDate, LocalDate endDate) {
    return startDate.until(endDate)
        .getDays() + 1;
  }

}
