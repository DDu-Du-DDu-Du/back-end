package com.ddudu.goal.dto.response;

import com.ddudu.goal.domain.Goal;
import lombok.Builder;

@Builder
public record GoalSummaryDTO(
    Long id,
    String name,
    String status,
    String color
) {

  public static GoalSummaryDTO from(Goal goal) {
    return GoalSummaryDTO.builder()
        .id(goal.getId())
        .name(goal.getName())
        .status(goal.getStatus()
            .name())
        .color(goal.getColor())
        .build();
  }

}
