package com.ddudu.application.goal.dto.response;

import com.ddudu.application.goal.domain.Goal;
import com.ddudu.application.goal.domain.GoalStatus;
import com.ddudu.application.goal.domain.PrivacyType;
import lombok.Builder;

@Builder
public record GoalResponse(
    Long id,
    String name,
    GoalStatus status,
    String color,
    PrivacyType privacyType
) {

  public static GoalResponse from(Goal goal) {
    return GoalResponse.builder()
        .id(goal.getId())
        .name(goal.getName())
        .status(goal.getStatus())
        .color(goal.getColor())
        .privacyType(goal.getPrivacyType())
        .build();
  }

}
