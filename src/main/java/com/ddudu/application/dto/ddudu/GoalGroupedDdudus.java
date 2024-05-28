package com.ddudu.application.dto.ddudu;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.dto.ddudu.response.BasicDduduResponse;
import com.ddudu.application.dto.goal.response.BasicGoalResponse;
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
