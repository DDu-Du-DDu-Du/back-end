package com.ddudu.application.planning.reminder.service;

import com.ddudu.application.common.dto.interim.InterimCancelReminderEvent;
import com.ddudu.application.common.port.reminder.in.CancelReminderByIdUseCase;
import com.ddudu.application.common.port.reminder.out.ReminderCommandPort;
import com.ddudu.application.common.port.reminder.out.ReminderLoaderPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.ReminderErrorCode;
import com.ddudu.domain.planning.reminder.aggregate.Reminder;
import com.ddudu.domain.user.user.aggregate.User;
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

    InterimCancelReminderEvent interimEvent = InterimCancelReminderEvent.from(user.getId(), reminder);
    applicationEventPublisher.publishEvent(interimEvent);
  }

}
