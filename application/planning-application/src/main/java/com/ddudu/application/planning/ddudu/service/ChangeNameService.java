package com.ddudu.application.planning.ddudu.service;

import com.ddudu.common.annotation.UseCase;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.common.exception.DduduErrorCode;
import com.ddudu.application.dto.ddudu.request.ChangeNameRequest;
import com.ddudu.application.dto.ddudu.response.BasicDduduResponse;
import com.ddudu.application.port.ddudu.in.ChangeNameUseCase;
import com.ddudu.application.port.ddudu.out.DduduLoaderPort;
import com.ddudu.application.port.ddudu.out.DduduUpdatePort;
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
