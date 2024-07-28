package com.ddudu.application.domain.repeat_ddudu.domain;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.nonNull;

import com.ddudu.application.domain.repeat_ddudu.exception.RepeatDduduErrorCode;
import com.ddudu.application.domain.repeat_ddudu.util.DayOfWeekUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.beans.ConstructorProperties;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

public class WeeklyRepeatPattern implements RepeatPattern {

  private final List<DayOfWeek> repeatDaysOfWeek;

  @ConstructorProperties({"repeatDaysOfWeek"})
  public WeeklyRepeatPattern(
      @JsonProperty(access = READ_ONLY)
      List<String> repeatDaysOfWeek
  ) {
    this.repeatDaysOfWeek = DayOfWeekUtil.toDaysOfWeek(repeatDaysOfWeek);
  }

  public static WeeklyRepeatPattern withValidation(List<String> repeatDaysOfWeek) {
    validate(repeatDaysOfWeek);
    return new WeeklyRepeatPattern(repeatDaysOfWeek);
  }

  private static void validate(List<String> repeatDaysOfWeek) {
    checkArgument(
        nonNull(repeatDaysOfWeek) && !repeatDaysOfWeek.isEmpty(),
        RepeatDduduErrorCode.NULL_OR_EMPTY_REPEAT_DAYS_OF_WEEK.getCodeName()
    );
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
