package com.ddudu.infra.mysql.planning.repeatddudu.entity;

import com.ddudu.domain.planning.repeatddudu.aggregate.RepeatDdudu;
import com.ddudu.domain.planning.repeatddudu.aggregate.enums.RepeatType;
import com.ddudu.domain.planning.repeatddudu.aggregate.vo.RepeatInfo;
import com.ddudu.domain.planning.repeatddudu.aggregate.vo.RepeatPattern;
import com.ddudu.infra.mysql.common.entity.BaseEntity;
import com.ddudu.infra.mysql.planning.repeatddudu.converter.RepeatInfoConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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

  @Column(
      name = "goal_id",
      nullable = false
  )
  private Long goalId;

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

  @Convert(converter = RepeatInfoConverter.class)
  @Column(
      name = "repeat_info",
      columnDefinition = "VARCHAR(500)"
  )
  private RepeatInfoEntity repeatInfoEntity;

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
    RepeatInfo repeatInfo = repeatDdudu.getRepeatInfo();
    RepeatInfoEntity repeatInfoEntity = RepeatInfoEntity.from(repeatInfo);

    return RepeatDduduEntity.builder()
        .id(repeatDdudu.getId())
        .goalId(repeatDdudu.getGoalId())
        .name(repeatDdudu.getName())
        .repeatType(repeatDdudu.getRepeatType())
        .repeatInfoEntity(repeatInfoEntity)
        .startDate(repeatDdudu.getStartDate())
        .endDate(repeatDdudu.getEndDate())
        .beginAt(repeatDdudu.getBeginAt())
        .endAt(repeatDdudu.getEndAt())
        .build();
  }

  public RepeatDdudu toDomain() {
    return RepeatDdudu.builder()
        .id(id)
        .goalId(goalId)
        .name(name)
        .repeatType(repeatType)
        .repeatPattern(createRepeatPattern())
        .startDate(startDate)
        .endDate(endDate)
        .beginAt(beginAt)
        .endAt(endAt)
        .build();
  }

  public void update(RepeatDdudu repeatDdudu) {
    RepeatInfo repeatInfo = repeatDdudu.getRepeatInfo();

    this.name = repeatDdudu.getName();
    this.repeatType = repeatDdudu.getRepeatType();
    this.repeatInfoEntity = RepeatInfoEntity.from(repeatInfo);
    this.startDate = repeatDdudu.getStartDate();
    this.endDate = repeatDdudu.getEndDate();
    this.beginAt = repeatDdudu.getBeginAt();
    this.endAt = repeatDdudu.getEndAt();
  }

  private RepeatPattern createRepeatPattern() {
    return repeatType.createRepeatPattern(
        repeatInfoEntity.repeatDaysOfWeek(),
        repeatInfoEntity.repeatDaysOfMonth(),
        repeatInfoEntity.lastDayOfMonth()
    );
  }

}
