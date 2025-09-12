package com.ddudu.application.common.port.notification.out;

import java.time.LocalDateTime;

public interface NotificationSchedulingPort {

  void scheduleNotificationEvent(Long eventId, LocalDateTime willFireAt);

  void cancelNotificationEvent(Long eventId);

}
