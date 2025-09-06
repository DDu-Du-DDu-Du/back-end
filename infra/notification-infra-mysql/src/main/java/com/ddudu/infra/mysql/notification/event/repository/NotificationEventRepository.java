package com.ddudu.infra.mysql.notification.event.repository;

import com.ddudu.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import com.ddudu.infra.mysql.notification.event.entity.NotificationEventEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationEventRepository extends JpaRepository<NotificationEventEntity, Long> {

  boolean existsByTypeCodeAndContextId(NotificationEventTypeCode typeCode, Long contextId);

  Optional<NotificationEventEntity> findByTypeCodeAndContextId(
      NotificationEventTypeCode typeCode,
      Long contextId
  );

  void deleteAllByTypeCodeAndContextId(NotificationEventTypeCode typeCode, Long contextId);

}
