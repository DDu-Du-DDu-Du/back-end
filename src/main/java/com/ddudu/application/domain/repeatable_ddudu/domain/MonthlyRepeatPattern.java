package com.ddudu.application.domain.repeatable_ddudu.domain;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class MonthlyRepeatPattern implements RepeatPattern {

  private List<Integer> repeatedDatesOfMonth;
  private boolean includeLastDay;

  @Override
  public List<LocalDate> calculateRepetitionDates(LocalDate startDate, LocalDate endDate) {
    return Stream.iterate(startDate.withDayOfMonth(1), date -> date.plusMonths(1))
        .limit(countMonthsBetween(startDate, endDate))
        .map(YearMonth::from)
        .flatMap(this::getDatesToBeRepeatIn)
        .filter(date -> isBetween(date, startDate, endDate))
        .toList();
  }

  private long countMonthsBetween(LocalDate startDate, LocalDate endDate) {
    return YearMonth.from(startDate)
        .until(endDate, ChronoUnit.MONTHS) + 1;
  }

  private Stream<LocalDate> getDatesToBeRepeatIn(YearMonth month) {
    List<LocalDate> dates = new ArrayList<>();

    addDatesToBeRepeatIn(dates, month);
    addLastDayOfMonthIfNeeded(dates, month);

    return dates.stream();
  }

  private void addDatesToBeRepeatIn(List<LocalDate> dates, YearMonth month) {
    for (Integer dateToBeRepeat : repeatedDatesOfMonth) {
      addDateToBeRepeat(dates, dateToBeRepeat, month);
    }
  }

  private void addDateToBeRepeat(List<LocalDate> dates, Integer dayToRepeat, YearMonth month) {
    if (dayToRepeat > 0 && dayToRepeat <= month.lengthOfMonth()) {
      dates.add(month.atDay(dayToRepeat));
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
