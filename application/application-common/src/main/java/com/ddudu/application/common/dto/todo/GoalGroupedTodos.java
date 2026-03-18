package com.ddudu.application.common.dto.todo;

import com.ddudu.application.common.dto.goal.response.BasicGoalResponse;
import com.ddudu.application.common.dto.todo.response.BasicTodoResponse;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import java.util.List;
import lombok.Builder;

@Builder
public record GoalGroupedTodos(
    BasicGoalResponse goal,
    List<BasicTodoResponse> todos
) {

  public static GoalGroupedTodos of(Goal goal, List<BasicTodoResponse> todos) {
    return GoalGroupedTodos.builder()
        .goal(BasicGoalResponse.from(goal))
        .todos(todos)
        .build();
  }

}
