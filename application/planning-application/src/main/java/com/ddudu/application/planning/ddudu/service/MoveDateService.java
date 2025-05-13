package com.ddudu.application.planning.ddudu.service;

import com.ddudu.application.common.dto.ddudu.request.MoveDateRequest;
import com.ddudu.application.common.port.ddudu.in.MoveDateUseCase;
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
public class MoveDateService implements MoveDateUseCase {

  private final DduduLoaderPort dduduLoaderPort;
  private final DduduUpdatePort dduduUpdatePort;

  @Override
  public void moveDate(Long loginId, Long dduduId, MoveDateRequest request) {
    Ddudu ddudu = dduduLoaderPort.getDduduOrElseThrow(
        dduduId,
        DduduErrorCode.ID_NOT_EXISTING.getCodeName()
    );

    ddudu.validateDduduCreator(loginId);

    Ddudu movedDdudu = ddudu.moveDate(request.newDate());

    dduduUpdatePort.update(movedDdudu);
  }

}
