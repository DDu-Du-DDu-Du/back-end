package com.ddudu.application.domain.repeat_ddudu.domain;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

import com.ddudu.application.domain.repeat_ddudu.util.DayOfWeekUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.beans.ConstructorProperties;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
import lombok.Builder;

public class WeeklyRepeatPattern implements RepeatPattern {

  private final List<DayOfWeek> repeatDaysOfWeek;

  @Builder
  @ConstructorProperties({"repeatDaysOfWeek"})
  public WeeklyRepeatPattern(
      @JsonProperty(access = READ_ONLY)
      List<String> repeatDaysOfWeek
  ) {
    this.repeatDaysOfWeek = DayOfWeekUtil.toDaysOfWeek(repeatDaysOfWeek);
  }

  @Override
  public List<LocalDate> calculateRepeatDates(LocalDate startDate, LocalDate endDate) {
    return Stream.iterate(startDate, date -> date.plusDays(1))
        .limit(countDaysBetween(startDate, endDate))
        .filter(date -> repeatDaysOfWeek.contains(date.getDayOfWeek()))
        .toList();
  }

  private long countDaysBetween(LocalDate startDate, LocalDate endDate) {
    return startDate.until(endDate)
        .getDays() + 1;
  }

}
