package com.ddudu.old.todo.dto.response;

import com.ddudu.old.goal.domain.Goal;
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
