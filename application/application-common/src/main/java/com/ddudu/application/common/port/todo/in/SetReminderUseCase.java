package com.ddudu.application.common.port.todo.in;

import com.ddudu.application.common.dto.todo.request.SetReminderRequest;

public interface SetReminderUseCase {

  void setReminder(Long loginId, Long id, SetReminderRequest request);

}
