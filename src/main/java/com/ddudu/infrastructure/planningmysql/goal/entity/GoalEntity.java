package com.ddudu.infrastructure.planningmysql.goal.entity;

import com.ddudu.domain.planning.goal.aggregate.Goal;
import com.ddudu.domain.planning.goal.aggregate.enums.GoalStatus;
import com.ddudu.domain.planning.goal.aggregate.enums.PrivacyType;
import com.ddudu.infrastructure.commonmysql.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "goals")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
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

  @Column(
      name = "user_id",
      nullable = false
  )
  private Long userId;

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

  public static GoalEntity from(Goal goal) {
    return GoalEntity.builder()
        .id(goal.getId())
        .name(goal.getName())
        .userId(goal.getUserId())
        .status(goal.getStatus())
        .color(goal.getColor())
        .privacyType(goal.getPrivacyType())
        .build();
  }

  public Goal toDomain() {
    return Goal.builder()
        .id(id)
        .name(name)
        .userId(userId)
        .status(status)
        .color(color)
        .privacyType(privacyType)
        .build();
  }

  public GoalEntity update(Goal goal) {
    this.name = goal.getName();
    this.userId = goal.getUserId();
    this.status = goal.getStatus();
    this.color = goal.getColor();
    this.privacyType = goal.getPrivacyType();

    return this;
  }

}
