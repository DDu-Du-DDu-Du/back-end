package com.ddudu.application.service.ddudu;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.dto.request.PeriodSetupRequest;
import com.ddudu.application.domain.ddudu.exception.DduduErrorCode;
import com.ddudu.application.port.in.ddudu.DduduPeriodSetupPort;
import com.ddudu.application.port.out.ddudu.DduduLoaderPort;
import com.ddudu.application.port.out.ddudu.PeriodSetupPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
@Transactional
public class DduduPeriodSetupService implements DduduPeriodSetupPort {

  private final DduduLoaderPort dduduLoaderPort;
  private final PeriodSetupPort periodSetupPort;

  @Override
  public void setUpPeriod(Long loginId, Long dduduId, PeriodSetupRequest request) {
    Ddudu ddudu = dduduLoaderPort.getDduduOrElseThrow(
        dduduId, DduduErrorCode.ID_NOT_EXISTING.getCodeName());

    ddudu.checkAuthority(loginId);

    Ddudu updatedDdudu = ddudu.setUpPeriod(request.beginAt(), request.endAt());

    periodSetupPort.updatePeriod(updatedDdudu);
  }

}
