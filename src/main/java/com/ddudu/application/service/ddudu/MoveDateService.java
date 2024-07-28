package com.ddudu.application.service.ddudu;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.exception.DduduErrorCode;
import com.ddudu.application.dto.ddudu.request.MoveDateRequest;
import com.ddudu.application.port.in.ddudu.MoveDateUseCase;
import com.ddudu.application.port.out.ddudu.DduduLoaderPort;
import com.ddudu.application.port.out.ddudu.DduduUpdatePort;
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

    Ddudu movedDdudu = ddudu.moveDate(request.newDate(), request.isPostponed());

    dduduUpdatePort.update(movedDdudu);
  }

}
