package com.ddudu.infrastructure.persistence.entity;

import com.ddudu.application.domain.period_goal.domain.PeriodGoal;
import com.ddudu.old.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "period_goals")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PeriodGoalEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @Column(name = "contents")
  private String contents;

  @Column(name = "type", nullable = false, length = 15)
  private String type;

  @Column(name = "plan_date", nullable = false, columnDefinition = "DATE")
  private LocalDate planDate;

  @Builder
  public PeriodGoalEntity(
      Long id, UserEntity user, String contents, String type, LocalDate planDate,
      LocalDateTime createdAt, LocalDateTime updatedAt
  ) {
    super(createdAt, updatedAt);

    this.id = id;
    this.user = user;
    this.contents = contents;
    this.type = type;
    this.planDate = planDate;
  }

  public static PeriodGoalEntity from(PeriodGoal periodGoal) {
    return PeriodGoalEntity.builder()
        .id(periodGoal.getId())
        .user(UserEntity.withOnlyId(periodGoal.getUserId()))
        .contents(periodGoal.getContents())
        .type(periodGoal.getType()
            .name())
        .planDate(periodGoal.getPlanDate())
        .build();
  }

  public PeriodGoal toDomain() {
    return PeriodGoal.builder()
        .id(id)
        .userId(user.getId())
        .contents(contents)
        .type(type)
        .planDate(planDate)
        .build();
  }

}
