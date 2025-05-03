package com.ddudu.application.planning.ddudu.service;

import com.ddudu.application.common.annotation.UseCase;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.ddudu.exception.DduduErrorCode;
import com.ddudu.application.planning.ddudu.dto.request.MoveDateRequest;
import com.ddudu.application.planning.ddudu.port.in.MoveDateUseCase;
import com.ddudu.application.planning.ddudu.port.out.DduduLoaderPort;
import com.ddudu.application.planning.ddudu.port.out.DduduUpdatePort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
@Transactional
public class MoveDateService implements MoveDateUseCase {

  private final DduduLoaderPort dduduLoaderPort;
  private final DduduUpdatePort dduduUpdatePort;

  @Override
  public void moveDate(Long loginId, Long dduduId, MoveDateRequest request) {
    Ddudu ddudu = dduduLoaderPort.getDduduOrElseThrow(
        dduduId, DduduErrorCode.ID_NOT_EXISTING.getCodeName());

    ddudu.validateDduduCreator(loginId);

    Ddudu movedDdudu = ddudu.moveDate(request.newDate());

    dduduUpdatePort.update(movedDdudu);
  }

}
