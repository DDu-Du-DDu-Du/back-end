package com.modoo.application.common.dto.reminder.response;

import com.modoo.domain.planning.reminder.aggregate.Reminder;
import java.time.LocalDateTime;

public record RetrieveReminderResponse(
    Long id,
    LocalDateTime remindsAt,
    LocalDateTime remindedAt
) {

  public static RetrieveReminderResponse from(Reminder reminder) {
    return new RetrieveReminderResponse(
        reminder.getId(),
        reminder.getRemindsAt(),
        reminder.getRemindedAt()
    );
  }

}
