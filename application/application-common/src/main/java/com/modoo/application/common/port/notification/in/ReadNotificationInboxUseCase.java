package com.modoo.application.common.port.notification.in;

import com.modoo.application.common.dto.notification.response.ReadNotificationInboxResponse;

public interface ReadNotificationInboxUseCase {

  ReadNotificationInboxResponse read(Long loginId, Long inboxId);

}
