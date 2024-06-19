package com.ddudu.application.dto.repeat_ddudu.request;

import java.util.List;

public record CreateRepeatPatternRequest(
    List<String> repeatDaysOfWeek,
    List<Integer> repeatDaysOfMonth,
    Boolean lastDayOfMonth
) {

  public static CreateRepeatPatternRequest from(CreateRepeatDduduRequest request) {
    return new CreateRepeatPatternRequest(
        request.repeatDaysOfWeek(),
        request.repeatDaysOfMonth(),
        request.lastDayOfMonth()
    );
  }

}
