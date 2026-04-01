package com.ddudu.application.planning.reminder.service;

import com.ddudu.application.common.dto.interim.InterimSetReminderEvent;
import com.ddudu.application.common.dto.reminder.request.UpdateReminderRequest;
import com.ddudu.application.common.port.reminder.in.UpdateReminderUseCase;
import com.ddudu.application.common.port.reminder.out.ReminderCommandPort;
import com.ddudu.application.common.port.reminder.out.ReminderLoaderPort;
import com.ddudu.application.common.port.todo.out.TodoLoaderPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.ReminderErrorCode;
import com.ddudu.domain.planning.reminder.aggregate.Reminder;
import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.user.user.aggregate.User;
import java.time.LocalDateTime;
import java.util.MissingResourceException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
public class UpdateReminderService implements UpdateReminderUseCase {

  private final UserLoaderPort userLoaderPort;
  private final ReminderLoaderPort reminderLoaderPort;
  private final TodoLoaderPort todoLoaderPort;
  private final ReminderCommandPort reminderCommandPort;
  private final ApplicationEventPublisher applicationEventPublisher;

  @Override
  @Transactional
  public void update(Long loginId, Long reminderId, UpdateReminderRequest request) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId,
        ReminderErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName()
    );
    Reminder reminder = reminderLoaderPort.getOptionalReminder(reminderId)
        .orElseThrow(() ->
            new MissingResourceException(
                ReminderErrorCode.REMINDER_NOT_EXISTING.getCodeName(),
                Reminder.class.getName(),
                String.valueOf(reminderId)
            )
        );
    reminder.validateReminderCreator(user.getId());

    Todo todo = todoLoaderPort.getTodoOrElseThrow(
        reminder.getTodoId(),
        ReminderErrorCode.TODO_NOT_EXISTING.getCodeName()
    );
    validateTodoCreator(todo, user.getId());

    LocalDateTime scheduledAt = resolveScheduledAt(todo);
    Reminder updatedReminder = reminder.update(scheduledAt, request.remindsAt());
    Reminder saved = reminderCommandPort.update(updatedReminder);

    InterimSetReminderEvent event = InterimSetReminderEvent.from(user.getId(), saved);
    applicationEventPublisher.publishEvent(event);
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
