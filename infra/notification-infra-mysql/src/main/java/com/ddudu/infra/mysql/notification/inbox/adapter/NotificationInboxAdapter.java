package com.ddudu.infra.mysql.notification.inbox.adapter;

import com.ddudu.application.common.dto.notification.NotificationInboxCursorDto;
import com.ddudu.application.common.dto.scroll.request.ScrollRequest;
import com.ddudu.application.common.port.notification.out.NotificationInboxCommandPort;
import com.ddudu.application.common.port.notification.out.NotificationInboxLoaderPort;
import com.ddudu.common.annotation.DrivenAdapter;
import com.ddudu.domain.notification.event.aggregate.NotificationInbox;
import com.ddudu.infra.mysql.notification.inbox.entity.NotificationInboxEntity;
import com.ddudu.infra.mysql.notification.inbox.repository.NotificationInboxRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;

@DrivenAdapter
@RequiredArgsConstructor
public class NotificationInboxAdapter implements NotificationInboxCommandPort,
    NotificationInboxLoaderPort {

  private final NotificationInboxRepository notificationInboxRepository;

  @Override
  public NotificationInbox save(NotificationInbox notificationInbox) {
    return notificationInboxRepository.save(NotificationInboxEntity.from(notificationInbox))
        .toDomain();
  }

  @Override
  public List<NotificationInboxCursorDto> search(Long userId, ScrollRequest request) {
    return notificationInboxRepository.findInboxScroll(userId, request);
  }

}
