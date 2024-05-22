package com.ddudu.old.todo.dto.response;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.goal.domain.Goal;
import lombok.Builder;

@Builder
public record TodoResponse(
    GoalInfo goal,
    TodoInfo todo
) {

  public static TodoResponse from(Ddudu ddudu) {
    Goal goal = ddudu.getGoal();

    return TodoResponse.builder()
        .goal(GoalInfo.from(goal))
        .todo(TodoInfo.from(ddudu))
        .build();
  }

}
