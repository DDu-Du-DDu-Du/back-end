package com.ddudu.persistence.entity;

import com.ddudu.common.BaseEntity;
import com.ddudu.goal.domain.Goal;
import com.ddudu.goal.domain.GoalStatus;
import com.ddudu.goal.domain.PrivacyType;
import com.ddudu.user.domain.User;
import jakarta.persistence.Column;
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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "goal")
@SQLRestriction("is_deleted = 0")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GoalEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name", nullable = false, length = 50)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "status", nullable = false, columnDefinition = "VARCHAR", length = 20)
  @Enumerated(EnumType.STRING)
  private GoalStatus status;

  @Column(name = "color", nullable = false, columnDefinition = "CHAR", length = 6)
  private String color;

  @Column(name = "privacy", nullable = false, columnDefinition = "VARCHAR", length = 20)
  @Enumerated(EnumType.STRING)
  private PrivacyType privacyType;

  @Builder
  public GoalEntity(
      Long id, String name, User user, GoalStatus status, String color, PrivacyType privacyType,
      LocalDateTime createdAt, LocalDateTime updatedAt, Boolean isDeleted
  ) {
    super(createdAt, updatedAt, isDeleted);

    this.id = id;
    this.name = name;
    this.user = user;
    this.status = status;
    this.color = color;
    this.privacyType = privacyType;
  }

  public static GoalEntity from(Goal goal) {
    return GoalEntity.builder()
        .id(goal.getId())
        .name(goal.getName())
        .user(goal.getUser())
        .status(goal.getStatus())
        .color(goal.getColor())
        .privacyType(goal.getPrivacyType())
        .createdAt(goal.getCreatedAt())
        .updatedAt(goal.getUpdatedAt())
        .isDeleted(goal.isDeleted())
        .build();
  }

  public Goal toDomain() {
    return Goal.builder()
        .id(id)
        .name(name)
        .user(user)
        .status(status)
        .color(color)
        .privacyType(privacyType)
        .createdAt(getCreatedAt())
        .updatedAt(getUpdatedAt())
        .isDeleted(isDeleted())
        .build();
  }

}
