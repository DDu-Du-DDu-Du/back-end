package com.ddudu.infrastructure.planningmysql.ddudu.entity;

import com.ddudu.domain.planning.ddudu.aggregate.Ddudu;
import com.ddudu.domain.planning.ddudu.aggregate.enums.DduduStatus;
import com.ddudu.infrastructure.commonmysql.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ddudus")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class DduduEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(
      name = "goal_id",
      nullable = false
  )
  private Long goalId;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "repeat_ddudu_id")
  private Long repeatDduduId;

  @Column(
      name = "name",
      length = 50,
      nullable = false
  )
  private String name;

  @Column(
      name = "status",
      nullable = false,
      columnDefinition = "VARCHAR",
      length = 20
  )
  @Enumerated(EnumType.STRING)
  private DduduStatus status;

  @Column(
      name = "scheduled_on",
      nullable = false,
      columnDefinition = "DATE"
  )
  private LocalDate scheduledOn;

  @Column(
      name = "begin_at",
      columnDefinition = "TIME"
  )
  private LocalTime beginAt;

  @Column(
      name = "end_at",
      columnDefinition = "TIME"
  )
  private LocalTime endAt;

  @Column(
      name = "is_postponed",
      nullable = false
  )
  private boolean isPostponed;

  public static DduduEntity from(Ddudu ddudu) {
    return DduduEntity.builder()
        .id(ddudu.getId())
        .goalId(ddudu.getGoalId())
        .userId(ddudu.getUserId())
        .repeatDduduId(ddudu.getRepeatDduduId())
        .name(ddudu.getName())
        .status(ddudu.getStatus())
        .isPostponed(ddudu.isPostponed())
        .scheduledOn(ddudu.getScheduledOn())
        .beginAt(ddudu.getBeginAt())
        .endAt(ddudu.getEndAt())
        .build();
  }

  public Ddudu toDomain() {
    return Ddudu.builder()
        .id(id)
        .userId(userId)
        .goalId(goalId)
        .repeatDduduId(repeatDduduId)
        .name(name)
        .status(status)
        .isPostponed(isPostponed)
        .scheduledOn(scheduledOn)
        .beginAt(beginAt)
        .endAt(endAt)
        .build();
  }

  public void update(Ddudu ddudu) {
    this.name = ddudu.getName();
    this.status = ddudu.getStatus();
    this.isPostponed = ddudu.isPostponed();
    this.scheduledOn = ddudu.getScheduledOn();
    this.beginAt = ddudu.getBeginAt();
    this.endAt = ddudu.getEndAt();
  }

}
