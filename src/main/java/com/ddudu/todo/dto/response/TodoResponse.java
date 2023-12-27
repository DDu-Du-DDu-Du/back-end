package com.ddudu.todo.dto.response;

import com.ddudu.goal.domain.Goal;
import com.ddudu.todo.domain.Todo;
import lombok.Builder;

@Builder
public record TodoResponse(
    GoalInfo goalInfo,
    TodoInfo todoInfo
) {

  public static TodoResponse from(Todo todo) {
    Goal goal = todo.getGoal();

    return TodoResponse.builder()
        .goalInfo(GoalInfo.from(goal))
        .todoInfo(TodoInfo.from(todo))
        .build();
  }

}
