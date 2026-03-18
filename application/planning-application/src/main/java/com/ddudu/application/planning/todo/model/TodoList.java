package com.ddudu.application.planning.todo.model;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;

import com.ddudu.application.common.dto.goal.response.BasicGoalResponse;
import com.ddudu.application.common.dto.todo.GoalGroupedTodos;
import com.ddudu.application.common.dto.todo.response.BasicTodoResponse;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.goal.aggregate.enums.GoalStatus;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TodoList {

  private final List<Todo> todos;

  public TodoList(List<Todo> todos) {
    checkArgument(nonNull(todos));

    this.todos = todos;
  }

  public List<GoalGroupedTodos> getTodosWithGoal(List<Goal> goals) {
    Map<Long, Goal> goalsById = mapGoalById(goals);
    Map<Long, List<Todo>> todosByGoal = mapTodosByGoal();

    return goalsById.entrySet()
        .stream()
        .map(entry -> GoalGroupedTodos.of(
            entry.getValue(),
            todosByGoal.getOrDefault(entry.getKey(), new ArrayList<>())
                .stream()
                .map(BasicTodoResponse::from)
                .toList()
        ))
        .filter((groupedTodos) -> !isDoneGoalAndEmpty(groupedTodos)) // 종료된 목표는 하위 투두가 있을 때만 조회
        .toList();
  }

  private boolean isDoneGoalAndEmpty(GoalGroupedTodos goalGroupedTodos) {
    BasicGoalResponse goal = goalGroupedTodos.goal();
    List<BasicTodoResponse> todos = goalGroupedTodos.todos();

    return goal.status() == GoalStatus.DONE && todos.isEmpty();
  }

  private Map<Long, Goal> mapGoalById(List<Goal> goals) {
    return goals.stream()
        .collect(Collectors.toMap(Goal::getId, Function.identity()));
  }

  private Map<Long, List<Todo>> mapTodosByGoal() {
    return todos.stream()
        .collect(groupingBy(Todo::getGoalId));
  }

}
