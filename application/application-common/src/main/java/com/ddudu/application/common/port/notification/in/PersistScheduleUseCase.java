package com.ddudu.application.common.port.notification.in;

import java.util.concurrent.ScheduledFuture;

public interface PersistScheduleUseCase {

  void persistSchedule(Long eventId, ScheduledFuture<?> schedule);

}
