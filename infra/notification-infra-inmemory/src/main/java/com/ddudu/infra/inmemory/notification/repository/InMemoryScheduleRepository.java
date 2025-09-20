package com.ddudu.infra.inmemory.notification.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class InMemoryScheduleRepository implements ScheduleRepository {

  private final Map<Long, ScheduledFuture<?>> repository = new ConcurrentHashMap<>();

  @Override
  public ScheduledFuture<?> save(Long eventId, ScheduledFuture<?> schedule) {
    repository.put(eventId, schedule);

    return schedule;
  }

  @Override
  public ScheduledFuture<?> delete(Long eventId) {
    log.debug("checking thread");
    return repository.remove(eventId);
  }

  @Override
  public Optional<ScheduledFuture<?>> find(Long eventId) {
    return Optional.ofNullable(repository.get(eventId));
  }

}
