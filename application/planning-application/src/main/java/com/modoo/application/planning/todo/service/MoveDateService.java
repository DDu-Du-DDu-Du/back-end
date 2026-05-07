package com.modoo.application.planning.todo.service;

import com.modoo.application.common.dto.todo.request.MoveDateRequest;
import com.modoo.application.common.port.todo.in.MoveDateUseCase;
import com.modoo.application.common.port.todo.out.TodoLoaderPort;
import com.modoo.application.common.port.todo.out.TodoUpdatePort;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.TodoErrorCode;
import com.modoo.domain.planning.todo.aggregate.Todo;
import java.time.LocalDate;
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

    Todo movedTodo = todo.convert(request.timeZone())
        .moveDate(request.newDate(), request.postpone());

    todoUpdatePort.update(movedTodo);
  }

}
