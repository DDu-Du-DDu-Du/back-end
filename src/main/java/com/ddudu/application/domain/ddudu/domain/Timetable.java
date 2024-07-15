package com.ddudu.application.domain.ddudu.domain;

import static java.util.stream.Collectors.groupingBy;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.dto.ddudu.BasicDduduWithGoalIdAndTime;
import com.ddudu.application.dto.ddudu.GoalGroupedDdudus;
import com.ddudu.application.dto.ddudu.TimeGroupedDdudus;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Timetable {

  private final Map<Integer, List<Ddudu>> timetable;
  private final DduduList unassignedDdudus;

  public Timetable(List<Ddudu> ddudus) {
    assertNotNull(ddudus, "Timetable 생성 시 ddudus는 null일 수 없습니다.");

    Map<Boolean, List<Ddudu>> split = ddudus.stream()
        .collect(groupingBy((Ddudu::hasStartTime)));

    List<Ddudu> assignedDdudus = split.getOrDefault(true, new ArrayList<>());
    timetable = assignedDdudus.stream()
        .collect(groupingBy(Ddudu::getBeginHour));
    unassignedDdudus = new DduduList(split.getOrDefault(false, new ArrayList<>()));
  }

  private static Map<Long, Goal> mapGoalById(List<Goal> goals) {
    return goals.stream()
        .collect(Collectors.toMap(Goal::getId, Function.identity()));
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
