package com.ddudu.application.domain.goal.dto.response;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.GoalStatus;
import lombok.Builder;

@Builder
public record GoalSummaryResponse(
    Long id,
    String name,
    GoalStatus status,
    String color
) {

  public static GoalSummaryResponse from(Goal goal) {
    return GoalSummaryResponse.builder()
        .id(goal.getId())
        .name(goal.getName())
        .status(goal.getStatus())
        .color(goal.getColor())
        .build();
  }

}
