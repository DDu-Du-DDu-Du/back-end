package com.ddudu.application.domain.goal.dto.response;

import com.ddudu.application.domain.goal.domain.Goal;
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
