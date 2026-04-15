package com.modoo.application.notification.inbox;

import com.modoo.application.common.dto.notification.response.NotificationInboxStatusResponse;
import com.modoo.application.common.port.notification.in.GetNotificationInboxStatusUseCase;
import com.modoo.application.common.port.notification.out.NotificationInboxLoaderPort;
import com.modoo.application.common.port.user.out.UserLoaderPort;
import com.modoo.common.annotation.UseCase;
import com.modoo.common.exception.NotificationInboxErrorCode;
import com.modoo.domain.user.user.aggregate.User;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class GetNotificationInboxStatusService implements GetNotificationInboxStatusUseCase {

  private final UserLoaderPort userLoaderPort;
  private final NotificationInboxLoaderPort notificationInboxLoaderPort;

  @Override
  public NotificationInboxStatusResponse getStatus(Long loginId) {
    User user = userLoaderPort.getUserOrElseThrow(
        loginId,
        NotificationInboxErrorCode.LOGIN_USER_NOT_EXISTING.getCodeName()
    );
    long unreadCount = notificationInboxLoaderPort.countUnreadByUserId(user.getId());

    return NotificationInboxStatusResponse.builder()
        .hasNew(unreadCount > 0)
        .unreadCount(unreadCount)
        .build();
  }

}
