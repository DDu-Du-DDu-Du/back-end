package com.ddudu.infrastructure.planningmysql.periodgoal.entity;

import com.ddudu.domain.planning.periodgoal.aggregate.PeriodGoal;
import com.ddudu.domain.planning.periodgoal.aggregate.enums.PeriodGoalType;
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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "period_goals")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class PeriodGoalEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(
      name = "user_id",
      nullable = false
  )
  private Long userId;

  @Column(name = "contents")
  private String contents;

  @Column(
      name = "type",
      nullable = false,
      columnDefinition = "VARCHAR",
      length = 15
  )
  @Enumerated(EnumType.STRING)
  private PeriodGoalType type;

  @Column(
      name = "plan_date",
      nullable = false,
      columnDefinition = "DATE"
  )
  private LocalDate planDate;

  public static PeriodGoalEntity from(PeriodGoal periodGoal) {
    return PeriodGoalEntity.builder()
        .id(periodGoal.getId())
        .userId(periodGoal.getUserId())
        .contents(periodGoal.getContents())
        .type(periodGoal.getType())
        .planDate(periodGoal.getPlanDate())
        .build();
  }

  public PeriodGoal toDomain() {
    return PeriodGoal.builder()
        .id(id)
        .userId(userId)
        .contents(contents)
        .type(type)
        .planDate(planDate)
        .build();
  }

  public void update(PeriodGoal periodGoal) {
    this.contents = periodGoal.getContents();
  }

}
