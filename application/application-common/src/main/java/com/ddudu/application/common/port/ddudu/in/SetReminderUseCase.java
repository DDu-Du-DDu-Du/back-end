package com.ddudu.application.common.port.ddudu.in;

import com.ddudu.application.common.dto.ddudu.request.SetReminderRequest;

public interface SetReminderUseCase {

  void setReminder(Long loginId, Long id, SetReminderRequest request);

}
