package com.ddudu.infra.mysql.planning.repeatddudu.entity;

import com.ddudu.domain.planning.repeatddudu.aggregate.vo.RepeatInfo;
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
