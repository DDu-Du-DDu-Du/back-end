package com.ddudu.application.planning.ddudu.service;

import com.ddudu.common.annotation.UseCase;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.common.exception.DduduErrorCode;
import com.ddudu.application.dto.ddudu.response.DduduDetailResponse;
import com.ddudu.application.port.ddudu.in.RetrieveDduduUseCase;
import com.ddudu.application.port.ddudu.out.DduduLoaderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RetrieveDduduService implements RetrieveDduduUseCase {

  private final DduduLoaderPort dduduLoaderPort;

  @Override
  public DduduDetailResponse findById(Long loginId, Long id) {
    Ddudu ddudu = dduduLoaderPort.getDduduOrElseThrow(
        id, DduduErrorCode.ID_NOT_EXISTING.getCodeName());

    ddudu.validateDduduCreator(loginId);

    return DduduDetailResponse.from(ddudu);
  }

}
