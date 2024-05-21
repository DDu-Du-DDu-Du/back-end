package com.ddudu.infrastructure.persistence.entity;

import com.ddudu.application.domain.goal.domain.Goal;
import com.ddudu.application.domain.goal.domain.enums.GoalStatus;
import com.ddudu.application.domain.goal.domain.enums.PrivacyType;
import com.ddudu.old.common.BaseEntity;
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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "goals")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class GoalEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(
      name = "name",
      nullable = false,
      length = 50
  )
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "user_id",
      nullable = false
  )
  private UserEntity user;

  @Column(
      name = "status",
      nullable = false,
      columnDefinition = "VARCHAR",
      length = 20
  )
  @Enumerated(EnumType.STRING)
  private GoalStatus status;

  @Column(
      name = "color",
      nullable = false,
      columnDefinition = "CHAR",
      length = 6
  )
  private String color;

  @Column(
      name = "privacy",
      nullable = false,
      columnDefinition = "VARCHAR",
      length = 20
  )
  @Enumerated(EnumType.STRING)
  private PrivacyType privacyType;

  @Builder
  public GoalEntity(
      Long id, String name, UserEntity user, GoalStatus status, String color,
      PrivacyType privacyType,
      LocalDateTime createdAt, LocalDateTime updatedAt
  ) {
    super(createdAt, updatedAt);

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
        .user(UserEntity.withOnlyId(goal.getUserId()))
        .status(goal.getStatus())
        .color(goal.getColor())
        .privacyType(goal.getPrivacyType())
        .build();
  }

  public static GoalEntity withOnlyId(Long goalId) {
    return GoalEntity.builder()
        .id(goalId)
        .build();
  }

  public Goal toDomain() {
    return Goal.builder()
        .id(id)
        .name(name)
        .userId(user.getId())
        .status(status)
        .color(color)
        .privacyType(privacyType)
        .build();
  }

  public GoalEntity update(Goal goal) {
    this.name = goal.getName();
    this.user = UserEntity.withOnlyId(goal.getUserId());
    this.status = goal.getStatus();
    this.color = goal.getColor();
    this.privacyType = goal.getPrivacyType();

    return this;
  }

}
