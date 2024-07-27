package com.ddudu.application.domain.repeat_ddudu.domain;

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

  List<LocalDate> calculateRepeatDates(LocalDate startDate, LocalDate endDate);

}
