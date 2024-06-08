package com.ddudu.application.domain.repeat_ddudu.domain;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.beans.ConstructorProperties;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.Builder;

public class MonthlyRepeatPattern implements RepeatPattern {

  @JsonProperty(access = READ_ONLY)
  private final List<Integer> repeatDaysOfMonth;
  @JsonProperty
  private final Boolean lastDay;

  @Builder
  @ConstructorProperties({"repeatDaysOfMonth", "lastDay"})
  public MonthlyRepeatPattern(
      List<Integer> repeatDaysOfMonth,
      Boolean lastDay
  ) {

    this.repeatDaysOfMonth = repeatDaysOfMonth;
    this.lastDay = lastDay;
  }

  @Override
  public List<LocalDate> calculateRepeatDates(LocalDate startDate, LocalDate endDate) {
    return Stream.iterate(startDate.withDayOfMonth(1), date -> date.plusMonths(1))
        .limit(countMonthsBetween(startDate, endDate))
        .map(YearMonth::from)
        .flatMap(this::getRepeatDatesIn)
        .filter(date -> isBetween(date, startDate, endDate))
        .toList();
  }

  private long countMonthsBetween(LocalDate startDate, LocalDate endDate) {
    return YearMonth.from(startDate)
        .until(endDate, ChronoUnit.MONTHS) + 1;
  }

  private Stream<LocalDate> getRepeatDatesIn(YearMonth month) {
    List<LocalDate> dates = new ArrayList<>();

    addRepeatDatesIn(dates, month);
    addLastDayOfMonthIfNeeded(dates, month);

    return dates.stream();
  }

  private void addRepeatDatesIn(List<LocalDate> dates, YearMonth month) {
    for (Integer repeatDate : repeatDaysOfMonth) {
      addRepeatDate(dates, repeatDate, month);
    }
  }

  private void addRepeatDate(List<LocalDate> dates, Integer repeatDate, YearMonth month) {
    if (repeatDate > 0 && repeatDate <= month.lengthOfMonth()) {
      dates.add(month.atDay(repeatDate));
    }
  }

  private void addLastDayOfMonthIfNeeded(List<LocalDate> dates, YearMonth month) {
    if (lastDay && !dates.contains(month.atEndOfMonth())) {
      dates.add(month.atEndOfMonth());
    }
  }

  private boolean isBetween(LocalDate date, LocalDate startDate, LocalDate endDate) {
    return !date.isBefore(startDate) && !date.isAfter(endDate);
  }

}
