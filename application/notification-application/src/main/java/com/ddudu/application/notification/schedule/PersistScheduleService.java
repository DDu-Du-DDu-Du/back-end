package com.ddudu.application.notification.schedule;

import com.ddudu.application.common.port.notification.in.PersistScheduleUseCase;
import com.ddudu.application.common.port.notification.out.ScheduleCommandPort;
import com.ddudu.application.common.port.notification.out.ScheduleLoaderPort;
import com.ddudu.common.annotation.UseCase;
import java.util.concurrent.ScheduledFuture;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class PersistScheduleService implements PersistScheduleUseCase {

  private final ScheduleLoaderPort scheduleLoaderPort;
  private final ScheduleCommandPort scheduleCommandPort;

  @Override
  public void persistSchedule(Long eventId, ScheduledFuture<?> schedule) {
    scheduleLoaderPort.getOptionalSchedule(eventId)
        .ifPresent(existing -> existing.cancel(false));
    scheduleCommandPort.save(eventId, schedule);
  }

}
