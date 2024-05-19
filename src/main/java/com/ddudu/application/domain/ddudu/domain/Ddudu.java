package com.ddudu.application.domain.ddudu.domain;

import static com.google.common.base.Preconditions.checkArgument;

import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import com.ddudu.application.domain.ddudu.exception.DduduErrorCode;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.user.domain.User;
import io.micrometer.common.util.StringUtils;
import java.time.LocalDateTime;
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
  private final Goal goal;
  private final User user;
  private final String name;
  private final DduduStatus status;
  private final boolean isPostponed;
  private final LocalDateTime beginAt;
  private final LocalDateTime endAt;

  @Builder
  private Ddudu(
      Long id, Goal goal, User user, String name, Boolean isPostponed, DduduStatus status,
      LocalDateTime beginAt, LocalDateTime endAt
  ) {
    validate(name, beginAt, endAt);

    this.id = id;
    this.goal = goal;
    this.user = user;
    this.name = name;
    this.status = Objects.requireNonNullElse(status, DEFAULT_STATUS);
    this.isPostponed = Objects.requireNonNullElse(isPostponed, false);
    this.beginAt = Objects.requireNonNullElse(beginAt, LocalDateTime.now());
    this.endAt = endAt;
  }

  public void checkAuthority(Long loginId) {
    if (!isCreatedByUser(loginId)) {
      throw new SecurityException(DduduErrorCode.INVALID_AUTHORITY.getCodeName());
    }
  }

  public Ddudu setUpPeriod(LocalDateTime beginAt, LocalDateTime endAt) {
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
        .beginAt(beginAt)
        .build();
  }

  public Ddudu switchStatus() {
    if (this.status == DduduStatus.UNCOMPLETED) {
      return getFullBuilder()
          .status(DduduStatus.COMPLETE)
          .endAt(LocalDateTime.now())
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
        .goal(this.goal)
        .user(this.user)
        .name(this.name)
        .status(this.status)
        .isPostponed(this.isPostponed)
        .beginAt(this.beginAt)
        .endAt(this.endAt);
  }

  private void validate(String name, LocalDateTime beginAt, LocalDateTime endAt) {
    validateTodo(name);
    validatePeriod(beginAt, endAt);
  }

  private void validateTodo(String name) {
    checkArgument(StringUtils.isNotBlank(name), DduduErrorCode.BLANK_NAME.getCodeName());
    checkArgument(
        name.length() <= MAX_NAME_LENGTH, DduduErrorCode.EXCESSIVE_NAME_LENGTH.getCodeName());
  }

  private void validatePeriod(LocalDateTime beginAt, LocalDateTime endAt) {
    if (Objects.isNull(beginAt) || Objects.isNull(endAt)) {
      return;
    }

    checkArgument(
        !beginAt.isAfter(endAt), DduduErrorCode.UNABLE_TO_FINISH_BEFORE_BEGIN.getCodeName());
  }

  private boolean isCreatedByUser(Long userId) {
    return Objects.equals(this.user.getId(), userId);
  }

}
