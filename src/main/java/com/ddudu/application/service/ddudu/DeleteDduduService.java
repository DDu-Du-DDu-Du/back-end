package com.ddudu.application.service.ddudu;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.port.in.ddudu.DeleteDduduUseCase;
import com.ddudu.application.port.out.ddudu.DduduLoaderPort;
import com.ddudu.application.port.out.ddudu.DeleteDduduPort;
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
