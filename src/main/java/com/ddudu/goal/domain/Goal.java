package com.ddudu.goal.domain;

import static io.micrometer.common.util.StringUtils.isBlank;
import static java.util.Objects.isNull;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "goal")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Goal {

  private static final GoalStatus DEFAULT_STATUS = GoalStatus.IN_PROGRESS;
  private static final PrivacyType DEFAULT_PRIVACY_TYPE = PrivacyType.PRIVATE;
  private static final Boolean DEFAULT_IS_DELETED = false;

  private static final int MAX_NAME_LENGTH = 50;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name", nullable = false, length = 50)
  private String name;

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

  @Column(name = "is_deleted", nullable = false)
  private boolean isDeleted = DEFAULT_IS_DELETED;

  @Builder
  public Goal(String name, String color, PrivacyType privacyType) {
    validateName(name);

    this.name = name;
    this.color = new Color(color);
    this.privacyType = isNull(privacyType) ? DEFAULT_PRIVACY_TYPE : privacyType;
  }

  public String getColor() {
    return color.getCode();
  }

  private void validateName(String name) {
    if (isBlank(name)) {
      throw new IllegalArgumentException("목표명은 필수값입니다.");
    }

    if (name.length() > MAX_NAME_LENGTH) {
      throw new IllegalArgumentException("목표명은 최대 " + MAX_NAME_LENGTH + "자 입니다.");
    }
  }

}
