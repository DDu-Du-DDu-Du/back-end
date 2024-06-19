package com.ddudu.application.dto.repeat_ddudu;

import com.ddudu.application.dto.repeat_ddudu.request.CreateRepeatDduduRequest;
import java.util.List;

public record RepeatPatternDto(
    List<String> repeatDaysOfWeek,
    List<Integer> repeatDaysOfMonth,
    Boolean lastDayOfMonth
) {

  public static RepeatPatternDto from(CreateRepeatDduduRequest request) {
    return new RepeatPatternDto(
        request.repeatDaysOfWeek(),
        request.repeatDaysOfMonth(),
        request.lastDayOfMonth()
    );
  }

}
