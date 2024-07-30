package com.ddudu.old.goal.dto;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.GoalStatus;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;

public record GoalResponse(
    Long id,
    String name,
    GoalStatus status,
    String color,
    PrivacyType privacyType
) {

  public static GoalResponse from(Goal goal) {
    return new GoalResponse(
        goal.getId(),
        goal.getName(),
        goal.getStatus(),
        goal.getColor(),
        goal.getPrivacyType()
    );
  }

}
