package com.modoo.infra.mysql.notification.inbox.repository;

import com.modoo.application.common.dto.notification.NotificationInboxCursorDto;
import com.modoo.application.common.dto.scroll.request.ScrollRequest;
import java.util.List;

public interface NotificationInboxQueryRepository {

  List<NotificationInboxCursorDto> findInboxScroll(Long userId, ScrollRequest request);

  long countUnreadByUserId(Long userId);

}
