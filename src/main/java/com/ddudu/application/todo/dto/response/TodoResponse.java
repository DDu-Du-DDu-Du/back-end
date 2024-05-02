package com.ddudu.application.todo.dto.response;

import com.ddudu.application.goal.domain.Goal;
import com.ddudu.application.todo.domain.Todo;
import lombok.Builder;

@Builder
public record TodoResponse(
    GoalInfo goal,
    TodoInfo todo
) {

  public static TodoResponse from(Todo todo) {
    Goal goal = todo.getGoal();

    return TodoResponse.builder()
        .goal(GoalInfo.from(goal))
        .todo(TodoInfo.from(todo))
        .build();
  }

}
