package com.ddudu.application.domain.ddudu.dto;

import com.ddudu.application.domain.ddudu.dto.response.BasicDduduResponse;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.dto.response.GoalInfo;
import java.util.List;
import lombok.Builder;

@Builder
public record GoalGroupedDdudus(
    GoalInfo goal,
    List<BasicDduduResponse> ddudus
) {

  public static GoalGroupedDdudus of(Goal goal, List<BasicDduduResponse> todos) {
    return GoalGroupedDdudus.builder()
        .goal(GoalInfo.from(goal))
        .ddudus(todos)
        .build();
  }

}
