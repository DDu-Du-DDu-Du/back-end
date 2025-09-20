package com.ddudu.application.common.port.notification.out;

import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

public interface ScheduleLoaderPort {

  Optional<ScheduledFuture<?>> getOptionalSchedule(Long eventId);

}
