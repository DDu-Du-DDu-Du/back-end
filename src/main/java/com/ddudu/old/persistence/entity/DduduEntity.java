package com.ddudu.old.persistence.entity;

import static java.util.Objects.isNull;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.ddudu.domain.enums.DduduStatus;
import com.ddudu.infrastructure.persistence.entity.GoalEntity;
import com.ddudu.infrastructure.persistence.entity.UserEntity;
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
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ddudus")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DduduEntity extends BaseEntity {

  private static final DduduStatus DEFAULT_STATUS = DduduStatus.UNCOMPLETED;
  private static final boolean DEFAULT_IS_POSTPONED = false;

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
      nullable = false,
      columnDefinition = "TINYINT(1)"
  )
  private boolean isPostponed;

  @Builder
  public DduduEntity(
      Long id, GoalEntity goal, UserEntity user, String name, DduduStatus status,
      LocalDateTime beginAt, LocalDateTime endAt, Boolean isPostponed
  ) {
    this.id = id;
    this.goal = goal;
    this.user = user;
    this.name = name;
    this.status = isNull(status) ? DEFAULT_STATUS : status;
    this.beginAt = beginAt;
    this.endAt = endAt;
    this.isPostponed = isNull(isPostponed) ? DEFAULT_IS_POSTPONED : isPostponed;
  }

  public static DduduEntity from(Ddudu ddudu) {
    return DduduEntity.builder()
        .id(ddudu.getId())
        .goal(GoalEntity.from(ddudu.getGoal()))
        .user(UserEntity.from(ddudu.getUser()))
        .name(ddudu.getName())
        .status(ddudu.getStatus())
        .beginAt(ddudu.getBeginAt())
        .endAt(ddudu.getEndAt())
        .build();
  }

  public Ddudu toDomain() {
    return Ddudu.builder()
        .id(id)
        .goal(goal.toDomain())
        .user(user.toDomain())
        .name(name)
        .status(status)
        .beginAt(beginAt)
        .endAt(endAt)
        .build();
  }

}
