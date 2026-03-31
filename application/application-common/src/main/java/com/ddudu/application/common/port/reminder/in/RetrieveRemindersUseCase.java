package com.ddudu.application.common.port.reminder.in;

import com.ddudu.application.common.dto.reminder.response.RetrieveReminderResponse;
import java.util.List;

public interface RetrieveRemindersUseCase {

  List<RetrieveReminderResponse> retrieve(Long loginId, Long todoId);

}
