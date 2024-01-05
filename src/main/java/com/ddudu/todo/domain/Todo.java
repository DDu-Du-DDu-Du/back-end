package com.ddudu.todo.domain;

import static io.micrometer.common.util.StringUtils.isBlank;
import static java.util.Objects.isNull;

import com.ddudu.common.BaseEntity;
import com.ddudu.common.exception.InvalidParameterException;
import com.ddudu.goal.domain.Goal;
import com.ddudu.todo.exception.TodoErrorCode;
import com.ddudu.user.domain.User;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "todo")
@SQLRestriction("is_deleted = 0")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SQLRestriction("is_deleted = 0")
public class Todo extends BaseEntity {

  private static final TodoStatus DEFAULT_STATUS = TodoStatus.UNCOMPLETED;
  private static final int MAX_NAME_LENGTH = 50;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "goal_id", nullable = false)
  private Goal goal;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "name", length = 50, nullable = false)
  private String name;

  @Column(name = "status", nullable = false, columnDefinition = "VARCHAR", length = 20)
  @Enumerated(EnumType.STRING)
  private TodoStatus status = DEFAULT_STATUS;

  @Column(name = "begin_at", nullable = false)
  private LocalDateTime beginAt;

  @Column(name = "end_at")
  private LocalDateTime endAt;

  @Builder
  public Todo(Goal goal, User user, String name, LocalDateTime beginAt) {
    validate(goal, user, name);

    this.goal = goal;
    this.user = user;
    this.name = name;
    this.beginAt = isNull(beginAt) ? LocalDateTime.now() : beginAt;
  }

  private void validate(Goal goal, User user, String name) {
    validateGoal(goal);
    validateUser(user);
    validateTodo(name);
  }

  private void validateGoal(Goal goal) {
    if (isNull(goal)) {
      throw new InvalidParameterException(TodoErrorCode.NULL_GOAL_VALUE);
    }
  }

  private void validateUser(User user) {
    if (isNull(user)) {
      throw new IllegalArgumentException("사용자는 필수값입니다.");
    }
  }

  private void validateTodo(String name) {
    if (isBlank(name)) {
      throw new InvalidParameterException(TodoErrorCode.BLANK_NAME);
    }

    if (name.length() > MAX_NAME_LENGTH) {
      throw new InvalidParameterException(TodoErrorCode.EXCESSIVE_NAME_LENGTH);
    }
  }

  public void switchStatus() {
    if (this.status == TodoStatus.UNCOMPLETED) {
      this.status = TodoStatus.COMPLETE;
      this.endAt = LocalDateTime.now();
    } else {
      this.status = TodoStatus.UNCOMPLETED;
      this.endAt = null;
    }
  }

}
