package com.ddudu.application.domain.repeat_ddudu.domain;

import com.ddudu.application.domain.repeat_ddudu.domain.enums.RepeatType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.LocalDate;
import java.util.List;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type"
)
@JsonSubTypes(
    {
        @JsonSubTypes.Type(value = DailyRepeatPattern.class, name = "DAILY"),
        @JsonSubTypes.Type(value = WeeklyRepeatPattern.class, name = "WEEKLY"),
        @JsonSubTypes.Type(value = MonthlyRepeatPattern.class, name = "MONTHLY")
    }
)
public interface RepeatPattern {

  static RepeatPattern create(
      RepeatType repeatType,
      List<String> repeatDays,
      List<Integer> repeatDates,
      Boolean lastDayOfMonth
  ) {
    return switch (repeatType) {
      case DAILY -> new DailyRepeatPattern();
      case WEEKLY -> WeeklyRepeatPattern.withValidation(repeatDays);
      case MONTHLY -> MonthlyRepeatPattern.withValidation(repeatDates, lastDayOfMonth);
    };
  }

  List<LocalDate> calculateRepeatDates(LocalDate startDate, LocalDate endDate);

}
