package com.ddudu.application.planning.ddudu.service;

import com.ddudu.application.common.dto.ddudu.request.ChangeNameRequest;
import com.ddudu.application.common.dto.ddudu.response.BasicTodoResponse;
import com.ddudu.application.common.port.ddudu.in.ChangeNameUseCase;
import com.ddudu.application.common.port.ddudu.out.TodoLoaderPort;
import com.ddudu.application.common.port.ddudu.out.TodoUpdatePort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.TodoErrorCode;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class ChangeNameService implements ChangeNameUseCase {

  private final TodoLoaderPort dduduLoaderPort;
  private final TodoUpdatePort dduduUpdatePort;

  @Override
  public BasicTodoResponse change(Long loginId, Long dduduId, ChangeNameRequest request) {
    Todo ddudu = dduduLoaderPort.getTodoOrElseThrow(
        dduduId, TodoErrorCode.ID_NOT_EXISTING.getCodeName());

    ddudu.validateTodoCreator(loginId);

    Todo changedTodo = ddudu.changeName(request.name());

    dduduUpdatePort.update(changedTodo);

    return BasicTodoResponse.from(changedTodo);
  }

}
