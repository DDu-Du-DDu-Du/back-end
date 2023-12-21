package com.ddudu.todo.domain;

import static io.micrometer.common.util.StringUtils.isBlank;
import static java.util.Objects.isNull;

import com.ddudu.goal.domain.Goal;
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
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Entity
@Table(name = "todo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EnableJpaAuditing
public class Todo {

  private static final TodoStatus DEFAULT_STATUS = TodoStatus.UNCOMPLETED;
  private static final Boolean DEFAULT_IS_DELETED = false;

  private static final int MAX_NAME_LENGTH = 50;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "goal_id")
  private Goal goal;

  @Column(name = "name", length = 50, nullable = false)
  private String name;

  @Column(name = "status", nullable = false, columnDefinition = "VARCHAR", length = 20)
  @Enumerated(EnumType.STRING)
  private TodoStatus status = DEFAULT_STATUS;

  @Column(name = "begin_at", nullable = false)
  private LocalDateTime beginAt;

  @Column(name = "end_at")
  private LocalDateTime endAt;

  @Column(name = "is_deleted", nullable = false)
  private boolean isDeleted = DEFAULT_IS_DELETED;

  @Builder
  public Todo(Goal goal, String name, LocalDateTime beginAt) {
    validateGoal(goal);
    validateTodo(name);

    this.goal = goal;
    this.name = name;
    this.beginAt = isNull(beginAt) ? LocalDateTime.now() : beginAt;
  }

  private void validateGoal(Goal goal) {
    if (isNull(goal)) {
      throw new IllegalArgumentException("목표는 필수값입니다.");
    }
  }

  private void validateTodo(String name) {
    if (isBlank(name)) {
      throw new IllegalArgumentException("할 일은 필수값입니다.");
    }

    if (name.length() > MAX_NAME_LENGTH) {
      throw new IllegalArgumentException("할 일은 최대 " + MAX_NAME_LENGTH + "자 입니다.");
    }
  }

}
