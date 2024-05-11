package com.ddudu.application.domain.goal.dto.response;

import com.ddudu.application.domain.goal.domain.Goal;
import lombok.Builder;

@Builder
public record CreateGoalResponse(
    Long id,
    String name,
    String color
) {

  public static CreateGoalResponse from(Goal goal) {
    return CreateGoalResponse.builder()
        .id(goal.getId())
        .name(goal.getName())
        .color(goal.getColor())
        .build();
  }

}
