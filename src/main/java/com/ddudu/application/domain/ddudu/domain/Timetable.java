package com.ddudu.application.domain.ddudu.domain;

import static com.google.gson.internal.$Gson$Preconditions.checkArgument;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.dto.ddudu.BasicDduduWithGoalIdAndTime;
import com.ddudu.application.dto.ddudu.GoalGroupedDdudus;
import com.ddudu.application.dto.ddudu.TimeGroupedDdudus;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Timetable {

  private final Map<Integer, List<Ddudu>> timetable;
  private final DduduList unassignedDdudus;

  public Timetable(List<Ddudu> ddudus) {
    checkArgument(nonNull(ddudus));

    Map<Boolean, List<Ddudu>> split = ddudus.stream()
        .collect(groupingBy((Ddudu::hasStartTime)));

    List<Ddudu> assignedDdudus = split.getOrDefault(true, new ArrayList<>());
    timetable = assignedDdudus.stream()
        .collect(groupingBy(Ddudu::getBeginHour));
    unassignedDdudus = new DduduList(split.getOrDefault(false, new ArrayList<>()));
  }

  public List<TimeGroupedDdudus> getTimeGroupedDdudus() {
    return timetable.entrySet()
        .stream()
        .map(entry ->
            TimeGroupedDdudus.of(
                LocalTime.of(entry.getKey(), 0),
                entry.getValue()
                    .stream()
                    .map(BasicDduduWithGoalIdAndTime::of)
                    .toList()
            )
        )
        .toList();
  }

  public List<GoalGroupedDdudus> getUnassignedDdudusWithGoal(List<Goal> goals) {
    return unassignedDdudus.getDdudusWithGoal(goals);
  }

}
