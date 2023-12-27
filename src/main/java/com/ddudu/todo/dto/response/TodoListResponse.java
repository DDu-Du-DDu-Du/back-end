package com.ddudu.todo.dto.response;

import com.ddudu.goal.domain.Goal;
import java.util.List;
import lombok.Builder;

@Builder
public record TodoListResponse(
    GoalInfo goalInfo,
    List<TodoInfo> todolist
) {

  public static TodoListResponse from(Goal goal, List<TodoInfo> todos) {
    return TodoListResponse.builder()
        .goalInfo(GoalInfo.from(goal))
        .todolist(todos)
        .build();
  }

}
