package com.ddudu.application.planning.todo.service;

import com.ddudu.application.common.dto.todo.request.PeriodSetupRequest;
import com.ddudu.application.common.port.todo.in.PeriodSetupUseCase;
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
public class PeriodSetupService implements PeriodSetupUseCase {

  private final TodoLoaderPort todoLoaderPort;
  private final TodoUpdatePort todoUpdatePort;

  @Override
  public void setUpPeriod(Long loginId, Long todoId, PeriodSetupRequest request) {
    Todo todo = todoLoaderPort.getTodoOrElseThrow(
        todoId,
        TodoErrorCode.ID_NOT_EXISTING.getCodeName()
    );

    todo.validateTodoCreator(loginId);

    Todo updatedTodo = todo.setUpPeriod(request.beginAt(), request.endAt());

    todoUpdatePort.update(updatedTodo);
  }

}
