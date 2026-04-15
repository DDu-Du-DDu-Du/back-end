package com.modoo.application.planning.todo.service;

import com.modoo.application.common.dto.todo.request.ChangeNameRequest;
import com.modoo.application.common.dto.todo.response.BasicTodoResponse;
import com.modoo.application.common.port.todo.in.ChangeNameUseCase;
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
