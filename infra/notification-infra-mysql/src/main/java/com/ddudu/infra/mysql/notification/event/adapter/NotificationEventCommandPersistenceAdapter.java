package com.ddudu.infra.mysql.notification.event.adapter;

import com.ddudu.application.common.port.notification.out.NotificationEventCommandPort;
import com.ddudu.application.common.port.notification.out.NotificationEventLoaderPort;
import com.ddudu.common.annotation.DrivenAdapter;
import com.ddudu.domain.notification.event.aggregate.NotificationEvent;
import com.ddudu.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import com.ddudu.infra.mysql.notification.event.entity.NotificationEventEntity;
import com.ddudu.infra.mysql.notification.event.repository.NotificationEventRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@DrivenAdapter
@RequiredArgsConstructor
public class NotificationEventCommandPersistenceAdapter implements NotificationEventLoaderPort,
    NotificationEventCommandPort {

  private final NotificationEventRepository notificationEventRepository;

  @Override
  public NotificationEvent save(NotificationEvent event) {
    Optional<NotificationEventEntity> optionalEvent = notificationEventRepository.findByTypeCodeAndContextId(
        event.getTypeCode(),
        event.getContextId()
    );

    if (optionalEvent.isEmpty()) {
      return notificationEventRepository.save(NotificationEventEntity.from(event))
          .toDomain();
    }

    NotificationEventEntity eventEntity = optionalEvent.get();

    eventEntity.update(event);

    return eventEntity.toDomain();
  }

  @Override
  public void delete(NotificationEvent event) {
    notificationEventRepository.deleteById(event.getId());
  }

  @Override
  public void deleteAllByContext(NotificationEventTypeCode typeCode, Long contextId) {
    notificationEventRepository.deleteAllByTypeCodeAndContextId(typeCode, contextId);
  }

  @Override
  public boolean existsByContext(NotificationEventTypeCode typeCode, Long contextId) {
    return notificationEventRepository.existsByTypeCodeAndContextId(typeCode, contextId);
  }

  @Override
  public Optional<NotificationEvent> getOptionalEventByContext(
      NotificationEventTypeCode typeCode,
      Long contextId
  ) {
    return notificationEventRepository.findByTypeCodeAndContextId(typeCode, contextId)
        .map(NotificationEventEntity::toDomain);
  }

}
