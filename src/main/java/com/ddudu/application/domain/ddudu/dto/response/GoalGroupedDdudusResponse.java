package com.ddudu.application.domain.ddudu.dto.response;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.dto.response.GoalInfo;
import java.util.List;
import lombok.Builder;

@Builder
public record GoalGroupedDdudusResponse(
    GoalInfo goal,
    List<DduduInfo> ddudus
) {

  public static GoalGroupedDdudusResponse from(Goal goal, List<DduduInfo> todos) {
    return GoalGroupedDdudusResponse.builder()
        .goal(GoalInfo.from(goal))
        .ddudus(todos)
        .build();
  }

}
