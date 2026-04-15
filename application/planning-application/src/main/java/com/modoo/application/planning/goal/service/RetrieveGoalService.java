package com.modoo.application.planning.goal.service;

import com.modoo.application.common.dto.goal.response.GoalWithRepeatTodoResponse;
import com.modoo.application.common.port.goal.in.RetrieveGoalUseCase;
import com.modoo.application.common.port.goal.out.GoalLoaderPort;
import com.modoo.application.common.port.repeattodo.out.RepeatTodoLoaderPort;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.GoalErrorCode;
import com.modoo.domain.planning.goal.aggregate.Goal;
import com.modoo.domain.planning.repeattodo.aggregate.RepeatTodo;
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
