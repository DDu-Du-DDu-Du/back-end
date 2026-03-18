package com.ddudu.application.planning.ddudu.model;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;

import com.ddudu.application.common.dto.ddudu.GoalGroupedTodos;
import com.ddudu.application.common.dto.ddudu.response.BasicTodoResponse;
import com.ddudu.application.common.dto.goal.response.BasicGoalResponse;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.goal.aggregate.enums.GoalStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TodoList {

  private final List<Todo> ddudus;

  public TodoList(List<Todo> ddudus) {
    checkArgument(nonNull(ddudus));

    this.ddudus = ddudus;
  }

  public List<GoalGroupedTodos> getTodosWithGoal(List<Goal> goals) {
    Map<Long, Goal> goalsById = mapGoalById(goals);
    Map<Long, List<Todo>> ddudusByGoal = mapTodosByGoal();

    return goalsById.entrySet()
        .stream()
        .map(entry -> GoalGroupedTodos.of(
            entry.getValue(),
            ddudusByGoal.getOrDefault(entry.getKey(), new ArrayList<>())
                .stream()
                .map(BasicTodoResponse::from)
                .toList()
        ))
        .filter((groupedTodos) -> !isDoneGoalAndEmpty(groupedTodos)) // 종료된 목표는 하위 뚜두가 있을 때만 조회
        .toList();
  }

  private boolean isDoneGoalAndEmpty(GoalGroupedTodos goalGroupedTodos) {
    BasicGoalResponse goal = goalGroupedTodos.goal();
    List<BasicTodoResponse> ddudus = goalGroupedTodos.ddudus();

    return goal.status() == GoalStatus.DONE && ddudus.isEmpty();
  }

  private Map<Long, Goal> mapGoalById(List<Goal> goals) {
    return goals.stream()
        .collect(Collectors.toMap(Goal::getId, Function.identity()));
  }

  private Map<Long, List<Todo>> mapTodosByGoal() {
    return ddudus.stream()
        .collect(groupingBy(Todo::getGoalId));
  }

}
