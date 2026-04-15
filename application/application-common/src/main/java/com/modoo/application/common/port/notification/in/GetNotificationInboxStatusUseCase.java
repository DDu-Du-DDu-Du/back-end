package com.modoo.application.common.port.notification.in;

import com.modoo.application.common.dto.notification.response.NotificationInboxStatusResponse;

public interface GetNotificationInboxStatusUseCase {

  NotificationInboxStatusResponse getStatus(Long loginId);

}
