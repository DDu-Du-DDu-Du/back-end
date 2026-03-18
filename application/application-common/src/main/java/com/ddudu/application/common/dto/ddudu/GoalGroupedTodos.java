package com.ddudu.application.common.dto.ddudu;

import com.ddudu.application.common.dto.ddudu.response.BasicTodoResponse;
import com.ddudu.application.common.dto.goal.response.BasicGoalResponse;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import java.util.List;
import lombok.Builder;

@Builder
public record GoalGroupedTodos(
    BasicGoalResponse goal,
    List<BasicTodoResponse> ddudus
) {

  public static GoalGroupedTodos of(Goal goal, List<BasicTodoResponse> todos) {
    return GoalGroupedTodos.builder()
        .goal(BasicGoalResponse.from(goal))
        .ddudus(todos)
        .build();
  }

}
