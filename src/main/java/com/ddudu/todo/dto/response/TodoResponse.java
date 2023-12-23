package com.ddudu.todo.dto.response;

import com.ddudu.goal.domain.Goal;
import com.ddudu.todo.domain.Todo;
import lombok.Builder;

@Builder
public record TodoResponse(
    Long id,
    Long goalId,
    String goalName,
    String name,
    String status
) {

  public static TodoResponse from(Todo todo) {
    Goal goal = todo.getGoal();

    return TodoResponse.builder()
        .id(todo.getId())
        .goalId(goal.getId())
        .goalName(goal.getName())
        .name(todo.getName())
        .status(todo.getStatus()
            .name())
        .build();
  }

}
