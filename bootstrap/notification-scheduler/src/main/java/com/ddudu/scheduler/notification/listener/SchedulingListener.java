package com.ddudu.scheduler.notification.listener;

import com.ddudu.application.common.dto.notification.event.NotificationScheduleEvent;
import com.ddudu.scheduler.notification.scheduler.NotificationScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SchedulingListener {

  private final NotificationScheduler notificationScheduler;

  @EventListener
  public void listenScheduleEvent(NotificationScheduleEvent event) {
    notificationScheduler.registerSchedule(event.eventId(), event.willFireAt());
  }

}
