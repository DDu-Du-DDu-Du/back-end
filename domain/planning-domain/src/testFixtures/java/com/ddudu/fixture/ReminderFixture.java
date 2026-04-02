package com.ddudu.fixture;

import com.ddudu.domain.planning.reminder.aggregate.Reminder;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReminderFixture extends BaseFixture {

  public static Reminder createValidReminder() {
    Long userId = getRandomId();
    Long todoId = getRandomId();
    LocalDateTime scheduledAt = getFutureDateTime(10, TimeUnit.DAYS);
    LocalDateTime remindsAt = getRandomDateTimeBetween(scheduledAt.minusDays(1), scheduledAt);

    return Reminder.from(userId, todoId, remindsAt, scheduledAt);
  }

  public static Reminder createReminderWithUserId(Long userId) {
    Long todoId = getRandomId();
    LocalDateTime scheduledAt = getFutureDateTime(10, TimeUnit.DAYS);
    LocalDateTime remindsAt = scheduledAt.minusMinutes(getRandomInt(1, 120));

    return Reminder.builder()
        .userId(userId)
        .todoId(todoId)
        .remindsAt(remindsAt)
        .build();
  }

  public static Reminder createReminderWithUserIdAndTodoId(Long userId, Long todoId) {
    LocalDateTime scheduledAt = getFutureDateTime(10, TimeUnit.DAYS);
    LocalDateTime remindsAt = scheduledAt.minusMinutes(getRandomInt(1, 120));

    return Reminder.builder()
        .userId(userId)
        .todoId(todoId)
        .remindsAt(remindsAt)
        .build();
  }

  public static Reminder createValidReminderWithUserIdAndTodoId(
      Long userId,
      Long todoId,
      LocalDateTime todoScheduledAt
  ) {
    LocalDateTime remindsAt = getRandomDateTimeBetween(
        LocalDateTime.now().plusMinutes(1),
        todoScheduledAt.minusMinutes(1)
    );

    return Reminder.from(userId, todoId, remindsAt, todoScheduledAt);
  }

  public static Reminder createReminderWithTodoId(Long todoId) {
    Long userId = getRandomId();
    LocalDateTime scheduledAt = getFutureDateTime(10, TimeUnit.DAYS);
    LocalDateTime remindsAt = scheduledAt.minusMinutes(getRandomInt(1, 120));

    return Reminder.builder()
        .userId(userId)
        .todoId(todoId)
        .remindsAt(remindsAt)
        .build();
  }

  public static Reminder createReminderWithRemindsAt(LocalDateTime remindsAt) {
    return Reminder.builder()
        .userId(getRandomId())
        .todoId(getRandomId())
        .remindsAt(remindsAt)
        .build();
  }

  public static Reminder createReminderWithRemindedAt(LocalDateTime remindedAt) {
    Long userId = getRandomId();
    Long todoId = getRandomId();
    LocalDateTime scheduledAt = getFutureDateTime(10, TimeUnit.DAYS);
    LocalDateTime remindsAt = scheduledAt.minusMinutes(getRandomInt(1, 120));

    return Reminder.builder()
        .userId(userId)
        .todoId(todoId)
        .remindsAt(remindsAt)
        .remindedAt(remindedAt)
        .build();
  }

}
