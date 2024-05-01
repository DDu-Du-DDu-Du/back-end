package com.ddudu.application.goal.dto.response;

import com.ddudu.application.goal.domain.Goal;
import lombok.Builder;

@Builder
public record GoalSummaryResponse(
    Long id,
    String name,
    String status,
    String color
) {

  public static GoalSummaryResponse from(Goal goal) {
    return GoalSummaryResponse.builder()
        .id(goal.getId())
        .name(goal.getName())
        .status(goal.getStatus()
            .name())
        .color(goal.getColor())
        .build();
  }

}
