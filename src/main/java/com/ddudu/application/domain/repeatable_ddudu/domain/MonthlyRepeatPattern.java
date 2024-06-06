package com.ddudu.application.domain.repeatable_ddudu.domain;

import static com.google.common.base.Preconditions.checkArgument;

import com.ddudu.application.domain.repeatable_ddudu.exception.RepeatableDduduErrorCode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.Builder;

public class MonthlyRepeatPattern implements RepeatPattern {

  private final List<Integer> repeatedDatesOfMonth;
  private final boolean includeLastDay;

  @Builder
  public MonthlyRepeatPattern(List<Integer> repeatedDatesOfMonth, Boolean includeLastDay) {
    checkArgument(
        !repeatedDatesOfMonth.isEmpty(),
        RepeatableDduduErrorCode.EMPTY_REPEAT_DATES_OF_MONTH.getCodeName()
    );
    this.repeatedDatesOfMonth = repeatedDatesOfMonth;
    this.includeLastDay = Objects.requireNonNullElse(includeLastDay, false);
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
    for (Integer repeatDate : repeatedDatesOfMonth) {
      addRepeatDate(dates, repeatDate, month);
    }
  }

  private void addRepeatDate(List<LocalDate> dates, Integer repeatDate, YearMonth month) {
    if (repeatDate > 0 && repeatDate <= month.lengthOfMonth()) {
      dates.add(month.atDay(repeatDate));
    }
  }

  private void addLastDayOfMonthIfNeeded(List<LocalDate> dates, YearMonth month) {
    if (includeLastDay && !dates.contains(month.atEndOfMonth())) {
      dates.add(month.atEndOfMonth());
    }
  }

  private boolean isBetween(LocalDate date, LocalDate startDate, LocalDate endDate) {
    return !date.isBefore(startDate) && !date.isAfter(endDate);
  }

}
