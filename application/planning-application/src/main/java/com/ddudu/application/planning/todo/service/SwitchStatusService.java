package com.ddudu.application.planning.todo.service;

import com.ddudu.application.common.port.todo.in.SwitchStatusUseCase;
import com.ddudu.application.common.port.todo.out.TodoLoaderPort;
import com.ddudu.application.common.port.todo.out.TodoUpdatePort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.TodoErrorCode;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class SwitchStatusService implements SwitchStatusUseCase {

  private final TodoLoaderPort todoLoaderPort;
  private final TodoUpdatePort todoUpdatePort;

  @Override
  public void switchStatus(Long loginId, Long todoId) {
    Todo todo = todoLoaderPort.getTodoOrElseThrow(
        todoId,
        TodoErrorCode.ID_NOT_EXISTING.getCodeName()
    );

    todo.validateTodoCreator(loginId);

    todoUpdatePort.update(todo.switchStatus());
  }

}
