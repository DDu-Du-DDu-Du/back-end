package com.modoo.application.common.dto.goal.response;

import com.modoo.domain.planning.goal.aggregate.Goal;
import lombok.Builder;

@Builder
public record GoalIdResponse(
    Long id
) {

  public static GoalIdResponse from(Goal goal) {
    return GoalIdResponse.builder()
        .id(goal.getId())
        .build();
  }

}
