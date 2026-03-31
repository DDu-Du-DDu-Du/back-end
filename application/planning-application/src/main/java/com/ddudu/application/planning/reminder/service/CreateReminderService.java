package com.ddudu.application.planning.reminder.service;

import com.ddudu.application.common.dto.interim.InterimSetReminderEvent;
import com.ddudu.application.common.dto.reminder.request.CreateReminderRequest;
import com.ddudu.application.common.dto.reminder.response.CreateReminderResponse;
import com.ddudu.application.common.port.reminder.in.CreateReminderUseCase;
import com.ddudu.application.common.port.reminder.out.ReminderCommandPort;
import com.ddudu.application.common.port.todo.out.TodoLoaderPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.ReminderErrorCode;
import com.ddudu.domain.planning.reminder.aggregate.Reminder;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.user.user.aggregate.User;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
public class CreateReminderService implements CreateReminderUseCase {

  private final UserLoaderPort userLoaderPort;
  private final TodoLoaderPort todoLoaderPort;
  private final ReminderCommandPort reminderCommandPort;
  private final ApplicationEventPublisher applicationEventPublisher;

  @Override
  @Transactional
  public CreateReminderResponse create(Long loginId, CreateReminderRequest request) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId,
        ReminderErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName()
    );
    Todo todo = todoLoaderPort.getTodoOrElseThrow(
        request.todoId(),
        ReminderErrorCode.TODO_NOT_EXISTING.getCodeName()
    );
    validateTodoCreator(todo, user.getId());

    LocalDateTime scheduledAt = resolveScheduledAt(todo);
    Reminder reminder = Reminder.from(user.getId(), todo.getId(), request.remindsAt(), scheduledAt);
    Reminder saved = reminderCommandPort.save(reminder);

    InterimSetReminderEvent event = InterimSetReminderEvent.from(user.getId(), saved);
    applicationEventPublisher.publishEvent(event);

    return CreateReminderResponse.from(saved);
  }

  private LocalDateTime resolveScheduledAt(Todo todo) {
    if (Objects.isNull(todo.getBeginAt())) {
      throw new IllegalArgumentException(ReminderErrorCode.NULL_SCHEDULED_AT.getCodeName());
    }

    return todo.getScheduledOn().atTime(todo.getBeginAt());
  }

  private void validateTodoCreator(Todo todo, Long userId) {
    try {
      todo.validateTodoCreator(userId);
    } catch (SecurityException ignored) {
      throw new SecurityException(ReminderErrorCode.INVALID_AUTHORITY.getCodeName());
    }
  }

}
