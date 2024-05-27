package com.ddudu.application.domain.goal.dto.response;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.GoalStatus;
import lombok.Builder;

@Builder
public record BasicGoalWithStatusResponse(
    Long id,
    String name,
    GoalStatus status,
    String color
) {

  public static BasicGoalWithStatusResponse from(Goal goal) {
    return BasicGoalWithStatusResponse.builder()
        .id(goal.getId())
        .name(goal.getName())
        .status(goal.getStatus())
        .color(goal.getColor())
        .build();
  }

}
