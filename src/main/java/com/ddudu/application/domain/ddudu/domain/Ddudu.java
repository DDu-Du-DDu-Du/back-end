package com.ddudu.application.domain.ddudu.domain;

import static com.google.common.base.Preconditions.checkArgument;

import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import com.ddudu.application.domain.ddudu.exception.DduduErrorCode;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.user.domain.User;
import io.micrometer.common.util.StringUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.BooleanUtils;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Ddudu {

  private static final int MAX_NAME_LENGTH = 50;

  @EqualsAndHashCode.Include
  private final Long id;
  private final Long goalId;
  private final Long userId;
  private final Long repeatableDduduId;
  private final String name;
  private final DduduStatus status;
  private final boolean isPostponed;
  private final LocalDate scheduledOn;
  private final LocalTime beginAt;
  private final LocalTime endAt;

  // TODO: delete below fields after migration as left for avoidance of compile errors
  private final Goal goal = null;
  private final User user = null;

  @Builder
  private Ddudu(
      Long id, Long goalId, Long userId, Long repeatableDduduId, String name, Boolean isPostponed,
      DduduStatus status, String statusValue, LocalDate scheduledOn, LocalTime beginAt,
      LocalTime endAt,
      // TODO: delete below fields after migration as left for avoidance of compile errors
      Goal goal, User user
  ) {
    validate(goalId, userId, name, beginAt, endAt);

    this.id = id;
    this.goalId = goalId;
    this.userId = userId;
    this.repeatableDduduId = repeatableDduduId;
    this.name = name;
    this.status = Objects.requireNonNullElse(status, DduduStatus.from(statusValue));
    this.isPostponed = Objects.requireNonNullElse(isPostponed, false);
    this.scheduledOn = Objects.requireNonNullElse(scheduledOn, LocalDate.now());
    this.beginAt = beginAt;
    this.endAt = endAt;
  }

  public void validateDduduCreator(Long userId) {
    if (!isCreatedByUser(userId)) {
      throw new SecurityException(DduduErrorCode.INVALID_AUTHORITY.getCodeName());
    }
  }

  public Ddudu setUpPeriod(LocalTime beginAt, LocalTime endAt) {
    DduduBuilder builder = getFullBuilder();

    if (Objects.nonNull(beginAt)) {
      builder.beginAt(beginAt);
    }

    if (Objects.nonNull(endAt)) {
      builder.endAt(endAt);
    }

    return builder.build();
  }

  public Ddudu moveDate(LocalDate newDate, Boolean isPostponed) {
    checkArgument(Objects.nonNull(newDate), DduduErrorCode.NULL_DATE_TO_MOVE.getCodeName());

    if (!newDate.isAfter(this.scheduledOn) && !this.isPostponed) {
      checkArgument(
          BooleanUtils.isNotTrue(isPostponed),
          DduduErrorCode.SHOULD_POSTPONE_UNTIL_FUTURE.getCodeName()
      );
    }

    DduduBuilder builder = getFullBuilder()
        .scheduledOn(newDate);

    if (this.status.isCompleted()) {
      return builder.build();
    }

    return builder
        .isPostponed(isPostponed)
        .build();
  }

  public Ddudu reproduceOnDate(LocalDate scheduledOn) {
    checkArgument(
        !scheduledOn.isEqual(this.scheduledOn),
        DduduErrorCode.UNABLE_TO_REPRODUCE_ON_SAME_DATE.getCodeName()
    );

    return getFullBuilder()
        .id(null)
        .isPostponed(false)
        .status(DduduStatus.UNCOMPLETED)
        .scheduledOn(scheduledOn)
        .build();
  }

  // TODO: 마이그레이션 예정
  public Ddudu applyTodoUpdates(Goal goal, String name, LocalDateTime beginAt) {
    return getFullBuilder()
        .goal(goal)
        .name(name)
        .beginAt(LocalTime.from(beginAt))
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

  private DduduBuilder getFullBuilder() {
    return Ddudu.builder()
        .id(this.id)
        .goalId(this.goalId)
        .userId(this.userId)
        .name(this.name)
        .status(this.status)
        .scheduledOn(this.scheduledOn)
        .isPostponed(this.isPostponed)
        .beginAt(this.beginAt)
        .endAt(this.endAt);
  }

  private void validate(
      Long goalId, Long userId, String name, LocalTime beginAt, LocalTime endAt
  ) {
    checkArgument(Objects.nonNull(goalId), DduduErrorCode.NULL_GOAL_VALUE.getCodeName());
    checkArgument(Objects.nonNull(userId), DduduErrorCode.NULL_USER.getCodeName());
    validateName(name);
    validatePeriod(beginAt, endAt);
  }

  private void validateName(String name) {
    checkArgument(StringUtils.isNotBlank(name), DduduErrorCode.BLANK_NAME.getCodeName());
    checkArgument(
        name.length() <= MAX_NAME_LENGTH, DduduErrorCode.EXCESSIVE_NAME_LENGTH.getCodeName());
  }

  private void validatePeriod(LocalTime beginAt, LocalTime endAt) {
    if (Objects.isNull(beginAt) || Objects.isNull(endAt)) {
      return;
    }

    checkArgument(
        !beginAt.isAfter(endAt), DduduErrorCode.UNABLE_TO_FINISH_BEFORE_BEGIN.getCodeName());
  }

  private boolean isCreatedByUser(Long userId) {
    return Objects.equals(this.userId, userId);
  }

}
