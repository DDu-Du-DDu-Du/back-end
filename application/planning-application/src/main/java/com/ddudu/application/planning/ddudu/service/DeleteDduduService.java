package com.ddudu.application.planning.ddudu.service;

import com.ddudu.application.common.port.ddudu.in.DeleteDduduUseCase;
import com.ddudu.application.common.port.ddudu.out.DduduLoaderPort;
import com.ddudu.application.common.port.ddudu.out.DeleteDduduPort;
import com.ddudu.application.common.port.notification.out.NotificationEventCommandPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class DeleteDduduService implements DeleteDduduUseCase {

  private final DduduLoaderPort dduduLoaderPort;
  private final DeleteDduduPort deleteDduduPort;
  private final NotificationEventCommandPort notificationEventCommandPort;

  @Override
  public void delete(Long loginId, Long dduduId) {
    Optional<Ddudu> optionalDdudu = dduduLoaderPort.getOptionalDdudu(dduduId);

    if (optionalDdudu.isEmpty()) {
      return;
    }

    Ddudu ddudu = optionalDdudu.get();

    ddudu.validateDduduCreator(loginId);
    deleteDduduPort.delete(ddudu);
    notificationEventCommandPort.deleteAllByContext(NotificationEventTypeCode.DDUDU, ddudu.getId());
  }

}
