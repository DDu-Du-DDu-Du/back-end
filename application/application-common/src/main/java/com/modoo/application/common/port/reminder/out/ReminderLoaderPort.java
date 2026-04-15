package com.modoo.application.common.port.reminder.out;

import com.modoo.domain.planning.reminder.aggregate.Reminder;
import java.util.List;
import java.util.Optional;

public interface ReminderLoaderPort {

  Optional<Reminder> getOptionalReminder(Long id);

  List<Reminder> getRemindersByTodoId(Long todoId);

}
