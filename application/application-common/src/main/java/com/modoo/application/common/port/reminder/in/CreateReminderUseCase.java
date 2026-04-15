package com.modoo.application.common.port.reminder.in;

import com.modoo.application.common.dto.reminder.request.CreateReminderRequest;
import com.modoo.application.common.dto.reminder.response.CreateReminderResponse;

public interface CreateReminderUseCase {

  CreateReminderResponse create(Long loginId, CreateReminderRequest request);

}
