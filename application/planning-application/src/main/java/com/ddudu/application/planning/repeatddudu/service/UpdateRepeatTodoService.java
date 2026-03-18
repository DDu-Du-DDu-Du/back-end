package com.ddudu.application.planning.repeattodo.service;

import com.ddudu.application.common.dto.repeattodo.request.UpdateRepeatTodoRequest;
import com.ddudu.application.common.port.ddudu.out.DeleteTodoPort;
import com.ddudu.application.common.port.ddudu.out.SaveTodoPort;
import com.ddudu.application.common.port.goal.out.GoalLoaderPort;
import com.ddudu.application.common.port.repeattodo.in.UpdateRepeatTodoUseCase;
import com.ddudu.application.common.port.repeattodo.out.RepeatTodoLoaderPort;
import com.ddudu.application.common.port.repeattodo.out.UpdateRepeatTodoPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.RepeatTodoErrorCode;
import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.repeattodo.aggregate.RepeatTodo;
import com.ddudu.domain.planning.repeattodo.service.RepeatTodoDomainService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class UpdateRepeatTodoService implements UpdateRepeatTodoUseCase {

  private final RepeatTodoDomainService repeatTodoDomainService;
  private final RepeatTodoLoaderPort repeatTodoLoaderPort;
  private final UpdateRepeatTodoPort updateRepeatTodoPort;
  private final DeleteTodoPort deleteTodoPort;
  private final GoalLoaderPort goalLoaderPort;
  private final SaveTodoPort saveTodoPort;

  @Override
  public Long update(Long loginId, Long id, UpdateRepeatTodoRequest request) {
    RepeatTodo repeatTodo = repeatTodoLoaderPort.getOrElseThrow(
        id,
        RepeatTodoErrorCode.REPEAT_DDUDU_NOT_EXIST.getCodeName()
    );
    Goal goal = goalLoaderPort.getGoalOrElseThrow(
        repeatTodo.getGoalId(),
        RepeatTodoErrorCode.INVALID_GOAL.getCodeName()
    );

    goal.validateGoalCreator(loginId);

    repeatTodo = updateRepeatTodoPort.update(
        repeatTodoDomainService.update(repeatTodo, request.toCommand()));

    deleteTodoPort.deleteAllByRepeatTodo(repeatTodo);
    saveTodoPort.saveAll(repeatTodoDomainService.createRepeatedTodosAfter(
        loginId,
        repeatTodo,
        LocalDateTime.now()
    ));

    return repeatTodo.getId();
  }

}
