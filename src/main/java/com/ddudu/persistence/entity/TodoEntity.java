package com.ddudu.persistence.entity;

import com.ddudu.application.common.BaseEntity;
import com.ddudu.application.todo.domain.Todo;
import com.ddudu.application.todo.domain.TodoStatus;
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
@Table(name = "todo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "goal_id", nullable = false)
  private GoalEntity goal;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private UserEntity user;

  @Column(name = "name", length = 50, nullable = false)
  private String name;

  @Column(name = "status", nullable = false, columnDefinition = "VARCHAR", length = 20)
  @Enumerated(EnumType.STRING)
  private TodoStatus status;

  @Column(name = "begin_at", nullable = false)
  private LocalDateTime beginAt;

  @Column(name = "end_at")
  private LocalDateTime endAt;

  @Builder
  public TodoEntity(
    Long id, GoalEntity goal, UserEntity user, String name, TodoStatus status,
    LocalDateTime beginAt,
    LocalDateTime endAt, LocalDateTime createdAt, LocalDateTime updatedAt
  ) {
    super(createdAt, updatedAt);

    this.id = id;
    this.goal = goal;
    this.user = user;
    this.name = name;
    this.status = status;
    this.beginAt = beginAt;
    this.endAt = endAt;
  }

  public static TodoEntity from(Todo todo) {
    return TodoEntity.builder()
      .id(todo.getId())
      .goal(GoalEntity.from(todo.getGoal()))
      .user(UserEntity.from(todo.getUser()))
      .name(todo.getName())
      .status(todo.getStatus())
      .beginAt(todo.getBeginAt())
      .endAt(todo.getEndAt())
      .createdAt(todo.getCreatedAt())
      .updatedAt(todo.getUpdatedAt())
      .build();
  }

  public Todo toDomain() {
    return Todo.builder()
      .id(id)
      .goal(goal.toDomain())
      .user(user.toDomain())
      .name(name)
      .status(status)
      .beginAt(beginAt)
      .endAt(endAt)
      .createdAt(getCreatedAt())
      .updatedAt(getUpdatedAt())
      .build();
  }

}
