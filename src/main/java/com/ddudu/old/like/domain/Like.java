package com.ddudu.old.like.domain;

import com.ddudu.old.common.domain.BaseDomain;
import com.ddudu.presentation.api.exception.InvalidParameterException;
import com.ddudu.old.like.exception.LikeErrorCode;
import com.ddudu.old.todo.domain.Todo;
import com.ddudu.old.todo.domain.TodoStatus;
import com.ddudu.application.domain.user.domain.User;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Like extends BaseDomain {

  private Long id;
  private User user;
  private Todo todo;

  @Builder
  public Like(
      Long id, User user, Todo todo, LocalDateTime createdAt, LocalDateTime updatedAt
  ) {
    super(createdAt, updatedAt);
    validate(user, todo);

    this.id = id;
    this.user = user;
    this.todo = todo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Like like = (Like) o;
    if (id != null) {
      return id.equals(like.id);
    } else {
      return super.equals(o);
    }
  }

  @Override
  public int hashCode() {
    return (id != null) ? id.hashCode() : super.hashCode();
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
