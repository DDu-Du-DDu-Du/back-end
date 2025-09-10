package com.ddudu.application.notification.event;

import com.ddudu.application.common.dto.notification.event.NotificationEventRemoveEvent;
import com.ddudu.application.common.port.notification.in.RemoveNotificationEventUseCase;
import com.ddudu.application.common.port.notification.out.NotificationEventCommandPort;
import com.ddudu.application.common.port.notification.out.NotificationEventLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.domain.notification.event.aggregate.NotificationEvent;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class RemoveNotificationEventService implements RemoveNotificationEventUseCase {

  private final NotificationEventLoaderPort notificationEventLoaderPort;
  private final NotificationEventCommandPort notificationEventCommandPort;

  @Override
  public void remove(NotificationEventRemoveEvent event) {
    Optional<NotificationEvent> optionalEvent = notificationEventLoaderPort.getOptionalEventByContext(
        event.userId(),
        event.typeCode(),
        event.contextId()
    );

    if (optionalEvent.isEmpty()) {
      return;
    }

    NotificationEvent notificationEvent = optionalEvent.get();

    if (notificationEvent.isAlreadyFired()) {
      return;
    }

    notificationEventCommandPort.delete(notificationEvent);

    if (notificationEvent.isPlannedToday()) {
      // TODO: Task Scheduler 구현 후 예약 취소 구현
    }
  }

}
