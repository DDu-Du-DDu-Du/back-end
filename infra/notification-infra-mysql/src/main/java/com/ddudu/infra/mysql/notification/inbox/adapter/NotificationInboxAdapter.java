package com.ddudu.infra.mysql.notification.inbox.adapter;

import com.ddudu.application.common.dto.notification.NotificationInboxCursorDto;
import com.ddudu.application.common.dto.scroll.request.ScrollRequest;
import com.ddudu.application.common.port.notification.out.NotificationInboxCommandPort;
import com.ddudu.application.common.port.notification.out.NotificationInboxLoaderPort;
import com.ddudu.common.annotation.DrivenAdapter;
import com.ddudu.domain.notification.event.aggregate.NotificationInbox;
import com.ddudu.infra.mysql.notification.inbox.entity.NotificationInboxEntity;
import com.ddudu.infra.mysql.notification.inbox.repository.NotificationInboxRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.MissingResourceException;
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
  public NotificationInbox update(NotificationInbox notificationInbox) {
    NotificationInboxEntity notificationInboxEntity = notificationInboxRepository.findById(
            notificationInbox.getId())
        .orElseThrow(EntityNotFoundException::new);

    notificationInboxEntity.update(notificationInbox);

    return notificationInboxEntity.toDomain();
  }

  @Override
  public List<NotificationInboxCursorDto> search(Long userId, ScrollRequest request) {
    return notificationInboxRepository.findInboxScroll(userId, request);
  }

  @Override
  public NotificationInbox getInboxOrElseThrow(Long id, String message) {
    return notificationInboxRepository.findById(id)
        .orElseThrow(() -> new MissingResourceException(
            message,
            NotificationInbox.class.getName(), id.toString()
        ))
        .toDomain();
  }

}
