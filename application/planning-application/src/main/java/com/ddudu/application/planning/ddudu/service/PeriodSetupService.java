package com.ddudu.application.planning.ddudu.service;

import com.ddudu.application.common.dto.ddudu.request.PeriodSetupRequest;
import com.ddudu.application.common.port.ddudu.in.PeriodSetupUseCase;
import com.ddudu.application.common.port.ddudu.out.DduduLoaderPort;
import com.ddudu.application.common.port.ddudu.out.DduduUpdatePort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.DduduErrorCode;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class PeriodSetupService implements PeriodSetupUseCase {

  private final DduduLoaderPort dduduLoaderPort;
  private final DduduUpdatePort dduduUpdatePort;

  @Override
  public void setUpPeriod(Long loginId, Long dduduId, PeriodSetupRequest request) {
    Ddudu ddudu = dduduLoaderPort.getDduduOrElseThrow(
        dduduId,
        DduduErrorCode.ID_NOT_EXISTING.getCodeName()
    );

    ddudu.validateDduduCreator(loginId);

    Ddudu updatedDdudu = ddudu.setUpPeriod(request.beginAt(), request.endAt());

    dduduUpdatePort.update(updatedDdudu);
  }

}
