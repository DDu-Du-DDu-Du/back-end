package com.ddudu.application.notification.event;

import com.ddudu.application.common.port.ddudu.out.DduduLoaderPort;
import com.ddudu.application.common.port.notification.in.SendNotificationEventUseCase;
import com.ddudu.application.common.port.notification.out.NotificationDeviceTokenLoaderPort;
import com.ddudu.application.common.port.notification.out.NotificationEventCommandPort;
import com.ddudu.application.common.port.notification.out.NotificationEventLoaderPort;
import com.ddudu.application.common.port.notification.out.NotificationInboxCommandPort;
import com.ddudu.application.common.port.notification.out.NotificationSendPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.NotificationEventErrorCode;
import com.ddudu.domain.notification.device.aggregate.NotificationDeviceToken;
import com.ddudu.domain.notification.event.aggregate.NotificationEvent;
import com.ddudu.domain.notification.event.aggregate.NotificationInbox;
import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SendNotificationEventService implements SendNotificationEventUseCase {

  private final NotificationEventLoaderPort notificationEventLoaderPort;
  private final DduduLoaderPort dduduLoaderPort;
  private final NotificationInboxCommandPort notificationInboxCommandPort;
  private final NotificationEventCommandPort notificationEventCommandPort;
  private final NotificationDeviceTokenLoaderPort notificationDeviceTokenLoaderPort;
  private final NotificationSendPort notificationSendPort;

  @Override
  public void send(Long eventId) {
    log.debug(
        "Starting notification fire service in {}",
        Thread.currentThread()
            .getName()
    );

    NotificationEvent notificationEvent = notificationEventLoaderPort.getEventOrElseThrow(
        eventId,
        NotificationEventErrorCode.NOTIFICATION_EVENT_NOT_EXISTING.getCodeName()
    );
    NotificationInbox notificationInbox = createNotificationInbox(notificationEvent);
    NotificationInbox savedInbox = notificationInboxCommandPort.save(notificationInbox);

    log.debug("Notification event has been turned into inbox with ID {}", savedInbox.getId());

    List<String> deviceTokens = notificationDeviceTokenLoaderPort.getAllTokensOfUser(savedInbox.getUserId())
        .stream()
        .map(NotificationDeviceToken::getToken)
        .toList();

    if (deviceTokens.isEmpty()) {
      log.debug("No Device Tokens under {}", savedInbox.getUserId());

      // TODO: 디바이스 토큰 없을 시 핸들링 고려 필요
      throw new NotImplementedException("Handling for No Device Token is not yet implemented");
    }

    notificationSendPort.sendToDevices(deviceTokens, savedInbox.getTitle(), savedInbox.getBody());

    NotificationEvent firedEvent = notificationEvent.markFired();

    notificationEventCommandPort.update(firedEvent);
  }

  private NotificationInbox createNotificationInbox(NotificationEvent notificationEvent) {
    return switch (notificationEvent.getTypeCode()) {
      case DDUDU_REMINDER -> createDduduNotificationInbox(notificationEvent);
      default -> throw new NotImplementedException("not implemented yet.");
    };
  }

  private NotificationInbox createDduduNotificationInbox(NotificationEvent notificationEvent) {
    Ddudu ddudu = dduduLoaderPort.getDduduOrElseThrow(
        notificationEvent.getContextId(),
        NotificationEventErrorCode.ORIGINAL_DDUDU_NOT_EXISTING.getCodeName()
    );
    String title = ddudu.getName();
    String body = notificationEvent.getDduduBody(ddudu.getRemindDifference());

    return buildNotificationInbox(notificationEvent, title, body);
  }

  private NotificationInbox buildNotificationInbox(
      NotificationEvent notificationEvent,
      String title,
      String body
  ) {
    return NotificationInbox.builder()
        .eventId(notificationEvent.getId())
        .typeCode(notificationEvent.getTypeCode())
        .title(title)
        .body(body)
        .senderId(notificationEvent.getSenderId())
        .userId(notificationEvent.getReceiverId())
        .contextId(notificationEvent.getContextId())
        .build();
  }

}
