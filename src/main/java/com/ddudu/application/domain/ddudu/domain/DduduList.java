package com.ddudu.application.domain.ddudu.domain;

import static com.google.gson.internal.$Gson$Preconditions.checkArgument;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.dto.ddudu.GoalGroupedDdudus;
import com.ddudu.application.dto.ddudu.response.BasicDduduResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DduduList {

  private final List<Ddudu> ddudus;

  public DduduList(List<Ddudu> ddudus) {
    checkArgument(nonNull(ddudus));
    this.ddudus = ddudus;
  }

  public List<GoalGroupedDdudus> getDdudusWithGoal(List<Goal> goals) {
    Map<Long, Goal> goalsById = mapGoalById(goals);
    Map<Long, List<Ddudu>> ddudusByGoal = mapDdudusByGoal();

    return goalsById.entrySet()
        .stream()
        .map(entry -> GoalGroupedDdudus.of(
            entry.getValue(),
            ddudusByGoal.getOrDefault(entry.getKey(), new ArrayList<>())
                .stream()
                .map(BasicDduduResponse::from)
                .toList()
        ))
        .toList();
  }

  private Map<Long, Goal> mapGoalById(List<Goal> goals) {
    return goals.stream()
        .collect(Collectors.toMap(Goal::getId, Function.identity()));
  }

  private Map<Long, List<Ddudu>> mapDdudusByGoal() {
    return ddudus.stream()
        .collect(groupingBy(Ddudu::getGoalId));
  }

}
