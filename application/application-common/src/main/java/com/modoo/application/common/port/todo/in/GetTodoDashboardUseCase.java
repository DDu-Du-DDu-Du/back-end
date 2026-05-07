package com.modoo.application.common.port.todo.in;

import com.modoo.application.common.dto.todo.response.TodoDashboardResponse;

public interface GetTodoDashboardUseCase {

  TodoDashboardResponse get(Long loginId, String timeZone);

}
