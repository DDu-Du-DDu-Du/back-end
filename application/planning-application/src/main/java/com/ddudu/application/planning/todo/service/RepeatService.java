package com.ddudu.application.planning.todo.service;

import com.ddudu.application.common.dto.todo.request.RepeatAnotherDayRequest;
import com.ddudu.application.common.dto.todo.response.RepeatAnotherDayResponse;
import com.ddudu.application.common.port.todo.in.RepeatUseCase;
import com.ddudu.application.common.port.todo.out.RepeatTodoPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.TodoErrorCode;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class RepeatService implements RepeatUseCase {

  private final RepeatTodoPort repeatTodoPort;

  @Override
  public RepeatAnotherDayResponse repeatOnAnotherDay(
      Long loginId,
      Long todoId,
      RepeatAnotherDayRequest request
  ) {
    Todo todo = repeatTodoPort.getTodoOrElseThrow(
        todoId,
        TodoErrorCode.ID_NOT_EXISTING.getCodeName()
    );

    todo.validateTodoCreator(loginId);

    Todo replica = todo.reproduceOnDate(request.repeatOn());
    Todo repeatedTodo = repeatTodoPort.save(replica);

    return new RepeatAnotherDayResponse(repeatedTodo.getId());
  }

}
