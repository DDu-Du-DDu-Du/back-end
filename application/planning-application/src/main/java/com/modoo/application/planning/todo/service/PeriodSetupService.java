package com.modoo.application.planning.todo.service;

import com.modoo.application.common.dto.todo.request.PeriodSetupRequest;
import com.modoo.application.common.port.todo.in.PeriodSetupUseCase;
import com.modoo.application.common.port.todo.out.TodoLoaderPort;
import com.modoo.application.common.port.todo.out.TodoUpdatePort;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.TodoErrorCode;
import com.modoo.domain.planning.todo.aggregate.Todo;
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
