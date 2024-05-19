package com.ddudu.infrastructure.persistence.entity;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ddudus")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class DduduEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "goal_id",
      nullable = false
  )
  private GoalEntity goal;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private UserEntity user;

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
      name = "begin_at",
      nullable = false
  )
  private LocalDateTime beginAt;

  @Column(name = "end_at")
  private LocalDateTime endAt;

  @Column(
      name = "is_postponed",
      nullable = false
  )
  private boolean isPostponed;

  public static DduduEntity from(Ddudu ddudu) {
    return DduduEntity.builder()
        .id(ddudu.getId())
        .goal(GoalEntity.from(ddudu.getGoal()))
        .user(UserEntity.from(ddudu.getUser()))
        .name(ddudu.getName())
        .status(ddudu.getStatus())
        .isPostponed(ddudu.isPostponed())
        .beginAt(ddudu.getBeginAt())
        .endAt(ddudu.getEndAt())
        .build();
  }

  public Ddudu toDomain() {
    return Ddudu.builder()
        .id(id)
        .name(name)
        .status(status)
        .isPostponed(isPostponed)
        .beginAt(beginAt)
        .endAt(endAt)
        .build();
  }

  public void update(Ddudu ddudu) {
    this.name = ddudu.getName();
    this.status = ddudu.getStatus();
    this.isPostponed = ddudu.isPostponed();
    this.beginAt = ddudu.getBeginAt();
    this.endAt = ddudu.getEndAt();
  }

}