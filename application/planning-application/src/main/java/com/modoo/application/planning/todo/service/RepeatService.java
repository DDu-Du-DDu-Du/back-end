package com.modoo.application.planning.todo.service;

import com.modoo.application.common.dto.todo.request.RepeatAnotherDayRequest;
import com.modoo.application.common.dto.todo.response.RepeatAnotherDayResponse;
import com.modoo.application.common.port.todo.in.RepeatUseCase;
import com.modoo.application.common.port.todo.out.RepeatTodoPort;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.TodoErrorCode;
import com.modoo.domain.planning.todo.aggregate.Todo;
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

    Todo replica = todo.convert(request.timeZone())
        .reproduceOnDate(request.repeatOn());
    Todo repeatedTodo = repeatTodoPort.save(replica);

    return new RepeatAnotherDayResponse(repeatedTodo.getId());
  }

}
