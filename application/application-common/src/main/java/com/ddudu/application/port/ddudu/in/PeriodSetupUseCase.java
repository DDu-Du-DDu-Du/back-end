package com.ddudu.application.port.ddudu.in;

import com.ddudu.application.dto.ddudu.request.PeriodSetupRequest;

public interface PeriodSetupUseCase {

  void setUpPeriod(Long loginId, Long dduduId, PeriodSetupRequest request);

}
