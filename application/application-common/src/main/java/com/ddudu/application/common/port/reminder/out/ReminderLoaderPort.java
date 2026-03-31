package com.ddudu.application.common.port.reminder.out;

import com.ddudu.domain.planning.reminder.aggregate.Reminder;
import java.util.Optional;

public interface ReminderLoaderPort {

  Optional<Reminder> getOptionalReminder(Long id);

}
