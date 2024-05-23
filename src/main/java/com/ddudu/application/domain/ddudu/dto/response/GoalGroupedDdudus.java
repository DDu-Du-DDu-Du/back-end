package com.ddudu.application.domain.ddudu.dto.response;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.dto.response.GoalInfo;
import java.util.List;
import lombok.Builder;

@Builder
public record GoalGroupedDdudus(
    GoalInfo goal,
    List<DduduInfo> ddudus
) {

  public static GoalGroupedDdudus of(Goal goal, List<DduduInfo> todos) {
    return GoalGroupedDdudus.builder()
        .goal(GoalInfo.from(goal))
        .ddudus(todos)
        .build();
  }

}
