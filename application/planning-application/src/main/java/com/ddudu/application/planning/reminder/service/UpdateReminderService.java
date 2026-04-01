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
import java.util.MissingResourceException;
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
    todo.validateTodoCreator(user.getId());

    Reminder updatedReminder = reminder.update(todo.getScheduleDatetime(), request.remindsAt());
    Reminder saved = reminderCommandPort.update(updatedReminder);

    InterimSetReminderEvent event = InterimSetReminderEvent.from(user.getId(), saved);
    applicationEventPublisher.publishEvent(event);
  }

}
