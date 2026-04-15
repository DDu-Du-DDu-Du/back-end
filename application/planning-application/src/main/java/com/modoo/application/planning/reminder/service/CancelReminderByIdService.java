package com.modoo.application.planning.reminder.service;

import com.modoo.application.common.dto.interim.InterimCancelReminderEvent;
import com.modoo.application.common.port.reminder.in.CancelReminderByIdUseCase;
import com.modoo.application.common.port.reminder.out.ReminderCommandPort;
import com.modoo.application.common.port.reminder.out.ReminderLoaderPort;
import com.modoo.application.common.port.user.out.UserLoaderPort;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.ReminderErrorCode;
import com.modoo.domain.planning.reminder.aggregate.Reminder;
import com.modoo.domain.user.user.aggregate.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
public class CancelReminderByIdService implements CancelReminderByIdUseCase {

  private final UserLoaderPort userLoaderPort;
  private final ReminderLoaderPort reminderLoaderPort;
  private final ReminderCommandPort reminderCommandPort;
  private final ApplicationEventPublisher applicationEventPublisher;

  @Override
  @Transactional
  public void cancel(Long loginId, Long reminderId) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId,
        ReminderErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName()
    );

    Optional<Reminder> optionalReminder = reminderLoaderPort.getOptionalReminder(reminderId);
    if (optionalReminder.isEmpty()) {
      return;
    }

    Reminder reminder = optionalReminder.get();
    reminder.validateReminderCreator(user.getId());
    reminder.validateCancelable();

    reminderCommandPort.deleteById(reminderId);

    InterimCancelReminderEvent interimEvent = InterimCancelReminderEvent.from(
        user.getId(),
        reminder
    );
    applicationEventPublisher.publishEvent(interimEvent);
  }

}
