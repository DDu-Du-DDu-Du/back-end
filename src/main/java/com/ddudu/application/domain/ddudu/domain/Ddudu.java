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

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Ddudu {

  private static final DduduStatus DEFAULT_STATUS = DduduStatus.UNCOMPLETED;
  private static final int MAX_NAME_LENGTH = 50;

  @EqualsAndHashCode.Include
  private final Long id;
  private final Long goalId;
  private final Long userId;
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
      Long id, Long goalId, Long userId, String name, Boolean isPostponed, DduduStatus status,
      LocalDate scheduledOn, LocalTime beginAt, LocalTime endAt,
      // TODO: delete below fields after migration as left for avoidance of compile errors
      Goal goal, User user
  ) {
    validate(goalId, userId, name, beginAt, endAt);

    this.id = id;
    this.goalId = goalId;
    this.userId = userId;
    this.name = name;
    this.status = Objects.requireNonNullElse(status, DEFAULT_STATUS);
    this.isPostponed = Objects.requireNonNullElse(isPostponed, false);
    this.scheduledOn = Objects.requireNonNullElse(scheduledOn, LocalDate.now());
    this.beginAt = beginAt;
    this.endAt = endAt;
  }

  public void checkAuthority(Long loginId) {
    if (!isCreatedByUser(loginId)) {
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

  public Ddudu applyTodoUpdates(Goal goal, String name, LocalDateTime beginAt) {
    return getFullBuilder()
        .goal(goal)
        .name(name)
        .beginAt(LocalTime.from(beginAt))
        .build();
  }

  public Ddudu switchStatus() {
    if (this.status == DduduStatus.UNCOMPLETED) {
      return getFullBuilder()
          .status(DduduStatus.COMPLETE)
          .endAt(LocalTime.now())
          .build();
    }

    return getFullBuilder()
        .status(DduduStatus.UNCOMPLETED)
        .endAt(null)
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
    validateTodo(name);
    validatePeriod(beginAt, endAt);
  }

  private void validateTodo(String name) {
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
