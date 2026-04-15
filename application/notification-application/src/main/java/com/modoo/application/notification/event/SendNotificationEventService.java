package com.modoo.application.notification.event;

import com.modoo.application.common.port.notification.in.SendNotificationEventUseCase;
import com.modoo.application.common.port.notification.out.NotificationDeviceTokenLoaderPort;
import com.modoo.application.common.port.notification.out.NotificationEventCommandPort;
import com.modoo.application.common.port.notification.out.NotificationEventLoaderPort;
import com.modoo.application.common.port.notification.out.NotificationInboxCommandPort;
import com.modoo.application.common.port.notification.out.NotificationSendPort;
import com.modoo.application.common.port.reminder.out.ReminderLoaderPort;
import com.modoo.application.common.port.todo.out.TodoLoaderPort;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.NotificationEventErrorCode;
import com.modoo.common.exception.ReminderErrorCode;
import com.modoo.domain.notification.device.aggregate.NotificationDeviceToken;
import com.modoo.domain.notification.event.aggregate.NotificationEvent;
import com.modoo.domain.notification.event.aggregate.NotificationInbox;
import com.modoo.domain.planning.reminder.aggregate.Reminder;
import com.modoo.domain.planning.todo.aggregate.Todo;
import java.util.List;
import java.util.MissingResourceException;
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
  private final ReminderLoaderPort reminderLoaderPort;
  private final TodoLoaderPort todoLoaderPort;
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

    List<String> deviceTokens = notificationDeviceTokenLoaderPort
        .getAllTokensOfUser(savedInbox.getUserId())
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
      case TODO_REMINDER -> createTodoNotificationInbox(notificationEvent);
      default -> throw new NotImplementedException("not implemented yet.");
    };
  }

  private NotificationInbox createTodoNotificationInbox(NotificationEvent notificationEvent) {
    Reminder reminder = reminderLoaderPort.getOptionalReminder(notificationEvent.getContextId())
        .orElseThrow(() ->
            new MissingResourceException(
                ReminderErrorCode.REMINDER_NOT_EXISTING.getCodeName(),
                Reminder.class.getName(),
                String.valueOf(notificationEvent.getContextId())
            )
        );
    Todo todo = todoLoaderPort.getTodoOrElseThrow(
        reminder.getTodoId(),
        NotificationEventErrorCode.ORIGINAL_TODO_NOT_EXISTING.getCodeName()
    );
    String title = todo.getName();
    String body = notificationEvent.getTodoBody(
        reminder.getRemindDifference(todo.getScheduleDatetime())
    );

    return buildNotificationInbox(notificationEvent, title, body, reminder.getTodoId());
  }

  private NotificationInbox buildNotificationInbox(
      NotificationEvent notificationEvent,
      String title,
      String body,
      Long contextId
  ) {
    return NotificationInbox.builder()
        .eventId(notificationEvent.getId())
        .typeCode(notificationEvent.getTypeCode())
        .title(title)
        .body(body)
        .senderId(notificationEvent.getSenderId())
        .userId(notificationEvent.getReceiverId())
        .contextId(contextId)
        .build();
  }

}
