package com.ddudu.application.planning.ddudu.port.in;

import com.ddudu.application.planning.ddudu.dto.request.PeriodSetupRequest;

public interface PeriodSetupUseCase {

  void setUpPeriod(Long loginId, Long dduduId, PeriodSetupRequest request);

}
