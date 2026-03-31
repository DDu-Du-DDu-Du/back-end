package com.ddudu.application.common.port.reminder.in;

import com.ddudu.application.common.dto.reminder.request.CreateReminderRequest;
import com.ddudu.application.common.dto.reminder.response.CreateReminderResponse;

public interface CreateReminderUseCase {

  CreateReminderResponse create(Long loginId, CreateReminderRequest request);

}
