package com.modoo.application.planning.repeattodo.service;

import com.modoo.application.common.port.goal.out.GoalLoaderPort;
import com.modoo.application.common.port.repeattodo.in.DeleteRepeatTodoUseCase;
import com.modoo.application.common.port.repeattodo.out.DeleteRepeatTodoPort;
import com.modoo.application.common.port.repeattodo.out.RepeatTodoLoaderPort;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.RepeatTodoErrorCode;
import com.modoo.domain.planning.goal.aggregate.Goal;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class DeleteRepeatTodoService implements DeleteRepeatTodoUseCase {

  private final RepeatTodoLoaderPort repeatTodoLoaderPort;
  private final GoalLoaderPort goalLoaderPort;
  private final DeleteRepeatTodoPort deleteRepeatTodoPort;

  public void delete(Long userId, Long id) {
    /**
     * 하위 투두들도 모두 삭제
     */
    repeatTodoLoaderPort.getOptionalRepeatTodo(id)
        .ifPresent(repeatTodo -> {
          Goal goal = goalLoaderPort.getGoalOrElseThrow(
              repeatTodo.getGoalId(),
              RepeatTodoErrorCode.INVALID_GOAL.getCodeName()
          );
          goal.validateGoalCreator(userId);
          deleteRepeatTodoPort.deleteWithTodos(repeatTodo);
        });
  }

}
