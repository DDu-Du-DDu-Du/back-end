package com.ddudu.application.common.dto.goal.response;

import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.goal.aggregate.enums.GoalStatus;
import lombok.Builder;

@Builder
public record BasicGoalResponse(
    Long id,
    String name,
    GoalStatus status,
    String color
) {

  public static BasicGoalResponse from(Goal goal) {
    return BasicGoalResponse.builder()
        .id(goal.getId())
        .name(goal.getName())
        .status(goal.getStatus())
        .color(goal.getColor())
        .build();
  }

}
