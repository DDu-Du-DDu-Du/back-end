package com.ddudu.application.dto.goal.response;

import com.ddudu.application.domain.goal.domain.Goal;
import lombok.Builder;

@Builder
public record BasicGoalResponse(
    Long id,
    String name,
    String color
) {

  public static BasicGoalResponse from(Goal goal) {
    return BasicGoalResponse.builder()
        .id(goal.getId())
        .name(goal.getName())
        .color(goal.getColor())
        .build();
  }

}
