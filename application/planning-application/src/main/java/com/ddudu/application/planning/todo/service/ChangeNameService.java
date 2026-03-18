package com.ddudu.application.planning.todo.service;

import com.ddudu.application.common.dto.todo.request.ChangeNameRequest;
import com.ddudu.application.common.dto.todo.response.BasicTodoResponse;
import com.ddudu.application.common.port.todo.in.ChangeNameUseCase;
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
public class ChangeNameService implements ChangeNameUseCase {

  private final TodoLoaderPort todoLoaderPort;
  private final TodoUpdatePort todoUpdatePort;

  @Override
  public BasicTodoResponse change(Long loginId, Long todoId, ChangeNameRequest request) {
    Todo todo = todoLoaderPort.getTodoOrElseThrow(
        todoId, TodoErrorCode.ID_NOT_EXISTING.getCodeName());

    todo.validateTodoCreator(loginId);

    Todo changedTodo = todo.changeName(request.name());

    todoUpdatePort.update(changedTodo);

    return BasicTodoResponse.from(changedTodo);
  }

}
