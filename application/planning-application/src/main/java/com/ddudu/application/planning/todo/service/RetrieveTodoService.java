package com.ddudu.application.planning.todo.service;

import com.ddudu.application.common.dto.reminder.response.RetrieveReminderResponse;
import com.ddudu.application.common.dto.todo.response.TodoDetailResponse;
import com.ddudu.application.common.port.reminder.out.ReminderLoaderPort;
import com.ddudu.application.common.port.todo.in.RetrieveTodoUseCase;
import com.ddudu.application.common.port.todo.out.TodoLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.TodoErrorCode;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RetrieveTodoService implements RetrieveTodoUseCase {

  private final TodoLoaderPort todoLoaderPort;
  private final ReminderLoaderPort reminderLoaderPort;

  @Override
  public TodoDetailResponse findById(Long loginId, Long id) {
    Todo todo = todoLoaderPort.getTodoOrElseThrow(
        id,
        TodoErrorCode.ID_NOT_EXISTING.getCodeName()
    );

    todo.validateTodoCreator(loginId);

    return TodoDetailResponse.from(
        todo,
        reminderLoaderPort.getRemindersByTodoId(todo.getId())
            .stream()
            .map(RetrieveReminderResponse::from)
            .toList()
    );
  }

}
