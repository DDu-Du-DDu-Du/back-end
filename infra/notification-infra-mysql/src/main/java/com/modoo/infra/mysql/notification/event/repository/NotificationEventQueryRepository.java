package com.modoo.infra.mysql.notification.event.repository;

import com.modoo.application.common.dto.notification.ReminderScheduleTargetDto;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface NotificationEventQueryRepository {

  Map<Long, List<ReminderScheduleTargetDto>> findAllTodoRemindersScheduledOn(LocalDate date);

}
