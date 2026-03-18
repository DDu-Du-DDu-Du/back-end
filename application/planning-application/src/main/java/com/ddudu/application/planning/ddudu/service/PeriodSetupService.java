package com.ddudu.application.planning.ddudu.service;

import com.ddudu.application.common.dto.ddudu.request.PeriodSetupRequest;
import com.ddudu.application.common.port.ddudu.in.PeriodSetupUseCase;
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
public class PeriodSetupService implements PeriodSetupUseCase {

  private final TodoLoaderPort dduduLoaderPort;
  private final TodoUpdatePort dduduUpdatePort;

  @Override
  public void setUpPeriod(Long loginId, Long dduduId, PeriodSetupRequest request) {
    Todo ddudu = dduduLoaderPort.getTodoOrElseThrow(
        dduduId,
        TodoErrorCode.ID_NOT_EXISTING.getCodeName()
    );

    ddudu.validateTodoCreator(loginId);

    Todo updatedTodo = ddudu.setUpPeriod(request.beginAt(), request.endAt());

    dduduUpdatePort.update(updatedTodo);
  }

}
