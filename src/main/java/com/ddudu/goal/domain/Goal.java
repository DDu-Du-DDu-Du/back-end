package com.ddudu.goal.domain;

import static io.micrometer.common.util.StringUtils.isBlank;
import static java.util.Objects.isNull;

import com.ddudu.common.BaseEntity;
import com.ddudu.common.exception.InvalidParameterException;
import com.ddudu.goal.exception.GoalErrorCode;
import com.ddudu.user.domain.User;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "goal")
@SQLRestriction("is_deleted = 0")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Goal extends BaseEntity {

  private static final GoalStatus DEFAULT_STATUS = GoalStatus.IN_PROGRESS;
  private static final PrivacyType DEFAULT_PRIVACY_TYPE = PrivacyType.PRIVATE;
  private static final int MAX_NAME_LENGTH = 50;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name", nullable = false, length = 50)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "status", nullable = false, columnDefinition = "VARCHAR", length = 20)
  @Enumerated(EnumType.STRING)
  private GoalStatus status = DEFAULT_STATUS;

  @Embedded
  @AttributeOverride(
      name = "code",
      column = @Column(name = "color", nullable = false, columnDefinition = "CHAR", length = 6)
  )
  private Color color;

  @Column(name = "privacy", nullable = false, columnDefinition = "VARCHAR", length = 20)
  @Enumerated(EnumType.STRING)
  private PrivacyType privacyType;

  @Builder
  public Goal(String name, User user, String color, PrivacyType privacyType) {
    validate(name, user);

    this.name = name;
    this.user = user;
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
