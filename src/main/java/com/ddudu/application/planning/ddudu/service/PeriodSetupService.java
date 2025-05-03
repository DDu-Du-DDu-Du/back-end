package com.ddudu.application.planning.ddudu.service;

import com.ddudu.application.common.annotation.UseCase;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.ddudu.exception.DduduErrorCode;
import com.ddudu.application.planning.ddudu.dto.request.PeriodSetupRequest;
import com.ddudu.application.planning.ddudu.port.in.PeriodSetupUseCase;
import com.ddudu.application.planning.ddudu.port.out.DduduLoaderPort;
import com.ddudu.application.planning.ddudu.port.out.DduduUpdatePort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
@Transactional
public class PeriodSetupService implements PeriodSetupUseCase {

  private final DduduLoaderPort dduduLoaderPort;
  private final DduduUpdatePort dduduUpdatePort;

  @Override
  public void setUpPeriod(Long loginId, Long dduduId, PeriodSetupRequest request) {
    Ddudu ddudu = dduduLoaderPort.getDduduOrElseThrow(
        dduduId, DduduErrorCode.ID_NOT_EXISTING.getCodeName());

    ddudu.validateDduduCreator(loginId);

    Ddudu updatedDdudu = ddudu.setUpPeriod(request.beginAt(), request.endAt());

    dduduUpdatePort.update(updatedDdudu);
  }

}
