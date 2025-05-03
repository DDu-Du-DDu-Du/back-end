package com.ddudu.domain.planning.repeatddudu.util;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.ddudu.domain.planning.repeatddudu.exception.RepeatDduduErrorCode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class DayOfWeekUtil {

  private static final DayOfWeek FIRST_DAY_OF_WEEK = DayOfWeek.MONDAY;
  private static final Map<String, DayOfWeek> dayOfWeekMap = new HashMap<>();

  static {
    for (DayOfWeek day : DayOfWeek.values()) {
      String koreanDay = day.getDisplayName(TextStyle.SHORT, Locale.KOREAN);
      dayOfWeekMap.put(koreanDay, day);
      dayOfWeekMap.put(day.name(), day);
    }
  }

  public static DayOfWeek toDaysOfWeek(String dayOfWeek) {
    checkArgument(nonNull(dayOfWeek), RepeatDduduErrorCode.INVALID_DAY_OF_WEEK.getCodeName());
    return dayOfWeekMap.get(dayOfWeek);
  }

  public static List<DayOfWeek> toDaysOfWeek(List<String> daysOfWeek) {
    if (daysOfWeek == null) {
      return null;
    }

    return daysOfWeek.stream()
        .map(DayOfWeekUtil::toDaysOfWeek)
        .toList();
  }

  public static LocalDate getFirstDayOfWeek(LocalDate date) {
    return isNull(date) ? LocalDate.now()
        .with(FIRST_DAY_OF_WEEK) : date.with(FIRST_DAY_OF_WEEK);
  }

}
