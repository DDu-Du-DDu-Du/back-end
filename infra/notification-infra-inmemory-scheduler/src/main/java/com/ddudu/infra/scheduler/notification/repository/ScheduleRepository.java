package com.ddudu.infra.scheduler.notification.repository;

import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

public interface ScheduleRepository {

  ScheduledFuture<?> save(Long eventId, ScheduledFuture<?> schedule);

  ScheduledFuture<?> delete(Long eventId);

  Optional<ScheduledFuture<?>> find(Long eventId);

}
