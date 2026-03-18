package com.ddudu.application.planning.ddudu.service;

import com.ddudu.application.common.dto.ddudu.response.TodoDetailResponse;
import com.ddudu.application.common.port.ddudu.in.RetrieveTodoUseCase;
import com.ddudu.application.common.port.ddudu.out.TodoLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.TodoErrorCode;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RetrieveTodoService implements RetrieveTodoUseCase {

  private final TodoLoaderPort dduduLoaderPort;

  @Override
  public TodoDetailResponse findById(Long loginId, Long id) {
    Todo ddudu = dduduLoaderPort.getTodoOrElseThrow(
        id,
        TodoErrorCode.ID_NOT_EXISTING.getCodeName()
    );

    ddudu.validateTodoCreator(loginId);

    return TodoDetailResponse.from(ddudu);
  }

}
