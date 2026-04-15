package com.modoo.application.common.port.reminder.in;

import com.modoo.application.common.dto.reminder.request.UpdateReminderRequest;

public interface UpdateReminderUseCase {

  void update(Long loginId, Long reminderId, UpdateReminderRequest request);

}
