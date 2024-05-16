package com.ddudu.old.todo.dto.response;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.old.todo.domain.Todo;
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
