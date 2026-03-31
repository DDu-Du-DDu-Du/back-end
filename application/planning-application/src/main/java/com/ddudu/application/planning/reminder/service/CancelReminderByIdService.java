package com.ddudu.application.planning.reminder.service;

import com.ddudu.application.common.port.reminder.in.CancelReminderByIdUseCase;
import com.ddudu.application.common.port.reminder.out.ReminderCommandPort;
import com.ddudu.application.common.port.reminder.out.ReminderLoaderPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.ReminderErrorCode;
import com.ddudu.common.exception.UnprocessableEntityException;
import com.ddudu.domain.planning.reminder.aggregate.Reminder;
import com.ddudu.domain.user.user.aggregate.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
public class CancelReminderByIdService implements CancelReminderByIdUseCase {

  private final UserLoaderPort userLoaderPort;
  private final ReminderLoaderPort reminderLoaderPort;
  private final ReminderCommandPort reminderCommandPort;

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

    Reminder reminder = optionalReminder.orElseThrow();
    validateAuthority(user, reminder);
    validateReminded(reminder);

    reminderCommandPort.deleteById(reminderId);
  }

  private void validateAuthority(User user, Reminder reminder) {
    if (!user.getId().equals(reminder.getUserId())) {
      throw new SecurityException(ReminderErrorCode.INVALID_AUTHORITY.getCodeName());
    }
  }

  private void validateReminded(Reminder reminder) {
    if (reminder.isReminded()) {
      throw new UnprocessableEntityException(ReminderErrorCode.ALREADY_REMINDED.getCodeName());
    }
  }

}
