package com.ddudu.infra.mysql.notification.event.repository;

import com.ddudu.application.common.dto.notification.ReminderScheduleTargetDto;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface NotificationEventQueryRepository {

  Map<Long, List<ReminderScheduleTargetDto>> findAllDduduRemindersScheduledOn(LocalDate date);

}
