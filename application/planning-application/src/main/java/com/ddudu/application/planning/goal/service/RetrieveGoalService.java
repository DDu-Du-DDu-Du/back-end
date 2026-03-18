package com.ddudu.application.planning.goal.service;

import com.ddudu.application.common.dto.goal.response.GoalWithRepeatTodoResponse;
import com.ddudu.application.common.port.goal.in.RetrieveGoalUseCase;
import com.ddudu.application.common.port.goal.out.GoalLoaderPort;
import com.ddudu.application.common.port.repeattodo.out.RepeatTodoLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.GoalErrorCode;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.repeattodo.aggregate.RepeatTodo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RetrieveGoalService implements RetrieveGoalUseCase {

  private final GoalLoaderPort goalLoaderPort;
  private final RepeatTodoLoaderPort repeatTodoLoaderPort;

  @Override
  public GoalWithRepeatTodoResponse getById(Long userId, Long id) {
    Goal goal = goalLoaderPort.getGoalOrElseThrow(id, GoalErrorCode.ID_NOT_EXISTING.getCodeName());

    goal.validateGoalCreator(userId);

    List<RepeatTodo> repeatTodos = repeatTodoLoaderPort.getAllByGoal(goal);

    return GoalWithRepeatTodoResponse.from(goal, repeatTodos);
  }

}
