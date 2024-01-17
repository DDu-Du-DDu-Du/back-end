package com.ddudu.todo.dto.response;

import com.ddudu.goal.domain.Goal;
import java.util.List;
import lombok.Builder;

@Builder
public record TodoListResponse(
    GoalInfo goal,
    List<TodoInfo> todos
) {

  public static TodoListResponse from(Goal goal, List<TodoInfo> todos) {
    return TodoListResponse.builder()
        .goal(GoalInfo.from(goal))
        .todos(todos)
        .build();
  }

}
