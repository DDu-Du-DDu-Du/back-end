package com.ddudu.application.domain.ddudu.domain;

import static io.micrometer.common.util.StringUtils.isBlank;
import static java.util.Objects.isNull;

import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.domain.ddudu.exception.TodoErrorCode;
import com.ddudu.presentation.api.exception.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Ddudu {

  private static final DduduStatus DEFAULT_STATUS = DduduStatus.UNCOMPLETED;
  private static final int MAX_NAME_LENGTH = 50;

  private Long id;
  private Goal goal;
  private User user;
  private String name;
  private DduduStatus status;
  private LocalDateTime beginAt;
  private LocalDateTime endAt;

  @Builder
  public Ddudu(
      Long id, Goal goal, User user, String name, DduduStatus status, LocalDateTime beginAt,
      LocalDateTime endAt
  ) {
    validate(goal, user, name);

    this.id = id;
    this.goal = goal;
    this.user = user;
    this.name = name;
    this.status = isNull(status) ? DEFAULT_STATUS : status;
    this.beginAt = isNull(beginAt) ? LocalDateTime.now() : beginAt;
    this.endAt = isNull(endAt) ? null : endAt;
  }

  public void applyTodoUpdates(Goal goal, String name, LocalDateTime beginAt) {
    validate(goal, user, name);

    this.goal = goal;
    this.name = name;
    this.beginAt = beginAt;
  }

  public void switchStatus() {
    if (this.status == DduduStatus.UNCOMPLETED) {
      this.status = DduduStatus.COMPLETE;
      this.endAt = LocalDateTime.now();
    } else {
      this.status = DduduStatus.UNCOMPLETED;
      this.endAt = null;
    }
  }

  public boolean isCreatedByUser(Long userId) {
    return Objects.deepEquals(this.user.getId(), userId);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Ddudu ddudu = (Ddudu) o;
    if (id != null) {
      return id.equals(ddudu.id);
    } else {
      return super.equals(o);
    }
  }

  @Override
  public int hashCode() {
    return (id != null) ? id.hashCode() : super.hashCode();
  }

  private void validate(Goal goal, User user, String name) {
    validateGoal(goal);
    validateUser(user);
    validateTodo(name);
  }

  private void validateGoal(Goal goal) {
    if (isNull(goal)) {
      throw new InvalidParameterException(TodoErrorCode.NULL_GOAL_VALUE);
    }
  }

  private void validateUser(User user) {
    if (isNull(user)) {
      throw new IllegalArgumentException("사용자는 필수값입니다.");
    }
  }

  private void validateTodo(String name) {
    if (isBlank(name)) {
      throw new InvalidParameterException(TodoErrorCode.BLANK_NAME);
    }

    if (name.length() > MAX_NAME_LENGTH) {
      throw new InvalidParameterException(TodoErrorCode.EXCESSIVE_NAME_LENGTH);
    }
  }

}
