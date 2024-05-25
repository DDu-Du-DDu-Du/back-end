package com.ddudu.application.domain.ddudu.dto.response;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public record TimetableResponse(
    Map<LocalTime, List<DduduWithColorInfo>> timetable,
    List<GoalGroupedDdudus> unassignedDdudus
) {

  public static TimetableResponse of(
      Map<LocalTime, List<DduduWithColorInfo>> timetable, List<GoalGroupedDdudus> unassignedDdudus
  ) {
    return new TimetableResponse(timetable, unassignedDdudus);
  }

}
