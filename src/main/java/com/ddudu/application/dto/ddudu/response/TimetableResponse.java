package com.ddudu.application.dto.ddudu.response;

import com.ddudu.application.dto.ddudu.BasicDduduWithGoalId;
import com.ddudu.application.dto.ddudu.GoalGroupedDdudus;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public record TimetableResponse(
    Map<LocalTime, List<BasicDduduWithGoalId>> timetable,
    List<GoalGroupedDdudus> unassignedDdudus
) {

  public static TimetableResponse of(
      Map<LocalTime, List<BasicDduduWithGoalId>> timetable, List<GoalGroupedDdudus> unassignedDdudus
  ) {
    return new TimetableResponse(timetable, unassignedDdudus);
  }

}
