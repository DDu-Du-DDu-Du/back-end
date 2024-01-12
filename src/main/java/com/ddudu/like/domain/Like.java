package com.ddudu.like.domain;

import com.ddudu.common.BaseEntity;
import com.ddudu.common.exception.InvalidParameterException;
import com.ddudu.like.exception.LikeErrorCode;
import com.ddudu.todo.domain.Todo;
import com.ddudu.todo.domain.TodoStatus;
import com.ddudu.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "likes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Like extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "todo_id", nullable = false)
  private Todo todo;

  @Builder
  public Like(User user, Todo todo) {
    validate(user, todo);

    this.user = user;
    this.todo = todo;
  }

  private void validate(User user, Todo todo) {
    if (Objects.isNull(user)) {
      throw new InvalidParameterException(LikeErrorCode.NULL_USER);
    }

    if (Objects.isNull(todo)) {
      throw new InvalidParameterException(LikeErrorCode.NULL_TODO);
    }

    if (user.equals(todo.getUser())) {
      throw new InvalidParameterException(LikeErrorCode.SELF_LIKE_UNAVAILABLE);
    }

    if (todo.getStatus()
        .equals(TodoStatus.UNCOMPLETED)) {
      throw new InvalidParameterException(LikeErrorCode.UNAVAILABLE_UNCOMPLETED_TODO);
    }
  }

}
