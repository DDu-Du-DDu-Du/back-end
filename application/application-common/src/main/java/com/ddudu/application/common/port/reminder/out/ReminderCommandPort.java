package com.ddudu.application.common.port.reminder.out;

import com.ddudu.domain.planning.reminder.aggregate.Reminder;

public interface ReminderCommandPort {

  Reminder save(Reminder reminder);

  Reminder update(Reminder reminder);

}
