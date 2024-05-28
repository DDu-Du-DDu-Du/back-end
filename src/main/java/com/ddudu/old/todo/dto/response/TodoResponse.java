package com.ddudu.old.todo.dto.response;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.dto.ddudu.response.BasicDduduResponse;
import com.ddudu.application.dto.goal.response.BasicGoalResponse;
import lombok.Builder;

@Builder
public record TodoResponse(
    BasicGoalResponse goal,
    BasicDduduResponse todo
) {

  public static TodoResponse from(Ddudu ddudu) {
    Goal goal = ddudu.getGoal();

    return TodoResponse.builder()
        .goal(BasicGoalResponse.from(goal))
        .todo(BasicDduduResponse.from(ddudu))
        .build();
  }

}
