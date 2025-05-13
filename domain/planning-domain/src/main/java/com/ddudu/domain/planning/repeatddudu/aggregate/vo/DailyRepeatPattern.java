package com.ddudu.domain.planning.repeatddudu.aggregate.vo;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

public class DailyRepeatPattern implements RepeatPattern {

  @Override
  public RepeatInfo getInfo() {
    return RepeatInfo.day();
  }

  @Override
  public List<LocalDate> calculateRepeatDates(LocalDate startDate, LocalDate endDate) {
    return Stream.iterate(startDate, date -> date.plusDays(1))
        .limit(countDaysBetween(startDate, endDate))
        .toList();
  }

  private long countDaysBetween(LocalDate startDate, LocalDate endDate) {
    return startDate.until(endDate)
        .getDays() + 1;
  }

}
