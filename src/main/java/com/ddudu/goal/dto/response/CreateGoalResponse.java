package com.ddudu.goal.dto.response;

import com.ddudu.goal.domain.Goal;

public record CreateGoalResponse(
    Long id,
    String name,
    String color
) {

  public static CreateGoalResponse from(Goal goal) {
    return new CreateGoalResponse(goal.getId(), goal.getName(), goal.getColor());
  }

}
