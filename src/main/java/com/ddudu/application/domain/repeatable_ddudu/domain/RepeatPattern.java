package com.ddudu.application.domain.repeatable_ddudu.domain;

import com.ddudu.application.domain.repeatable_ddudu.domain.enums.RepeatType;
import com.ddudu.application.domain.repeatable_ddudu.util.DayOfWeekUtil;
import java.time.LocalDate;
import java.util.List;

public interface RepeatPattern {

  static RepeatPattern create(
      RepeatType repeatType,
      List<String> repeatDays,
      List<Integer> repeatDates,
      Boolean lastDayOfMonth
  ) {
    return switch (repeatType) {
      case DAILY -> new DailyRepeatPattern();
      case WEEKLY -> new WeeklyRepeatPattern(DayOfWeekUtil.toDaysOfWeek(repeatDays));
      case MONTHLY -> new MonthlyRepeatPattern(repeatDates, lastDayOfMonth);
    };
  }

  List<LocalDate> calculateRepetitionDates(LocalDate startDate, LocalDate endDate);

}
