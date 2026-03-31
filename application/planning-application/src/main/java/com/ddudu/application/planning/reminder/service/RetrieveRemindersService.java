package com.ddudu.application.planning.reminder.service;

import com.ddudu.application.common.dto.reminder.response.RetrieveReminderResponse;
import com.ddudu.application.common.port.reminder.in.RetrieveRemindersUseCase;
import com.ddudu.application.common.port.reminder.out.ReminderLoaderPort;
import com.ddudu.application.common.port.todo.out.TodoLoaderPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.ReminderErrorCode;
import com.ddudu.domain.planning.reminder.aggregate.Reminder;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.user.user.aggregate.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
public class RetrieveRemindersService implements RetrieveRemindersUseCase {

  private final UserLoaderPort userLoaderPort;
  private final TodoLoaderPort todoLoaderPort;
  private final ReminderLoaderPort reminderLoaderPort;

  @Override
  @Transactional(readOnly = true)
  public List<RetrieveReminderResponse> retrieve(Long loginId, Long todoId) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId,
        ReminderErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName()
    );
    Todo todo = todoLoaderPort.getTodoOrElseThrow(
        todoId,
        ReminderErrorCode.TODO_NOT_EXISTING.getCodeName()
    );
    validateTodoCreator(todo, user.getId());

    List<Reminder> reminders = reminderLoaderPort.getRemindersByTodoId(todoId);
    return reminders.stream()
        .map(RetrieveReminderResponse::from)
        .toList();
  }

  private void validateTodoCreator(Todo todo, Long userId) {
    try {
      todo.validateTodoCreator(userId);
    } catch (SecurityException ignored) {
      throw new SecurityException(ReminderErrorCode.INVALID_AUTHORITY.getCodeName());
    }
  }

}
