package com.ddudu.application.planning.ddudu.dto.response;

import com.ddudu.application.planning.ddudu.dto.GoalGroupedDdudus;
import com.ddudu.application.planning.ddudu.dto.TimeGroupedDdudus;
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
