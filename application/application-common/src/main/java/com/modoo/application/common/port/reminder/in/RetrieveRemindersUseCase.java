package com.modoo.application.common.port.reminder.in;

import com.modoo.application.common.dto.reminder.response.RetrieveReminderResponse;
import java.util.List;

public interface RetrieveRemindersUseCase {

  List<RetrieveReminderResponse> retrieve(Long loginId, Long todoId);

}
