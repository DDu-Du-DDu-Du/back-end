package com.ddudu.application.planning.ddudu.service;

import com.ddudu.common.annotation.UseCase;
import com.ddudu.application.port.ddudu.in.SwitchStatusUseCase;
import com.ddudu.application.port.ddudu.out.DduduLoaderPort;
import com.ddudu.application.port.ddudu.out.DduduUpdatePort;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.common.exception.DduduErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class SwitchStatusService implements SwitchStatusUseCase {

  private final DduduLoaderPort dduduLoaderPort;
  private final DduduUpdatePort dduduUpdatePort;

  @Override
  public void switchStatus(Long loginId, Long dduduId) {
    Ddudu ddudu = dduduLoaderPort.getDduduOrElseThrow(
        dduduId, DduduErrorCode.ID_NOT_EXISTING.getCodeName());

    ddudu.validateDduduCreator(loginId);

    dduduUpdatePort.update(ddudu.switchStatus());
  }

}
