package com.ddudu.application.common.port.notification.out;

import com.ddudu.application.common.dto.notification.ReminderScheduleTargetDto;
import com.ddudu.domain.notification.event.aggregate.NotificationEvent;
import com.ddudu.domain.notification.event.aggregate.enums.NotificationEventTypeCode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface NotificationEventLoaderPort {

  boolean existsByContext(Long userId, NotificationEventTypeCode typeCode, Long contextId);

  Optional<NotificationEvent> getOptionalEventByContext(
      Long userId,
      NotificationEventTypeCode typeCode,
      Long contextId
  );

  NotificationEvent getEventOrElseThrow(Long eventId, String message);

  Map<Long, List<ReminderScheduleTargetDto>> getAllToFireOn(LocalDate date);

}
