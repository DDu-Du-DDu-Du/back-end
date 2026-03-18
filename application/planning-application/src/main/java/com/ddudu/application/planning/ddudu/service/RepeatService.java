package com.ddudu.application.planning.ddudu.service;

import com.ddudu.application.common.dto.ddudu.request.RepeatAnotherDayRequest;
import com.ddudu.application.common.dto.ddudu.response.RepeatAnotherDayResponse;
import com.ddudu.application.common.port.ddudu.in.RepeatUseCase;
import com.ddudu.application.common.port.ddudu.out.RepeatTodoPort;
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
      Long dduduId,
      RepeatAnotherDayRequest request
  ) {
    Todo ddudu = repeatTodoPort.getTodoOrElseThrow(
        dduduId,
        TodoErrorCode.ID_NOT_EXISTING.getCodeName()
    );

    ddudu.validateTodoCreator(loginId);

    Todo replica = ddudu.reproduceOnDate(request.repeatOn());
    Todo repeatedTodo = repeatTodoPort.save(replica);

    return new RepeatAnotherDayResponse(repeatedTodo.getId());
  }

}
