package com.ddudu.old.todo.dto.response;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.dto.response.BasicDduduResponse;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.dto.response.GoalInfo;
import lombok.Builder;

@Builder
public record TodoResponse(
    GoalInfo goal,
    BasicDduduResponse todo
) {

  public static TodoResponse from(Ddudu ddudu) {
    Goal goal = ddudu.getGoal();

    return TodoResponse.builder()
        .goal(GoalInfo.from(goal))
        .todo(BasicDduduResponse.from(ddudu))
        .build();
  }

}
