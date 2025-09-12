package com.ddudu.infra.scheduler.notification.config;

import com.ddudu.infra.scheduler.notification.adapter.SchedulingAdapter;
import com.ddudu.infra.scheduler.notification.repository.InMemoryScheduleRepository;
import com.ddudu.infra.scheduler.notification.repository.ScheduleRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@TestConfiguration
public class SchedulingTestConfig {

  @Bean
  public TaskScheduler taskScheduler() {
    ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();

    taskScheduler.setRemoveOnCancelPolicy(true);
    taskScheduler.initialize();

    return taskScheduler;
  }

  @Bean
  public ScheduleRepository scheduleRepository() {
    return new InMemoryScheduleRepository();
  }

  @Bean
  public SchedulingAdapter schedulingAdapter(
      TaskScheduler taskScheduler,
      ScheduleRepository scheduleRepository,
      ApplicationEventPublisher applicationEventPublisher
  ) {
    return new SchedulingAdapter(taskScheduler, scheduleRepository, applicationEventPublisher);
  }

}
