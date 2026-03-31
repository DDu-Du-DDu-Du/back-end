package com.ddudu.application.common.port.reminder.in;

import com.ddudu.application.common.dto.reminder.request.UpdateReminderRequest;

public interface UpdateReminderUseCase {

  void update(Long loginId, Long id, UpdateReminderRequest request);

}
