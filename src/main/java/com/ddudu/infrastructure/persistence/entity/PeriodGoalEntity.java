package com.ddudu.infrastructure.persistence.entity;

import com.ddudu.application.domain.period_goal.domain.PeriodGoal;
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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @Column(name = "contents")
  private String contents;

  @Column(name = "type", nullable = false, length = 15)
  private String type;

  @Column(name = "plan_date", nullable = false, columnDefinition = "DATE")
  private LocalDate planDate;

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

  public void update(PeriodGoal periodGoal) {
    this.contents = periodGoal.getContents();
  }

}
