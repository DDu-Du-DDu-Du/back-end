package com.ddudu.application.common.dto.ddudu;

import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.application.common.dto.ddudu.response.BasicDduduResponse;
import com.ddudu.application.common.dto.goal.response.BasicGoalResponse;
import java.util.List;
import lombok.Builder;

@Builder
public record GoalGroupedDdudus(
    BasicGoalResponse goal,
    List<BasicDduduResponse> ddudus
) {

  public static GoalGroupedDdudus of(Goal goal, List<BasicDduduResponse> todos) {
    return GoalGroupedDdudus.builder()
        .goal(BasicGoalResponse.from(goal))
        .ddudus(todos)
        .build();
  }

}
