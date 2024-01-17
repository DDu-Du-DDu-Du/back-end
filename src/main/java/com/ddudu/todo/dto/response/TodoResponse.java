package com.ddudu.todo.dto.response;

import com.ddudu.goal.domain.Goal;
import com.ddudu.todo.domain.Todo;
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
