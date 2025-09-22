package com.ddudu.application.common.port.notification.in;

import com.ddudu.application.common.dto.notification.response.ReadNotificationInboxResponse;

public interface ReadNotificationInboxUseCase {

  ReadNotificationInboxResponse read(Long loginId, Long inboxId);

}
