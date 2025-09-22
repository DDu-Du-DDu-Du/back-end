package com.ddudu.infra.mysql.notification.inbox.repository;

import com.ddudu.application.common.dto.notification.NotificationInboxCursorDto;
import com.ddudu.application.common.dto.scroll.request.ScrollRequest;
import java.util.List;

public interface NotificationInboxQueryRepository {

  List<NotificationInboxCursorDto> findInboxScroll(Long userId, ScrollRequest request);

}
