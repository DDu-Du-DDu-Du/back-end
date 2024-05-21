package com.ddudu.application.domain.goal.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static io.micrometer.common.util.StringUtils.isNotBlank;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.ddudu.application.domain.goal.domain.enums.GoalStatus;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.application.domain.goal.domain.vo.Color;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.domain.user.domain.User;
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
  public Goal(
      Long id, String name, Long userId, GoalStatus status, String color, PrivacyType privacyType,
      // TODO : remove this after all migration as left for avoidance of compile errors
      User user
  ) {
    validate(name, user, userId);

    this.id = id;
    this.name = name;
    this.userId = isNull(user) ? userId : user.getId();
    this.status = isNull(status) ? DEFAULT_STATUS : status;
    this.color = new Color(color);
    this.privacyType = isNull(privacyType) ? DEFAULT_PRIVACY_TYPE : privacyType;
  }

  public String getColor() {
    return color.getCode();
  }

  public Goal applyGoalUpdates(
      String name, String color, PrivacyType privacyType
  ) {
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

  private void validate(String name, User user, Long userId) {
    validateName(name);
    validateUser(user, userId);
  }

  private void validateName(String name) {
    checkArgument(isNotBlank(name), GoalErrorCode.BLANK_NAME.getCodeName());
    checkArgument(
        name.length() <= MAX_NAME_LENGTH, GoalErrorCode.EXCESSIVE_NAME_LENGTH.getCodeName());
  }

  private void validateUser(User user, Long userId) {
    checkArgument(nonNull(user) || nonNull(userId), GoalErrorCode.NULL_USER.getCodeName());
    checkArgument(
        isNull(user) || isNull(userId) || user.getId()
            .equals(userId), GoalErrorCode.TWO_OWNERS.getCodeName()
    );
    checkArgument(userId > 0, GoalErrorCode.NOT_POSITIVE_USER_ID.getCodeName());
  }

}
