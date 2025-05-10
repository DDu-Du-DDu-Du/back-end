package com.ddudu.application.common.port.ddudu.in;

import com.ddudu.application.common.dto.ddudu.request.PeriodSetupRequest;

public interface PeriodSetupUseCase {

  void setUpPeriod(Long loginId, Long dduduId, PeriodSetupRequest request);

}
