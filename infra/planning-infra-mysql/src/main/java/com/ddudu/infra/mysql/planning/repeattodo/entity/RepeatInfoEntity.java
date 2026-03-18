package com.ddudu.infra.mysql.planning.repeattodo.entity;

import com.ddudu.domain.planning.repeattodo.aggregate.vo.RepeatInfo;
import java.util.List;

public record RepeatInfoEntity(
    List<String> repeatDaysOfWeek,
    List<Integer> repeatDaysOfMonth,
    Boolean lastDayOfMonth
) {

  public static RepeatInfoEntity from(RepeatInfo repeatInfo) {
    return new RepeatInfoEntity(
        repeatInfo.repeatDaysOfWeek(),
        repeatInfo.repeatDaysOfMonth(),
        repeatInfo.lastDayOfMonth()
    );
  }

}
