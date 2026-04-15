package com.modoo.application.common.port.todo.in;

import com.modoo.application.common.dto.todo.request.PeriodSetupRequest;

public interface PeriodSetupUseCase {

  void setUpPeriod(Long loginId, Long todoId, PeriodSetupRequest request);

}
