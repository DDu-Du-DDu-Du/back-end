package com.ddudu.application.planning.ddudu.service;

import com.ddudu.application.common.annotation.UseCase;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.ddudu.exception.DduduErrorCode;
import com.ddudu.application.planning.ddudu.dto.request.ChangeNameRequest;
import com.ddudu.application.planning.ddudu.dto.response.BasicDduduResponse;
import com.ddudu.application.planning.ddudu.port.in.ChangeNameUseCase;
import com.ddudu.application.planning.ddudu.port.out.DduduLoaderPort;
import com.ddudu.application.planning.ddudu.port.out.DduduUpdatePort;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class ChangeNameService implements ChangeNameUseCase {

  private final DduduLoaderPort dduduLoaderPort;
  private final DduduUpdatePort dduduUpdatePort;

  @Override
  public BasicDduduResponse change(Long loginId, Long dduduId, ChangeNameRequest request) {
    Ddudu ddudu = dduduLoaderPort.getDduduOrElseThrow(
        dduduId, DduduErrorCode.ID_NOT_EXISTING.getCodeName());

    ddudu.validateDduduCreator(loginId);

    Ddudu changedDdudu = ddudu.changeName(request.name());

    dduduUpdatePort.update(changedDdudu);

    return BasicDduduResponse.from(changedDdudu);
  }

}
