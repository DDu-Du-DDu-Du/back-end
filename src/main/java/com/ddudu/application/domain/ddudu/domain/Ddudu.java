package com.ddudu.application.domain.ddudu.domain;

import static com.google.common.base.Preconditions.checkArgument;

import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import com.ddudu.application.domain.ddudu.exception.DduduErrorCode;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.user.domain.User;
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
    validate(goal, user, name);

    this.id = id;
    this.goal = goal;
    this.user = user;
    this.name = name;
    this.status = Objects.requireNonNullElse(status, DEFAULT_STATUS);
    this.isPostponed = Objects.requireNonNullElse(isPostponed, false);
    this.beginAt = Objects.requireNonNullElse(beginAt, LocalDateTime.now());
    this.endAt = endAt;
  }

  public Ddudu applyTodoUpdates(Goal goal, String name, LocalDateTime beginAt) {
    validate(goal, user, name);

    return Ddudu.builder()
        .id(this.id)
        .goal(goal)
        .user(user)
        .name(name)
        .status(this.status)
        .isPostponed(this.isPostponed)
        .beginAt(beginAt)
        .endAt(this.endAt)
        .build();
  }

  public Ddudu switchStatus() {
    DduduBuilder dduduBuilder = Ddudu.builder()
        .id(this.id)
        .goal(this.goal)
        .user(this.user)
        .name(this.name)
        .isPostponed(this.isPostponed)
        .beginAt(this.beginAt);

    if (this.status == DduduStatus.UNCOMPLETED) {
      return dduduBuilder
          .status(DduduStatus.COMPLETE)
          .endAt(LocalDateTime.now())
          .build();
    }

    return dduduBuilder
        .status(DduduStatus.UNCOMPLETED)
        .endAt(null)
        .build();
  }

  public boolean isCreatedByUser(Long userId) {
    return Objects.equals(this.user.getId(), userId);
  }

  private void validate(Goal goal, User user, String name) {
    checkArgument(Objects.nonNull(goal), DduduErrorCode.NULL_GOAL_VALUE.getCodeName());
    checkArgument(Objects.nonNull(user), DduduErrorCode.NULL_USER.getCodeName());
    validateTodo(name);
  }

  private void validateTodo(String name) {
    checkArgument(!name.isBlank(), DduduErrorCode.BLANK_NAME.getCodeName());
    checkArgument(
        name.length() <= MAX_NAME_LENGTH, DduduErrorCode.EXCESSIVE_NAME_LENGTH.getCodeName());
  }

}
