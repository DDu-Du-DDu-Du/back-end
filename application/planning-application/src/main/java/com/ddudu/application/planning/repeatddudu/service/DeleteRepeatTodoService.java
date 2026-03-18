package com.ddudu.application.planning.repeattodo.service;

import com.ddudu.application.common.port.goal.out.GoalLoaderPort;
import com.ddudu.application.common.port.repeattodo.in.DeleteRepeatTodoUseCase;
import com.ddudu.application.common.port.repeattodo.out.DeleteRepeatTodoPort;
import com.ddudu.application.common.port.repeattodo.out.RepeatTodoLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.RepeatTodoErrorCode;
import com.ddudu.domain.planning.goal.aggregate.Goal;
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
     * 하위 뚜두들도 모두 삭제
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
