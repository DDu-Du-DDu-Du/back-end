package com.ddudu.application.common.port.todo.in;

import com.ddudu.application.common.dto.todo.request.PeriodSetupRequest;

public interface PeriodSetupUseCase {

  void setUpPeriod(Long loginId, Long todoId, PeriodSetupRequest request);

}
