package com.modoo.infra.mysql.planning.todo.entity;

import com.modoo.domain.planning.todo.aggregate.Todo;
import com.modoo.domain.planning.todo.aggregate.enums.TodoStatus;
import com.modoo.infra.mysql.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "todos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class TodoEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "goal_id")
  private Long goalId;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "repeat_todo_id")
  private Long repeatTodoId;

  @Column(
      name = "name",
      length = 50,
      nullable = false
  )
  private String name;

  @Column(
      name = "memo",
      length = 2000
  )
  private String memo;

  @Column(
      name = "status",
      nullable = false,
      columnDefinition = "VARCHAR",
      length = 20
  )
  @Enumerated(EnumType.STRING)
  private TodoStatus status;

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
      name = "postponed_at",
      columnDefinition = "TIMESTAMP"
  )
  private LocalDateTime postponedAt;

  public static TodoEntity from(Todo todo) {
    Todo utcTodo = todo.convert(ZoneOffset.UTC);

    return TodoEntity.builder()
        .id(utcTodo.getId())
        .goalId(utcTodo.getGoalId())
        .userId(utcTodo.getUserId())
        .repeatTodoId(utcTodo.getRepeatTodoId())
        .name(utcTodo.getName())
        .memo(utcTodo.getMemo())
        .status(utcTodo.getStatus())
        .postponedAt(utcTodo.getPostponedAt())
        .scheduledOn(utcTodo.getScheduledOn())
        .beginAt(utcTodo.getBeginAt())
        .endAt(utcTodo.getEndAt())
        .build();
  }

  public Todo toDomain() {
    return Todo.builder()
        .id(id)
        .userId(userId)
        .goalId(goalId)
        .repeatTodoId(repeatTodoId)
        .name(name)
        .memo(memo)
        .status(status)
        .postponedAt(postponedAt)
        .scheduledOn(scheduledOn)
        .beginAt(beginAt)
        .endAt(endAt)
        .build();
  }

  public void update(Todo todo) {
    Todo utcTodo = todo.convert(ZoneOffset.UTC);

    this.goalId = utcTodo.getGoalId();
    this.name = utcTodo.getName();
    this.memo = utcTodo.getMemo();
    this.status = utcTodo.getStatus();
    this.postponedAt = utcTodo.getPostponedAt();
    this.scheduledOn = utcTodo.getScheduledOn();
    this.beginAt = utcTodo.getBeginAt();
    this.endAt = utcTodo.getEndAt();
  }

}
