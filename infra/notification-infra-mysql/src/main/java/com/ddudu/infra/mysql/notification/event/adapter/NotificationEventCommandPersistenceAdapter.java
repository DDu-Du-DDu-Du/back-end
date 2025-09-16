package com.ddudu.infra.mysql.notification.event.adapter;

import com.ddudu.application.common.port.notification.out.NotificationEventCommandPort;
import com.ddudu.application.common.port.notification.out.NotificationEventLoaderPort;
import com.ddudu.common.annotation.DrivenAdapter;
import com.ddudu.domain.notification.event.aggregate.NotificationEvent;
import com.ddudu.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import com.ddudu.infra.mysql.notification.event.entity.NotificationEventEntity;
import com.ddudu.infra.mysql.notification.event.repository.NotificationEventRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.MissingResourceException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@DrivenAdapter
@RequiredArgsConstructor
public class NotificationEventCommandPersistenceAdapter implements NotificationEventLoaderPort,
    NotificationEventCommandPort {

  private final NotificationEventRepository notificationEventRepository;

  @Override
  public NotificationEvent save(NotificationEvent event) {
    return notificationEventRepository.save(NotificationEventEntity.from(event))
        .toDomain();
  }

  @Override
  public NotificationEvent update(NotificationEvent event) {
    NotificationEventEntity notificationEventEntity = notificationEventRepository.findById(event.getId())
        .orElseThrow(EntityNotFoundException::new);

    notificationEventEntity.update(event);

    return notificationEventEntity.toDomain();
  }

  @Override
  public void delete(NotificationEvent event) {
    notificationEventRepository.deleteById(event.getId());
  }

  @Override
  public boolean existsByContext(Long userId, NotificationEventTypeCode typeCode, Long contextId) {
    return notificationEventRepository.existsByReceiverIdAndTypeCodeAndContextId(
        userId,
        typeCode,
        contextId
    );
  }

  @Override
  public Optional<NotificationEvent> getOptionalEventByContext(
      Long userId,
      NotificationEventTypeCode typeCode,
      Long contextId
  ) {
    return notificationEventRepository.findByReceiverIdAndTypeCodeAndContextId(
            userId,
            typeCode,
            contextId
        )
        .map(NotificationEventEntity::toDomain);
  }

  @Override
  public NotificationEvent getEventOrElseThrow(Long eventId, String message) {
    return notificationEventRepository.findById(eventId)
        .orElseThrow(() -> new MissingResourceException(
            message,
            NotificationEvent.class.getName(),
            eventId.toString()
        ))
        .toDomain();
  }

}
