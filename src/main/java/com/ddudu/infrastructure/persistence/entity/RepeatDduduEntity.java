package com.ddudu.infrastructure.persistence.entity;

import com.ddudu.application.domain.repeat_ddudu.domain.RepeatDdudu;
import com.ddudu.application.domain.repeat_ddudu.domain.RepeatPattern;
import com.ddudu.application.domain.repeat_ddudu.domain.enums.RepeatType;
import com.ddudu.infrastructure.persistence.converter.RepeatPatternConverter;
import com.ddudu.old.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "repeat_ddudus")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class RepeatDduduEntity extends BaseEntity {

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
  @Column(
      name = "name",
      length = 50,
      nullable = false
  )
  private String name;

  @Column(
      name = "repeat_type",
      length = 20,
      nullable = false,
      columnDefinition = "VARCHAR"
  )
  @Enumerated(EnumType.STRING)
  private RepeatType repeatType;

  @Convert(converter = RepeatPatternConverter.class)
  @Column(
      name = "repeat_info",
      columnDefinition = "VARCHAR(500)"
  )
  private RepeatPattern repeatInfo;

  @Column(
      name = "start_date",
      columnDefinition = "DATE",
      nullable = false
  )
  private LocalDate startDate;

  @Column(
      name = "end_date",
      columnDefinition = "DATE",
      nullable = false
  )
  private LocalDate endDate;

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

  public static RepeatDduduEntity from(RepeatDdudu repeatDdudu) {
    return RepeatDduduEntity.builder()
        .id(repeatDdudu.getId())
        .goal(GoalEntity.builder()
            .id(repeatDdudu.getGoalId())
            .build()
        )
        .name(repeatDdudu.getName())
        .repeatType(repeatDdudu.getRepeatType())
        .repeatInfo(repeatDdudu.getRepeatPattern())
        .startDate(repeatDdudu.getStartDate())
        .endDate(repeatDdudu.getEndDate())
        .beginAt(repeatDdudu.getBeginAt())
        .endAt(repeatDdudu.getEndAt())
        .build();
  }

  public RepeatDdudu toDomain() {
    return RepeatDdudu.builder()
        .id(id)
        .goalId(goal.getId())
        .name(name)
        .repeatType(repeatType)
        .repeatPattern(repeatInfo)
        .startDate(startDate)
        .endDate(endDate)
        .beginAt(beginAt)
        .endAt(endAt)
        .build();
  }

  public void update(RepeatDdudu repeatDdudu) {
    this.name = repeatDdudu.getName();
    this.repeatType = repeatDdudu.getRepeatType();
    this.repeatInfo = repeatDdudu.getRepeatPattern();
    this.startDate = repeatDdudu.getStartDate();
    this.endDate = repeatDdudu.getEndDate();
    this.beginAt = repeatDdudu.getBeginAt();
    this.endAt = repeatDdudu.getEndAt();
  }

}
