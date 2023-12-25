package com.ddudu.goal.dto.response;

import com.ddudu.goal.domain.Goal;
import lombok.Builder;

@Builder
public record GoalResponse(
    Long id,
    String name,
    String status,
    String color,
    String privacyType
) {

  public static GoalResponse from(Goal goal) {
    return GoalResponse.builder()
        .id(goal.getId())
        .name(goal.getName())
        .status(goal.getStatus()
            .name())
        .color(goal.getColor())
        .privacyType(goal.getPrivacyType()
            .name())
        .build();
  }

}

