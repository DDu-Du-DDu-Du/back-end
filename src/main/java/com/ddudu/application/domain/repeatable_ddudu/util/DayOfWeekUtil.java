package com.ddudu.application.domain.repeatable_ddudu.util;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.nonNull;

import com.ddudu.application.domain.repeatable_ddudu.exception.RepeatableDduduErrorCode;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class DayOfWeekUtil {

  private static final Map<String, DayOfWeek> dayOfWeekMap = new HashMap<>();

  static {
    for (DayOfWeek day : DayOfWeek.values()) {
      String koreanDay = day.getDisplayName(TextStyle.SHORT, Locale.KOREAN);
      dayOfWeekMap.put(koreanDay, day);
    }
  }

  public static DayOfWeek toDaysOfWeek(String dayOfWeek) {
    checkArgument(nonNull(dayOfWeek), RepeatableDduduErrorCode.INVALID_DAY_OF_WEEK.getCodeName());
    return dayOfWeekMap.get(dayOfWeek);
  }

  public static List<DayOfWeek> toDaysOfWeek(List<String> dayOfWeeks) {
    return dayOfWeeks.stream()
        .map(DayOfWeekUtil::toDaysOfWeek)
        .toList();
  }

}
