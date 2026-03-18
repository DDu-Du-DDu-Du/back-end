package com.ddudu.application.planning.todo.model;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;

import com.ddudu.application.common.dto.todo.GoalGroupedTodos;
import com.ddudu.application.common.dto.todo.TimeGroupedTodos;
import com.ddudu.application.common.dto.todo.TodoForTimetable;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Timetable {

  private final Map<Integer, List<Todo>> timetable;
  private final TodoList unassignedTodos;

  public Timetable(List<Todo> todos) {
    checkArgument(nonNull(todos));

    Map<Boolean, List<Todo>> split = todos.stream()
        .collect(groupingBy((Todo::hasStartTime)));

    List<Todo> assignedTodos = split.getOrDefault(true, new ArrayList<>());
    timetable = assignedTodos.stream()
        .collect(groupingBy(Todo::getBeginHour));
    unassignedTodos = new TodoList(split.getOrDefault(false, new ArrayList<>()));
  }

  public List<TimeGroupedTodos> getTimeGroupedTodos(List<Goal> goals) {
    Map<Long, String> goalColorMap = mapGoalsToColors(goals);

    return timetable.entrySet()
        .stream()
        .map(entry ->
            TimeGroupedTodos.of(
                LocalTime.of(entry.getKey(), 0),
                toTodoForTimetableList(entry.getValue(), goalColorMap)
            )
        )
        .toList();
  }

  public List<GoalGroupedTodos> getUnassignedTodosWithGoal(List<Goal> goals) {
    return unassignedTodos.getTodosWithGoal(goals);
  }

  private Map<Long, String> mapGoalsToColors(List<Goal> goals) {
    return goals.stream()
        .collect(Collectors.toMap(Goal::getId, Goal::getColor));
  }

  private List<TodoForTimetable> toTodoForTimetableList(
      List<Todo> todos, Map<Long, String> goalColorMap
  ) {
    return todos.stream()
        .map((todo) -> TodoForTimetable.of(todo, goalColorMap.get(todo.getGoalId())))
        .toList();
  }

}
