package com.ddudu.application.common.port.reminder.out;

import com.ddudu.domain.planning.reminder.aggregate.Reminder;
import java.util.MissingResourceException;
import java.util.Optional;

public interface ReminderLoaderPort {

  Optional<Reminder> getOptionalReminder(Long id);

  default Reminder getReminderOrElseThrow(Long id, String message) {
    return getOptionalReminder(id)
        .orElseThrow(() -> new MissingResourceException(message, "", ""));
  }

}
