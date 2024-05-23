package com.ddudu.old.todo.dto.response;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.dto.response.DduduInfo;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.dto.response.GoalInfo;
import lombok.Builder;

@Builder
public record TodoResponse(
    GoalInfo goal,
    DduduInfo todo
) {

  public static TodoResponse from(Ddudu ddudu) {
    Goal goal = ddudu.getGoal();

    return TodoResponse.builder()
        .goal(GoalInfo.from(goal))
        .todo(DduduInfo.from(ddudu))
        .build();
  }

}
