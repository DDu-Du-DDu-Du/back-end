package com.ddudu.domain.planning.repeatddudu.aggregate.vo;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.nonNull;

import com.ddudu.common.exception.RepeatDduduErrorCode;
import com.ddudu.domain.planning.repeatddudu.util.DayOfWeekUtil;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

public class WeeklyRepeatPattern implements RepeatPattern {

  private final List<DayOfWeek> repeatDaysOfWeek;

  public WeeklyRepeatPattern(List<String> repeatDaysOfWeek) {
    validate(repeatDaysOfWeek);

    this.repeatDaysOfWeek = DayOfWeekUtil.toDaysOfWeek(repeatDaysOfWeek);
  }

  @Override
  public RepeatInfo getInfo() {
    List<String> daysOfWeek = repeatDaysOfWeek.stream()
        .map(DayOfWeek::name)
        .toList();

    return RepeatInfo.week(daysOfWeek);
  }

  @Override
  public List<LocalDate> calculateRepeatDates(LocalDate startDate, LocalDate endDate) {
    return Stream.iterate(startDate, date -> date.plusDays(1))
        .limit(countDaysBetween(startDate, endDate))
        .filter(date -> repeatDaysOfWeek.contains(date.getDayOfWeek()))
        .toList();
  }

  private void validate(List<String> repeatDaysOfWeek) {
    checkArgument(
        nonNull(repeatDaysOfWeek) && !repeatDaysOfWeek.isEmpty(),
        RepeatDduduErrorCode.NULL_OR_EMPTY_REPEAT_DAYS_OF_WEEK.getCodeName()
    );
  }

  private long countDaysBetween(LocalDate startDate, LocalDate endDate) {
    return startDate.until(endDate)
        .getDays() + 1;
  }

}
