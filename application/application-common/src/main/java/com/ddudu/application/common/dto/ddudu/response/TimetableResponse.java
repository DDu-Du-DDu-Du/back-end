package com.ddudu.application.common.dto.ddudu.response;

import com.ddudu.application.common.dto.ddudu.GoalGroupedTodos;
import com.ddudu.application.common.dto.ddudu.TimeGroupedTodos;
import java.util.List;

public record TimetableResponse(
    List<TimeGroupedTodos> timetable,
    List<GoalGroupedTodos> unassignedTodos
) {

  public static TimetableResponse of(
      List<TimeGroupedTodos> timetable, List<GoalGroupedTodos> unassignedTodos
  ) {
    return new TimetableResponse(timetable, unassignedTodos);
  }

}
