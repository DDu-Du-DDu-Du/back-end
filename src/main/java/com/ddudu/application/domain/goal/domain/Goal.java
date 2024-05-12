package com.ddudu.application.domain.goal.domain;

import static io.micrometer.common.util.StringUtils.isBlank;
import static java.util.Objects.isNull;

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
public class Goal {

  private static final GoalStatus DEFAULT_STATUS = GoalStatus.IN_PROGRESS;
  private static final PrivacyType DEFAULT_PRIVACY_TYPE = PrivacyType.PRIVATE;
  private static final int MAX_NAME_LENGTH = 50;

  @EqualsAndHashCode.Include
  private Long id;
  private String name;
  private User user;
  private GoalStatus status = DEFAULT_STATUS;
  private PrivacyType privacyType;

  @Getter(AccessLevel.NONE)
  private Color color;

  @Builder
  public Goal(
      Long id, String name, User user, GoalStatus status, String color, PrivacyType privacyType
  ) {
    validate(name, user);

    this.id = id;
    this.name = name;
    this.user = user;
    this.status = isNull(status) ? DEFAULT_STATUS : status;
    this.color = new Color(color);
    this.privacyType = isNull(privacyType) ? DEFAULT_PRIVACY_TYPE : privacyType;
  }

  public String getColor() {
    return color.getCode();
  }

  public void applyGoalUpdates(
      String name, GoalStatus status, String color, PrivacyType privacyType
  ) {
    validateName(name);

    this.name = name;
    this.status = status;
    this.color = new Color(color);
    this.privacyType = isNull(privacyType) ? DEFAULT_PRIVACY_TYPE : privacyType;
  }

  public boolean isCreatedBy(Long userId) {
    return Objects.equals(this.user.getId(), userId);
  }

  private void validate(String name, User user) {
    validateName(name);
    validateUser(user);
  }

  private void validateName(String name) {
    if (isBlank(name)) {
      throw new IllegalArgumentException(GoalErrorCode.BLANK_NAME.getCodeName());
    }

    if (name.length() > MAX_NAME_LENGTH) {
      throw new IllegalArgumentException(GoalErrorCode.EXCESSIVE_NAME_LENGTH.getCodeName());
    }
  }

  private void validateUser(User user) {
    if (isNull(user)) {
      throw new IllegalArgumentException(GoalErrorCode.NULL_USER.getCodeName());
    }
  }

}
