package com.ddudu.application.port.in.ddudu;

import com.ddudu.application.domain.ddudu.dto.request.PeriodSetupRequest;

public interface PeriodSetupUseCase {

  void setUpPeriod(Long loginId, Long dduduId, PeriodSetupRequest request);

}