package com.ddudu.todo.dto.response;

import com.ddudu.goal.domain.Goal;
import lombok.Builder;

@Builder
public record GoalInfo(Long id, String name) {

  public static GoalInfo from(Goal goal) {
    return GoalInfo.builder()
        .id(goal.getId())
        .name(goal.getName())
        .build();
  }

}
