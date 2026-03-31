package com.ddudu.application.common.dto.reminder.response;

import com.ddudu.domain.planning.reminder.aggregate.Reminder;

public record CreateReminderResponse(
    Long id
) {

  public static CreateReminderResponse from(Reminder reminder) {
    return new CreateReminderResponse(reminder.getId());
  }

}
