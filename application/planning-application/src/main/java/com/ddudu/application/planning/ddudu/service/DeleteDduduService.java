package com.ddudu.application.planning.ddudu.service;

import com.ddudu.application.common.dto.interim.InterimCancelReminderEvent;
import com.ddudu.application.common.dto.interim.InterimDeleteDduduEvent;
import com.ddudu.application.common.dto.notification.event.NotificationEventRemoveEvent;
import com.ddudu.application.common.port.ddudu.in.DeleteDduduUseCase;
import com.ddudu.application.common.port.ddudu.out.DduduLoaderPort;
import com.ddudu.application.common.port.ddudu.out.DeleteDduduPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@UseCase
@RequiredArgsConstructor
public class DeleteDduduService implements DeleteDduduUseCase {

  private final DduduLoaderPort dduduLoaderPort;
  private final DeleteDduduPort deleteDduduPort;
  private final ApplicationEventPublisher applicationEventPublisher;

  @Override
  @Transactional
  public void delete(Long loginId, Long dduduId) {
    Optional<Ddudu> optionalDdudu = dduduLoaderPort.getOptionalDdudu(dduduId);

    if (optionalDdudu.isEmpty()) {
      return;
    }

    Ddudu ddudu = optionalDdudu.get();

    ddudu.validateDduduCreator(loginId);
    deleteDduduPort.delete(ddudu);

    InterimCancelReminderEvent interimEvent = InterimCancelReminderEvent.from(
        loginId,
        ddudu
    );

    applicationEventPublisher.publishEvent(interimEvent);
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  void publishNotificationEventAfterCommit(InterimDeleteDduduEvent event) {
    NotificationEventRemoveEvent removeEvent = NotificationEventRemoveEvent.from(event);

    applicationEventPublisher.publishEvent(removeEvent);
  }

}
