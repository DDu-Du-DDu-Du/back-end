package com.ddudu.infra.scheduler.notification.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryScheduleRepository implements ScheduleRepository {

  private final Map<Long, ScheduledFuture<?>> repository = new ConcurrentHashMap<>();

  @Override
  public ScheduledFuture<?> save(Long eventId, ScheduledFuture<?> schedule) {
    repository.put(eventId, schedule);

    return schedule;
  }

  @Override
  public ScheduledFuture<?> delete(Long eventId) {
    return repository.remove(eventId);
  }

  @Override
  public Optional<ScheduledFuture<?>> find(Long eventId) {
    return Optional.ofNullable(repository.get(eventId));
  }

}
