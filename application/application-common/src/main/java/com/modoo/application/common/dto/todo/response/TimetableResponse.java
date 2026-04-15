package com.modoo.application.common.dto.todo.response;

import com.modoo.application.common.dto.todo.GoalGroupedTodos;
import com.modoo.application.common.dto.todo.TimeGroupedTodos;
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
