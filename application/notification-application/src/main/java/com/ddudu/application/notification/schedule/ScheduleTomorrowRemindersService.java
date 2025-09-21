package com.ddudu.application.notification.schedule;

import com.ddudu.application.common.dto.notification.ReminderScheduleTargetDto;
import com.ddudu.application.common.port.notification.in.ScheduleTomorrowRemindersUseCase;
import com.ddudu.application.common.port.notification.out.NotificationEventLoaderPort;
import com.ddudu.application.common.port.notification.out.NotificationSchedulingPort;
import com.ddudu.application.common.port.user.out.UserLoaderPort;
import com.ddudu.common.annotation.UseCase;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ScheduleTomorrowRemindersService implements ScheduleTomorrowRemindersUseCase {

  private final NotificationEventLoaderPort notificationEventLoaderPort;
  private final UserLoaderPort userLoaderPort;
  private final NotificationSchedulingPort notificationSchedulingPort;

  @Override
  public void registerAllTomorrowReminders() {
    LocalDate tomorrow = LocalDate.now()
        .plusDays(1);

    log.info("Scheduling reminders for {}", tomorrow);

    notificationEventLoaderPort.getAllToFireOn(tomorrow)
        .entrySet()
        .stream()
        .filter(reminderSet -> filterReminderForValidUser(reminderSet.getKey()))
        .forEach(reminderSet -> registerReminderSchedule(
            reminderSet.getKey(),
            reminderSet.getValue()
        ));
  }

  private boolean filterReminderForValidUser(Long userId) {
    boolean isExisting = userLoaderPort.isExistingUser(userId);

    if (!isExisting) {
      log.warn("user {} does not exist. All reminders of this user are not available.", userId);
    }

    return isExisting;
  }

  private void registerReminderSchedule(Long userId, List<ReminderScheduleTargetDto> reminders) {
    log.info("Reminder sending for user {}", userId);
    reminders.forEach(reminder -> {
      log.info(
          "Registering event {} scheduling at {}",
          reminder.eventId(), reminder.willFireAt()
      );
      notificationSchedulingPort.scheduleNotificationEvent(
          reminder.eventId(),
          reminder.willFireAt()
      );
    });
  }

}
