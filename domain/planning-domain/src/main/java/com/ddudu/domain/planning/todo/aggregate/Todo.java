package com.ddudu.domain.planning.todo.aggregate;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.ddudu.common.exception.TodoErrorCode;
import com.ddudu.domain.planning.todo.aggregate.enums.TodoStatus;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Todo {

  private static final int MAX_NAME_LENGTH = 50;
  private static final int MAX_MEMO_LENGTH = 2000;

  @EqualsAndHashCode.Include
  private final Long id;
  private final Long goalId;
  private final Long userId;
  private final Long repeatTodoId;
  private final String name;
  private final String memo;
  private final TodoStatus status;
  private final LocalDateTime postponedAt;
  private final LocalDate scheduledOn;
  private final LocalTime beginAt;
  private final LocalTime endAt;
  private final LocalDateTime remindAt;

  @Builder
  private Todo(
      Long id,
      Long goalId,
      Long userId,
      Long repeatTodoId,
      String name,
      String memo,
      Boolean isPostponed,
      LocalDateTime postponedAt,
      TodoStatus status,
      String statusValue,
      LocalDate scheduledOn,
      LocalTime beginAt,
      LocalTime endAt,
      LocalDateTime remindAt,
      Integer remindDays,
      Integer remindHours,
      Integer remindMinutes
  ) {
    validate(goalId, userId, name, memo, beginAt, endAt);

    this.id = id;
    this.goalId = goalId;
    this.userId = userId;
    this.repeatTodoId = repeatTodoId;
    this.name = name;
    this.memo = memo;
    this.status = Objects.requireNonNullElse(status, TodoStatus.from(statusValue));
    this.postponedAt = resolvePostponedAt(postponedAt, isPostponed);
    this.scheduledOn = Objects.requireNonNullElse(scheduledOn, LocalDate.now());
    this.beginAt = beginAt;
    this.endAt = endAt;
    this.remindAt = resolveReminder(remindAt, remindDays, remindHours, remindMinutes);
  }

  public void validateTodoCreator(Long userId) {
    if (!isCreatedByUser(userId)) {
      throw new SecurityException(TodoErrorCode.INVALID_AUTHORITY.getCodeName());
    }
  }

  public Todo setUpPeriod(LocalTime beginAt, LocalTime endAt) {
    return getFullBuilder().beginAt(beginAt)
        .endAt(endAt)
        .build();
  }

  public Todo moveDate(LocalDate newDate) {
    return moveDate(newDate, true);
  }

  public Todo moveDate(LocalDate newDate, boolean postpone) {
    checkArgument(Objects.nonNull(newDate), TodoErrorCode.NULL_DATE_TO_MOVE.getCodeName());
    LocalDate previousScheduledOn = this.scheduledOn;

    TodoBuilder builder = getFullBuilder()
        .scheduledOn(newDate);

    if (!postpone) {
      return builder.build();
    }

    checkArgument(!this.status.isCompleted(),
        TodoErrorCode.UNABLE_TO_POSTPONE_COMPLETED_TODO.getCodeName());

    return builder
        .postponedAt(previousScheduledOn.atStartOfDay())
        .build();
  }

  public Todo reproduceOnDate(LocalDate scheduledOn) {
    checkArgument(
        !scheduledOn.isEqual(this.scheduledOn),
        TodoErrorCode.UNABLE_TO_REPRODUCE_ON_SAME_DATE.getCodeName()
    );

    return getFullBuilder()
        .id(null)
        .postponedAt(null)
        .status(TodoStatus.UNCOMPLETED)
        .scheduledOn(scheduledOn)
        .build();
  }

  public Todo switchStatus() {
    return getFullBuilder()
        .status(status.switchStatus())
        .build();
  }

  public Todo changeName(String name) {
    validateName(name);
    return getFullBuilder()
        .name(name)
        .build();
  }

  public Todo update(
      Long goalId,
      String name,
      String memo,
      LocalDate scheduledOn,
      LocalTime beginAt,
      LocalTime endAt,
      Integer remindDays,
      Integer remindHours,
      Integer remindMinutes
  ) {
    TodoBuilder builder = getFullBuilder()
        .goalId(goalId)
        .name(name)
        .memo(memo)
        .scheduledOn(scheduledOn)
        .beginAt(beginAt)
        .endAt(endAt);

    if (isReminderInputEmpty(remindDays, remindHours, remindMinutes)) {
      return builder.build();
    }

    return builder
        .remindDays(remindDays)
        .remindHours(remindHours)
        .remindMinutes(remindMinutes)
        .remindAt(null)
        .build();
  }


  public Long getRepeatTodoId() {
    return repeatTodoId;
  }

  public boolean hasStartTime() {
    return nonNull(beginAt);
  }

  public int getBeginHour() {
    if (isNull(beginAt)) {
      return -1;
    }

    return beginAt.getHour();
  }

  public boolean hasReminder() {
    return nonNull(remindAt);
  }

  public boolean isPostponed() {
    return nonNull(postponedAt);
  }

  public Todo setReminder(int days, int hours, int minutes) {
    LocalDateTime reminder = validateReminder(days, hours, minutes);

    return getFullBuilder()
        .remindAt(reminder)
        .build();
  }

  public Todo cancelReminder() {
    return getFullBuilder()
        .remindAt(null)
        .build();
  }

  public Duration getRemindDifference() {
    checkState(
        Objects.nonNull(beginAt) && Objects.nonNull(remindAt),
        TodoErrorCode.UNABLE_TO_GET_REMINDER.getCodeName()
    );

    LocalDateTime scheduledAt = scheduledOn.atTime(beginAt);

    checkState(
        scheduledAt.isAfter(remindAt),
        TodoErrorCode.REMINDER_NOT_AFTER_NOW.getCodeName()
    );

    return Duration.between(remindAt, scheduledAt);
  }

  private TodoBuilder getFullBuilder() {
    return Todo.builder()
        .id(this.id)
        .goalId(this.goalId)
        .userId(this.userId)
        .repeatTodoId(this.repeatTodoId)
        .name(this.name)
        .memo(this.memo)
        .status(this.status)
        .scheduledOn(this.scheduledOn)
        .postponedAt(this.postponedAt)
        .beginAt(this.beginAt)
        .endAt(this.endAt)
        .remindAt(this.remindAt);
  }

  private LocalDateTime resolvePostponedAt(LocalDateTime postponedAt, Boolean isPostponed) {
    if (nonNull(postponedAt)) {
      return postponedAt;
    }

    if (Boolean.TRUE.equals(isPostponed)) {
      return LocalDateTime.now();
    }

    return null;
  }

  private void validate(
      Long goalId,
      Long userId,
      String name,
      String memo,
      LocalTime beginAt,
      LocalTime endAt
  ) {
    checkArgument(Objects.nonNull(goalId), TodoErrorCode.NULL_GOAL_VALUE.getCodeName());
    checkArgument(Objects.nonNull(userId), TodoErrorCode.NULL_USER.getCodeName());
    validateName(name);
    validateMemo(memo);
    validatePeriod(beginAt, endAt);
  }

  private void validateName(String name) {
    checkArgument(StringUtils.isNotBlank(name), TodoErrorCode.BLANK_NAME.getCodeName());
    checkArgument(
        name.length() <= MAX_NAME_LENGTH,
        TodoErrorCode.EXCESSIVE_NAME_LENGTH.getCodeName()
    );
  }

  private void validateMemo(String memo) {
    if (isNull(memo)) {
      return;
    }

    checkArgument(
        memo.length() <= MAX_MEMO_LENGTH,
        TodoErrorCode.EXCESSIVE_MEMO_LENGTH.getCodeName()
    );
  }

  private void validatePeriod(LocalTime beginAt, LocalTime endAt) {
    if (isNull(beginAt) || isNull(endAt)) {
      return;
    }

    checkArgument(
        !beginAt.isAfter(endAt),
        TodoErrorCode.UNABLE_TO_FINISH_BEFORE_BEGIN.getCodeName()
    );
  }

  private boolean isCreatedByUser(Long userId) {
    return Objects.equals(this.userId, userId);
  }

  private LocalDateTime resolveReminder(
      LocalDateTime remindAt,
      Integer remindDays,
      Integer remindHours,
      Integer remindMinutes
  ) {
    if (nonNull(remindAt)) {
      return remindAt;
    }

    if (isReminderInputEmpty(remindDays, remindHours, remindMinutes)) {
      return null;
    }

    return validateReminder(
        Objects.requireNonNullElse(remindDays, 0),
        Objects.requireNonNullElse(remindHours, 0),
        Objects.requireNonNullElse(remindMinutes, 0)
    );
  }

  private boolean isReminderInputEmpty(
      Integer remindDays,
      Integer remindHours,
      Integer remindMinutes
  ) {
    return isNull(remindDays) && isNull(remindHours) && isNull(remindMinutes);
  }

  private LocalDateTime validateReminder(int days, int hours, int minutes) {
    checkArgument(
        days >= 0 && hours >= 0 && minutes >= 0,
        TodoErrorCode.NEGATIVE_REMINDER_INPUT_EXISTS.getCodeName()
    );
    checkArgument(
        hasStartTime(),
        TodoErrorCode.BEGIN_AT_REQUIRED_FOR_REMINDER.getCodeName()
    );

    Duration offset = Duration.ofDays(days)
        .plusHours(hours)
        .plusMinutes(minutes);

    checkArgument(
        !offset.isZero(),
        TodoErrorCode.ZERO_REMINDER.getCodeName()
    );

    LocalDateTime reminder = scheduledOn.atTime(beginAt)
        .minusDays(days)
        .minusHours(hours)
        .minusMinutes(minutes);

    checkArgument(
        reminder.isAfter(LocalDateTime.now()),
        TodoErrorCode.REMINDER_NOT_AFTER_NOW.getCodeName()
    );

    return reminder;
  }

}
