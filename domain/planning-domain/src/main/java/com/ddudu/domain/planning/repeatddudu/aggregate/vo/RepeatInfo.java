package com.ddudu.domain.planning.repeatddudu.aggregate.vo;

import java.util.List;
import lombok.Builder;

@Builder
public record RepeatInfo(
    List<String> repeatDaysOfWeek,
    List<Integer> repeatDaysOfMonth,
    Boolean lastDayOfMonth
) {

  public static RepeatInfo day() {
    return RepeatInfo.builder()
        .build();
  }

  public static RepeatInfo week(List<String> repeatDaysOfWeek) {
    return RepeatInfo.builder()
        .repeatDaysOfWeek(repeatDaysOfWeek)
        .build();
  }

  public static RepeatInfo month(List<Integer> repeatDaysOfMonth, Boolean lastDayOfMonth) {
    return RepeatInfo.builder()
        .repeatDaysOfMonth(repeatDaysOfMonth)
        .lastDayOfMonth(lastDayOfMonth)
        .build();
  }

}
