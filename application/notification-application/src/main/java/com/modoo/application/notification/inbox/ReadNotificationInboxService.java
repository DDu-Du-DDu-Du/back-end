package com.modoo.application.notification.inbox;

import com.modoo.application.common.dto.notification.response.ReadNotificationInboxResponse;
import com.modoo.application.common.port.notification.in.ReadNotificationInboxUseCase;
import com.modoo.application.common.port.notification.out.NotificationInboxCommandPort;
import com.modoo.application.common.port.notification.out.NotificationInboxLoaderPort;
import com.modoo.application.common.port.user.out.UserLoaderPort;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.NotificationInboxErrorCode;
import com.modoo.domain.notification.event.aggregate.NotificationInbox;
import com.modoo.domain.user.user.aggregate.User;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
public class ReadNotificationInboxService implements ReadNotificationInboxUseCase {

  private final UserLoaderPort userLoaderPort;
  private final NotificationInboxLoaderPort notificationInboxLoaderPort;
  private final NotificationInboxCommandPort notificationInboxCommandPort;

  @Override
  public ReadNotificationInboxResponse read(Long loginId, Long inboxId) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId,
        NotificationInboxErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName()
    );
    NotificationInbox notificationInbox = notificationInboxLoaderPort.getInboxOrElseThrow(
        inboxId,
        NotificationInboxErrorCode.INBOX_NOT_EXISTING.getCodeName()
    );

    notificationInbox.validateOwner(user.getId());

    NotificationInbox read = notificationInbox.markRead();
    NotificationInbox updated = notificationInboxCommandPort.update(read);
    String context = updated.getTypeCode()
        .getUpstreamContext();

    return ReadNotificationInboxResponse.builder()
        .context(context)
        .contextId(updated.getContextId())
        .build();
  }

}
