package com.modoo.application.planning.todo.service;

import com.modoo.application.common.dto.reminder.response.RetrieveReminderResponse;
import com.modoo.application.common.dto.todo.response.TodoDetailResponse;
import com.modoo.application.common.port.reminder.out.ReminderLoaderPort;
import com.modoo.application.common.port.todo.in.RetrieveTodoUseCase;
import com.modoo.application.common.port.todo.out.TodoLoaderPort;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.TodoErrorCode;
import com.modoo.domain.planning.todo.aggregate.Todo;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RetrieveTodoService implements RetrieveTodoUseCase {

  private final TodoLoaderPort todoLoaderPort;
  private final ReminderLoaderPort reminderLoaderPort;

  public TodoDetailResponse findById(Long loginId, Long id) {
    return findById(loginId, id, null);
  }

  @Override
  public TodoDetailResponse findById(Long loginId, Long id, String timeZone) {
    Todo todo = todoLoaderPort.getTodoOrElseThrow(
        id,
        TodoErrorCode.ID_NOT_EXISTING.getCodeName()
    );

    todo.validateTodoCreator(loginId);

    return TodoDetailResponse.from(
        todo.convert(timeZone),
        reminderLoaderPort.getRemindersByTodoId(todo.getId())
            .stream()
            .map(RetrieveReminderResponse::from)
            .toList()
    );
  }

}
