package com.ddudu.application.common.dto.ddudu.response;

import com.ddudu.application.common.dto.ddudu.GoalGroupedDdudus;
import com.ddudu.application.common.dto.ddudu.TimeGroupedDdudus;
import java.util.List;

public record TimetableResponse(
    List<TimeGroupedDdudus> timetable,
    List<GoalGroupedDdudus> unassignedDdudus
) {

  public static TimetableResponse of(
      List<TimeGroupedDdudus> timetable, List<GoalGroupedDdudus> unassignedDdudus
  ) {
    return new TimetableResponse(timetable, unassignedDdudus);
  }

}
