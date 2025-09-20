package com.ddudu.scheduler.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class SchedulingConfig {

  private static final String SCHEDULER_THREAD_NAME_PREFIX = "noti-schedule-";

  @Bean
  TaskScheduler dynamicScheduler() {
    ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();

    taskScheduler.setRemoveOnCancelPolicy(true);
    taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
    taskScheduler.setThreadNamePrefix(SCHEDULER_THREAD_NAME_PREFIX);

    return taskScheduler;
  }

}
