package com.ddudu.infra.mysql.notification.event.repository;

import com.ddudu.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import com.ddudu.infra.mysql.notification.event.entity.NotificationEventEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationEventRepository extends JpaRepository<NotificationEventEntity, Long>,
    NotificationEventQueryRepository {

  boolean existsByReceiverIdAndTypeCodeAndContextId(
      Long receiverId,
      NotificationEventTypeCode typeCode,
      Long contextId
  );

  Optional<NotificationEventEntity> findByReceiverIdAndTypeCodeAndContextId(
      Long userId,
      NotificationEventTypeCode typeCode,
      Long contextId
  );

}
