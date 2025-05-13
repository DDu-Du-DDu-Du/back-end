package com.ddudu.domain.planning.goal.aggregate;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import com.ddudu.common.exception.GoalErrorCode;
import com.ddudu.domain.planning.goal.aggregate.enums.GoalStatus;
import com.ddudu.domain.planning.goal.aggregate.enums.PrivacyType;
import com.ddudu.domain.planning.goal.aggregate.vo.Color;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class Goal {

  private static final GoalStatus DEFAULT_STATUS = GoalStatus.IN_PROGRESS;
  private static final PrivacyType DEFAULT_PRIVACY_TYPE = PrivacyType.PRIVATE;
  private static final int MAX_NAME_LENGTH = 50;

  @EqualsAndHashCode.Include
  private final Long id;
  private final String name;
  private final Long userId;
  private final GoalStatus status;
  private final PrivacyType privacyType;

  @Getter(AccessLevel.NONE)
  private final Color color;

  @Builder
  private Goal(
      Long id,
      String name,
      Long userId,
      GoalStatus status,
      String color,
      PrivacyType privacyType
  ) {
    validate(name, userId);

    this.id = id;
    this.name = name;
    this.userId = userId;
    this.status = isNull(status) ? DEFAULT_STATUS : status;
    this.color = new Color(color);
    this.privacyType = isNull(privacyType) ? DEFAULT_PRIVACY_TYPE : privacyType;
  }

  public String getColor() {
    return color.getCode();
  }

  public Goal applyGoalUpdates(String name, String color, PrivacyType privacyType) {
    validateName(name);

    return Goal.builder()
        .id(id)
        .name(name)
        .userId(userId)
        .status(status)
        .color(color)
        .privacyType(privacyType)
        .build();
  }

  public Goal changeStatus(GoalStatus status) {
    return Goal.builder()
        .id(id)
        .name(name)
        .userId(userId)
        .status(status)
        .color(color.getCode())
        .privacyType(privacyType)
        .build();
  }

  public void validateGoalCreator(Long userId) {
    if (!isCreatedBy(userId)) {
      throw new SecurityException(GoalErrorCode.INVALID_AUTHORITY.getCodeName());
    }
  }

  public boolean isCreatedBy(Long userId) {
    return Objects.equals(this.userId, userId);
  }

  public boolean isDone() {
    return status == GoalStatus.DONE;
  }

  private void validate(String name, Long userId) {
    validateName(name);
    validateUser(userId);
  }

  private void validateName(String name) {
    checkArgument(isNotBlank(name), GoalErrorCode.BLANK_NAME.getCodeName());
    checkArgument(
        name.length() <= MAX_NAME_LENGTH,
        GoalErrorCode.EXCESSIVE_NAME_LENGTH.getCodeName()
    );
  }

  private void validateUser(Long userId) {
    checkArgument(nonNull(userId), GoalErrorCode.NULL_USER.getCodeName());
    checkArgument(userId > 0, GoalErrorCode.NOT_POSITIVE_USER_ID.getCodeName());
  }

}
