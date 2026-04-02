package com.ddudu.infra.mysql.planning.todo.entity;

import com.ddudu.domain.planning.todo.aggregate.Todo;
import com.ddudu.domain.planning.todo.aggregate.enums.TodoStatus;
import com.ddudu.infra.mysql.common.entity.BaseEntity;
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

  @Column(
      name = "goal_id",
      nullable = false
  )
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
    return TodoEntity.builder()
        .id(todo.getId())
        .goalId(todo.getGoalId())
        .userId(todo.getUserId())
        .repeatTodoId(todo.getRepeatTodoId())
        .name(todo.getName())
        .memo(todo.getMemo())
        .status(todo.getStatus())
        .postponedAt(todo.getPostponedAt())
        .scheduledOn(todo.getScheduledOn())
        .beginAt(todo.getBeginAt())
        .endAt(todo.getEndAt())
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
    this.name = todo.getName();
    this.memo = todo.getMemo();
    this.status = todo.getStatus();
    this.postponedAt = todo.getPostponedAt();
    this.scheduledOn = todo.getScheduledOn();
    this.beginAt = todo.getBeginAt();
    this.endAt = todo.getEndAt();
  }

}
