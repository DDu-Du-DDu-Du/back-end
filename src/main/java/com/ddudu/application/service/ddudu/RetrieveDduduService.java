package com.ddudu.application.service.ddudu;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.exception.DduduErrorCode;
import com.ddudu.application.dto.ddudu.response.DduduDetailResponse;
import com.ddudu.application.port.in.ddudu.RetrieveDduduUseCase;
import com.ddudu.application.port.out.ddudu.DduduLoaderPort;
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
