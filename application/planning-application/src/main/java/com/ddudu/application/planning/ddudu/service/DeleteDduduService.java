package com.ddudu.application.planning.ddudu.service;

import com.ddudu.common.annotation.UseCase;
import com.ddudu.application.port.ddudu.in.DeleteDduduUseCase;
import com.ddudu.application.port.ddudu.out.DduduLoaderPort;
import com.ddudu.application.port.ddudu.out.DeleteDduduPort;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class DeleteDduduService implements DeleteDduduUseCase {

  private final DduduLoaderPort dduduLoaderPort;
  private final DeleteDduduPort deleteDduduPort;

  @Override
  public void delete(Long loginId, Long dduduId) {
    dduduLoaderPort.getOptionalDdudu(dduduId)
        .ifPresent(ddudu -> {
          ddudu.validateDduduCreator(loginId);
          deleteDduduPort.delete(ddudu);
        });
  }

}
