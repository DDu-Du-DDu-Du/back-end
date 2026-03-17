package com.ddudu.domain.planning.ddudu.aggregate;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.ddudu.common.exception.DduduErrorCode;
import com.ddudu.domain.planning.ddudu.aggregate.enums.DduduStatus;
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
public class Ddudu {

  private static final int MAX_NAME_LENGTH = 50;
  private static final int MAX_MEMO_LENGTH = 2000;

  @EqualsAndHashCode.Include
  private final Long id;
  private final Long goalId;
  private final Long userId;
  private final Long repeatDduduId;
  private final String name;
  private final String memo;
  private final DduduStatus status;
  private final LocalDateTime postponedAt;
  private final LocalDate scheduledOn;
  private final LocalTime beginAt;
  private final LocalTime endAt;
  private final LocalDateTime remindAt;

  @Builder
  private Ddudu(
      Long id,
      Long goalId,
      Long userId,
      Long repeatDduduId,
      String name,
      String memo,
      Boolean isPostponed,
      LocalDateTime postponedAt,
      DduduStatus status,
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
    this.repeatDduduId = repeatDduduId;
    this.name = name;
    this.memo = memo;
    this.status = Objects.requireNonNullElse(status, DduduStatus.from(statusValue));
    this.postponedAt = resolvePostponedAt(postponedAt, isPostponed);
    this.scheduledOn = Objects.requireNonNullElse(scheduledOn, LocalDate.now());
    this.beginAt = beginAt;
    this.endAt = endAt;
    this.remindAt = resolveReminder(remindAt, remindDays, remindHours, remindMinutes);
  }

  public void validateDduduCreator(Long userId) {
    if (!isCreatedByUser(userId)) {
      throw new SecurityException(DduduErrorCode.INVALID_AUTHORITY.getCodeName());
    }
  }

  public Ddudu setUpPeriod(LocalTime beginAt, LocalTime endAt) {
    return getFullBuilder().beginAt(beginAt)
        .endAt(endAt)
        .build();
  }

  public Ddudu moveDate(LocalDate newDate) {
    return moveDate(newDate, true);
  }

  public Ddudu moveDate(LocalDate newDate, boolean postpone) {
    checkArgument(Objects.nonNull(newDate), DduduErrorCode.NULL_DATE_TO_MOVE.getCodeName());
    LocalDate previousScheduledOn = this.scheduledOn;

    DduduBuilder builder = getFullBuilder()
        .scheduledOn(newDate);

    if (!postpone) {
      return builder.build();
    }

    checkArgument(!this.status.isCompleted(),
        DduduErrorCode.UNABLE_TO_POSTPONE_COMPLETED_DDUDU.getCodeName());

    return builder
        .postponedAt(previousScheduledOn.atStartOfDay())
        .build();
  }

  public Ddudu reproduceOnDate(LocalDate scheduledOn) {
    checkArgument(
        !scheduledOn.isEqual(this.scheduledOn),
        DduduErrorCode.UNABLE_TO_REPRODUCE_ON_SAME_DATE.getCodeName()
    );

    return getFullBuilder()
        .id(null)
        .postponedAt(null)
        .status(DduduStatus.UNCOMPLETED)
        .scheduledOn(scheduledOn)
        .build();
  }

  public Ddudu switchStatus() {
    return getFullBuilder()
        .status(status.switchStatus())
        .build();
  }

  public Ddudu changeName(String name) {
    validateName(name);
    return getFullBuilder()
        .name(name)
        .build();
  }

  public Ddudu update(
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
    DduduBuilder builder = getFullBuilder()
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

  public Ddudu setReminder(int days, int hours, int minutes) {
    LocalDateTime reminder = validateReminder(days, hours, minutes);

    return getFullBuilder()
        .remindAt(reminder)
        .build();
  }

  public Ddudu cancelReminder() {
    return getFullBuilder()
        .remindAt(null)
        .build();
  }

  public Duration getRemindDifference() {
    checkState(
        Objects.nonNull(beginAt) && Objects.nonNull(remindAt),
        DduduErrorCode.UNABLE_TO_GET_REMINDER.getCodeName()
    );

    LocalDateTime scheduledAt = scheduledOn.atTime(beginAt);

    checkState(
        scheduledAt.isAfter(remindAt),
        DduduErrorCode.REMINDER_NOT_AFTER_NOW.getCodeName()
    );

    return Duration.between(remindAt, scheduledAt);
  }

  private DduduBuilder getFullBuilder() {
    return Ddudu.builder()
        .id(this.id)
        .goalId(this.goalId)
        .userId(this.userId)
        .repeatDduduId(this.repeatDduduId)
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
    checkArgument(Objects.nonNull(goalId), DduduErrorCode.NULL_GOAL_VALUE.getCodeName());
    checkArgument(Objects.nonNull(userId), DduduErrorCode.NULL_USER.getCodeName());
    validateName(name);
    validateMemo(memo);
    validatePeriod(beginAt, endAt);
  }

  private void validateName(String name) {
    checkArgument(StringUtils.isNotBlank(name), DduduErrorCode.BLANK_NAME.getCodeName());
    checkArgument(
        name.length() <= MAX_NAME_LENGTH,
        DduduErrorCode.EXCESSIVE_NAME_LENGTH.getCodeName()
    );
  }

  private void validateMemo(String memo) {
    if (isNull(memo)) {
      return;
    }

    checkArgument(
        memo.length() <= MAX_MEMO_LENGTH,
        DduduErrorCode.EXCESSIVE_MEMO_LENGTH.getCodeName()
    );
  }

  private void validatePeriod(LocalTime beginAt, LocalTime endAt) {
    if (isNull(beginAt) || isNull(endAt)) {
      return;
    }

    checkArgument(
        !beginAt.isAfter(endAt),
        DduduErrorCode.UNABLE_TO_FINISH_BEFORE_BEGIN.getCodeName()
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
        DduduErrorCode.NEGATIVE_REMINDER_INPUT_EXISTS.getCodeName()
    );
    checkArgument(
        hasStartTime(),
        DduduErrorCode.BEGIN_AT_REQUIRED_FOR_REMINDER.getCodeName()
    );

    Duration offset = Duration.ofDays(days)
        .plusHours(hours)
        .plusMinutes(minutes);

    checkArgument(
        !offset.isZero(),
        DduduErrorCode.ZERO_REMINDER.getCodeName()
    );

    LocalDateTime reminder = scheduledOn.atTime(beginAt)
        .minusDays(days)
        .minusHours(hours)
        .minusMinutes(minutes);

    checkArgument(
        reminder.isAfter(LocalDateTime.now()),
        DduduErrorCode.REMINDER_NOT_AFTER_NOW.getCodeName()
    );

    return reminder;
  }

}
