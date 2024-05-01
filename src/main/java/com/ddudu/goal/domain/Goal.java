package com.ddudu.goal.domain;

import static io.micrometer.common.util.StringUtils.isBlank;
import static java.util.Objects.isNull;

import com.ddudu.common.domain.BaseDomain;
import com.ddudu.common.exception.InvalidParameterException;
import com.ddudu.goal.exception.GoalErrorCode;
import com.ddudu.user.domain.User;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Goal extends BaseDomain {

  private static final GoalStatus DEFAULT_STATUS = GoalStatus.IN_PROGRESS;
  private static final PrivacyType DEFAULT_PRIVACY_TYPE = PrivacyType.PRIVATE;
  private static final int MAX_NAME_LENGTH = 50;

  private Long id;
  private String name;
  private User user;
  private GoalStatus status = DEFAULT_STATUS;
  private Color color;
  private PrivacyType privacyType;

  @Builder
  public Goal(
      Long id, String name, User user, GoalStatus status, String color, PrivacyType privacyType,
      LocalDateTime createdAt, LocalDateTime updatedAt, Boolean isDeleted
  ) {
    super(createdAt, updatedAt, isDeleted);
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

  public boolean isCreatedByUser(Long userId) {
    return Objects.deepEquals(this.user.getId(), userId);
  }

  private void validate(String name, User user) {
    validateName(name);
    validateUser(user);
  }

  private void validateName(String name) {
    if (isBlank(name)) {
      throw new InvalidParameterException(GoalErrorCode.BLANK_NAME);
    }

    if (name.length() > MAX_NAME_LENGTH) {
      throw new InvalidParameterException(GoalErrorCode.EXCESSIVE_NAME_LENGTH);
    }
  }

  private void validateUser(User user) {
    if (isNull(user)) {
      throw new IllegalArgumentException("사용자는 필수값입니다.");
    }
  }

}
