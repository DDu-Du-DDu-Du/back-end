package com.ddudu.domain.planning.reminder.aggregate;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import com.ddudu.common.exception.ReminderErrorCode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Reminder {

  @EqualsAndHashCode.Include
  private final Long id;
  private final Long userId;
  private final Long todoId;
  private final LocalDateTime remindsAt;
  private final LocalDateTime remindedAt;

  @Builder
  private Reminder(
      Long id,
      Long userId,
      Long todoId,
      LocalDateTime remindsAt,
      LocalDateTime remindedAt
  ) {
    checkArgument(Objects.nonNull(userId), ReminderErrorCode.NULL_USER.getCodeName());
    checkArgument(Objects.nonNull(todoId), ReminderErrorCode.NULL_TODO_VALUE.getCodeName());
    checkArgument(Objects.nonNull(remindsAt), ReminderErrorCode.NULL_REMINDS_AT.getCodeName());

    this.id = id;
    this.userId = userId;
    this.todoId = todoId;
    this.remindsAt = remindsAt;
    this.remindedAt = remindedAt;
  }

  public static Reminder from(
      Long userId,
      Long todoId,
      LocalDateTime remindsAt,
      LocalDateTime scheduledAt
  ) {
    checkArgument(Objects.nonNull(remindsAt), ReminderErrorCode.NULL_REMINDS_AT.getCodeName());
    checkArgument(Objects.nonNull(scheduledAt), ReminderErrorCode.NULL_SCHEDULED_AT.getCodeName());
    checkArgument(
        !remindsAt.isAfter(scheduledAt),
        ReminderErrorCode.INVALID_REMINDS_AT.getCodeName()
    );

    return Reminder.builder()
        .userId(userId)
        .todoId(todoId)
        .remindsAt(remindsAt)
        .build();
  }

  public Reminder update(LocalDateTime todoScheduledAt, LocalDateTime newRemindsAt) {
    Reminder updated = Reminder.from(userId, todoId, newRemindsAt, todoScheduledAt);

    return Reminder.builder()
        .id(id)
        .userId(updated.getUserId())
        .todoId(updated.getTodoId())
        .remindsAt(updated.getRemindsAt())
        .remindedAt(remindedAt)
        .build();
  }

  public boolean isReminded() {
    return Objects.nonNull(remindedAt);
  }

  public Duration getRemindDifference(LocalDateTime todoScheduledAt) {
    checkState(Objects.nonNull(remindsAt), ReminderErrorCode.UNABLE_TO_GET_REMINDER.getCodeName());
    checkState(
        Objects.nonNull(todoScheduledAt) && todoScheduledAt.isAfter(remindsAt),
        ReminderErrorCode.REMINDER_NOT_AFTER_NOW.getCodeName()
    );

    return Duration.between(remindsAt, todoScheduledAt);
  }

}
