package com.modoo.application.planning.repeattodo.service;

import com.modoo.application.common.dto.repeattodo.request.UpdateRepeatTodoRequest;
import com.modoo.application.common.port.goal.out.GoalLoaderPort;
import com.modoo.application.common.port.repeattodo.in.UpdateRepeatTodoUseCase;
import com.modoo.application.common.port.repeattodo.out.RepeatTodoLoaderPort;
import com.modoo.application.common.port.repeattodo.out.UpdateRepeatTodoPort;
import com.modoo.application.common.port.todo.out.DeleteTodoPort;
import com.modoo.application.common.port.todo.out.SaveTodoPort;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.RepeatTodoErrorCode;
import com.modoo.domain.planning.goal.aggregate.Goal;
import com.modoo.domain.planning.repeattodo.aggregate.RepeatTodo;
import com.modoo.domain.planning.repeattodo.service.RepeatTodoDomainService;
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
        RepeatTodoErrorCode.REPEAT_TODO_NOT_EXIST.getCodeName()
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
