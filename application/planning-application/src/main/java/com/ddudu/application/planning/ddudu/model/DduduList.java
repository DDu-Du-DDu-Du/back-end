package com.ddudu.application.planning.ddudu.model;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;

import com.ddudu.application.common.dto.ddudu.GoalGroupedDdudus;
import com.ddudu.application.common.dto.ddudu.response.BasicDduduResponse;
import com.ddudu.application.common.dto.goal.response.BasicGoalResponse;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.goal.aggregate.enums.GoalStatus;
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
        .filter((groupedDdudus) -> !isDoneGoalAndEmpty(groupedDdudus)) // 종료된 목표는 하위 뚜두가 있을 때만 조회
        .toList();
  }

  private boolean isDoneGoalAndEmpty(GoalGroupedDdudus goalGroupedDdudus) {
    BasicGoalResponse goal = goalGroupedDdudus.goal();
    List<BasicDduduResponse> ddudus = goalGroupedDdudus.ddudus();

    return goal.status() == GoalStatus.DONE && ddudus.isEmpty();
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
