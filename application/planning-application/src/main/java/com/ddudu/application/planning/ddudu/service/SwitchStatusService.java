package com.ddudu.application.planning.ddudu.service;

import com.ddudu.application.common.port.ddudu.in.SwitchStatusUseCase;
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
public class SwitchStatusService implements SwitchStatusUseCase {

  private final TodoLoaderPort dduduLoaderPort;
  private final TodoUpdatePort dduduUpdatePort;

  @Override
  public void switchStatus(Long loginId, Long dduduId) {
    Todo ddudu = dduduLoaderPort.getTodoOrElseThrow(
        dduduId,
        TodoErrorCode.ID_NOT_EXISTING.getCodeName()
    );

    ddudu.validateTodoCreator(loginId);

    dduduUpdatePort.update(ddudu.switchStatus());
  }

}
