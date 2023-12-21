package com.ddudu.goal.domain;

import static io.micrometer.common.util.StringUtils.isBlank;
import static io.micrometer.common.util.StringUtils.isNotBlank;
import static java.util.Objects.isNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "goal")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Goal {

  private static final GoalStatus DEFAULT_STATUS = GoalStatus.IN_PROGRESS;
  private static final String DEFAULT_COLOR = "191919";
  private static final PrivacyType DEFAULT_PRIVACY_TYPE = PrivacyType.PRIVATE;
  private static final Boolean DEFAULT_IS_DELETED = false;

  private static final int MAX_NAME_LENGTH = 50;

  private static final int HEX_COLOR_CODE_LENGTH = 6;
  private static final String HEX_COLOR_PATTERN = "^[0-9A-Fa-f]{" + HEX_COLOR_CODE_LENGTH + "}$";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name", nullable = false, length = 50)
  private String name;

  @Column(name = "status", nullable = false, columnDefinition = "VARCHAR", length = 20)
  @Enumerated(EnumType.STRING)
  private GoalStatus status = DEFAULT_STATUS;

  @Column(name = "color", nullable = false, columnDefinition = "CHAR", length = 6)
  private String color;

  @Column(name = "privacy", nullable = false, columnDefinition = "VARCHAR", length = 20)
  @Enumerated(EnumType.STRING)
  private PrivacyType privacyType;

  @Column(name = "is_deleted", nullable = false)
  private boolean isDeleted = DEFAULT_IS_DELETED;

  @Builder
  public Goal(String name, String color, PrivacyType privacyType) {
    validateName(name);
    validateColor(color);

    this.name = name;
    this.color = isBlank(color) ? DEFAULT_COLOR : color;
    this.privacyType = isNull(privacyType) ? DEFAULT_PRIVACY_TYPE : privacyType;
  }

  private void validateName(String name) {
    if (isNull(name)) {
      throw new IllegalArgumentException("목표명은 필수값입니다.");
    }

    if (name.length() > MAX_NAME_LENGTH) {
      throw new IllegalArgumentException("목표명은 최대 " + MAX_NAME_LENGTH + "자 입니다.");
    }
  }

  private void validateColor(String color) {
    if (isNotBlank(color) && !color.matches(HEX_COLOR_PATTERN)) {
      throw new IllegalArgumentException("올바르지 않은 색상 코드입니다. 색상 코드는 "
          + HEX_COLOR_CODE_LENGTH + "자리 16진수입니다.");
    }
  }

}
