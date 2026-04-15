package com.modoo.application.notification.event;

import com.modoo.application.common.dto.notification.event.NotificationEventSaveEvent;
import com.modoo.application.common.port.notification.in.SaveNotificationEventUseCase;
import com.modoo.application.common.port.notification.out.NotificationEventCommandPort;
import com.modoo.application.common.port.notification.out.NotificationEventLoaderPort;
import com.modoo.application.common.port.notification.out.NotificationSchedulingPort;
import com.modoo.common.annotation.UseCase;
import com.modoo.domain.notification.event.aggregate.NotificationEvent;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class SaveNotificationEventService implements SaveNotificationEventUseCase {

  private final NotificationEventCommandPort notificationEventCommandPort;
  private final NotificationEventLoaderPort notificationEventLoaderPort;
  private final NotificationSchedulingPort notificationSchedulingPort;

  @Override
  public void save(NotificationEventSaveEvent event) {
    NotificationEvent notificationEvent = upsertEvent(event);

    if (notificationEvent.isPlannedToday()) {
      notificationSchedulingPort.scheduleNotificationEvent(
          notificationEvent.getId(),
          notificationEvent.getWillFireAt()
      );
    }
  }

  private NotificationEvent upsertEvent(NotificationEventSaveEvent event) {
    Optional<NotificationEvent> optionalEvent =
        notificationEventLoaderPort.getOptionalEventByContext(
            event.userId(),
            event.typeCode(),
            event.contextId()
        );

    if (optionalEvent.isEmpty()) {
      NotificationEvent notificationEvent = NotificationEvent.builder()
          .contextId(event.contextId())
          .typeCode(event.typeCode())
          .receiverId(event.userId())
          .senderId(event.userId())
          .willFireAt(event.willFireAt())
          .build();

      return notificationEventCommandPort.save(notificationEvent);
    }

    NotificationEvent notificationEvent = optionalEvent.get()
        .updateFireTime(event.willFireAt());

    return notificationEventCommandPort.update(notificationEvent);
  }

}
