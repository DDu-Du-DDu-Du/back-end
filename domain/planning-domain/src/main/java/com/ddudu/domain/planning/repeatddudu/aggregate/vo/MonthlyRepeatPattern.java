package com.ddudu.domain.planning.repeatddudu.aggregate.vo;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.ddudu.common.exception.RepeatDduduErrorCode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class MonthlyRepeatPattern implements RepeatPattern {

  private static final Boolean DEFAULT_LAST_DAY = false;

  private final List<Integer> repeatDaysOfMonth;
  private final Boolean lastDay;

  public MonthlyRepeatPattern(
      List<Integer> repeatDaysOfMonth,
      Boolean lastDay
  ) {
    validate(repeatDaysOfMonth, lastDay);
    this.repeatDaysOfMonth = isNull(repeatDaysOfMonth) ? List.of() : repeatDaysOfMonth;
    this.lastDay = isNull(lastDay) ? DEFAULT_LAST_DAY : lastDay;
  }

  @Override
  public RepeatInfo getInfo() {
    return RepeatInfo.month(repeatDaysOfMonth, lastDay);
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

  private void validate(List<Integer> repeatDaysOfMonth, Boolean lastDay) {
    if (nonNull(lastDay)) {
      return;
    }

    checkArgument(
        nonNull(repeatDaysOfMonth) && !repeatDaysOfMonth.isEmpty(),
        RepeatDduduErrorCode.NULL_OR_EMPTY_REPEAT_DATES_OF_MONTH.getCodeName()
    );
  }

}
