package com.modoo.application.common.port.reminder.out;

import com.modoo.domain.planning.reminder.aggregate.Reminder;

public interface ReminderCommandPort {

  Reminder save(Reminder reminder);

  Reminder update(Reminder reminder);

  void deleteById(Long id);

}
