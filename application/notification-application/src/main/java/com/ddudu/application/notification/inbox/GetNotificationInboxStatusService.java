package com.ddudu.application.notification.inbox;

import com.ddudu.application.common.dto.notification.response.NotificationInboxStatusResponse;
import com.ddudu.application.common.port.notification.in.GetNotificationInboxStatusUseCase;
import com.ddudu.application.common.port.notification.out.NotificationInboxLoaderPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.UseCase;
import com.ddudu.common.exception.NotificationInboxErrorCode;
import com.ddudu.domain.user.user.aggregate.User;
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
