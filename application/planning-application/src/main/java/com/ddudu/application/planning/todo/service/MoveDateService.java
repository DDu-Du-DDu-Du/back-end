package com.ddudu.application.planning.todo.service;

import com.ddudu.application.common.dto.todo.request.MoveDateRequest;
import com.ddudu.application.common.port.todo.in.MoveDateUseCase;
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
public class MoveDateService implements MoveDateUseCase {

  private final TodoLoaderPort todoLoaderPort;
  private final TodoUpdatePort todoUpdatePort;

  @Override
  public void moveDate(Long loginId, Long todoId, MoveDateRequest request) {
    Todo todo = todoLoaderPort.getTodoOrElseThrow(
        todoId,
        TodoErrorCode.ID_NOT_EXISTING.getCodeName()
    );

    todo.validateTodoCreator(loginId);

    Todo movedTodo = todo.moveDate(request.newDate(), request.postpone());

    todoUpdatePort.update(movedTodo);
  }

}
