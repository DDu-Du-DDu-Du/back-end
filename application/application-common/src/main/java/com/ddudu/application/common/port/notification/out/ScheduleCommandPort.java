package com.ddudu.application.common.port.notification.out;

import java.util.concurrent.ScheduledFuture;

public interface ScheduleCommandPort {

  void save(Long eventId, ScheduledFuture<?> schedule);

}
