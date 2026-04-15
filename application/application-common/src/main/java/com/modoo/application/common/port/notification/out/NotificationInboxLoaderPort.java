package com.modoo.application.common.port.notification.out;

import com.modoo.application.common.dto.notification.NotificationInboxCursorDto;
import com.modoo.application.common.dto.scroll.request.ScrollRequest;
import com.modoo.domain.notification.event.aggregate.NotificationInbox;
import java.util.List;

public interface NotificationInboxLoaderPort {

  List<NotificationInboxCursorDto> search(Long userId, ScrollRequest request);

  NotificationInbox getInboxOrElseThrow(Long id, String message);

  long countUnreadByUserId(Long userId);

}
