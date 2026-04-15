package com.modoo.infra.mysql.planning.repeattodo.entity;

import com.modoo.domain.planning.repeattodo.aggregate.RepeatTodo;
import com.modoo.domain.planning.repeattodo.aggregate.enums.RepeatType;
import com.modoo.domain.planning.repeattodo.aggregate.vo.RepeatInfo;
import com.modoo.domain.planning.repeattodo.aggregate.vo.RepeatPattern;
import com.modoo.infra.mysql.common.entity.BaseEntity;
import com.modoo.infra.mysql.planning.repeattodo.converter.RepeatInfoConverter;
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
@Table(name = "repeat_todos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class RepeatTodoEntity extends BaseEntity {

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

  public static RepeatTodoEntity from(RepeatTodo repeatTodo) {
    RepeatInfo repeatInfo = repeatTodo.getRepeatInfo();
    RepeatInfoEntity repeatInfoEntity = RepeatInfoEntity.from(repeatInfo);

    return RepeatTodoEntity.builder()
        .id(repeatTodo.getId())
        .goalId(repeatTodo.getGoalId())
        .name(repeatTodo.getName())
        .repeatType(repeatTodo.getRepeatType())
        .repeatInfoEntity(repeatInfoEntity)
        .startDate(repeatTodo.getStartDate())
        .endDate(repeatTodo.getEndDate())
        .beginAt(repeatTodo.getBeginAt())
        .endAt(repeatTodo.getEndAt())
        .build();
  }

  public RepeatTodo toDomain() {
    return RepeatTodo.builder()
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

  public void update(RepeatTodo repeatTodo) {
    RepeatInfo repeatInfo = repeatTodo.getRepeatInfo();

    this.name = repeatTodo.getName();
    this.repeatType = repeatTodo.getRepeatType();
    this.repeatInfoEntity = RepeatInfoEntity.from(repeatInfo);
    this.startDate = repeatTodo.getStartDate();
    this.endDate = repeatTodo.getEndDate();
    this.beginAt = repeatTodo.getBeginAt();
    this.endAt = repeatTodo.getEndAt();
  }

  private RepeatPattern createRepeatPattern() {
    return repeatType.createRepeatPattern(
        repeatInfoEntity.repeatDaysOfWeek(),
        repeatInfoEntity.repeatDaysOfMonth(),
        repeatInfoEntity.lastDayOfMonth()
    );
  }

}
