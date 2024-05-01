package com.ddudu.application.todo.dto.response;

import com.ddudu.application.goal.domain.Goal;
import lombok.Builder;

@Builder
public record GoalInfo(
    Long id,
    String name,
    String color
) {

  public static GoalInfo from(Goal goal) {
    return GoalInfo.builder()
        .id(goal.getId())
        .name(goal.getName())
        .color(goal.getColor())
        .build();
  }

}
