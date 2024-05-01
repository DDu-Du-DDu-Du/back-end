package com.ddudu.like.domain;

import static java.util.Objects.isNull;

import com.ddudu.common.exception.InvalidParameterException;
import com.ddudu.like.exception.LikeErrorCode;
import com.ddudu.todo.domain.Todo;
import com.ddudu.todo.domain.TodoStatus;
import com.ddudu.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Like {

  private Long id;
  private User user;
  private Todo todo;
  private boolean isDeleted = false;

  @Builder
  public Like(Long id, User user, Todo todo, Boolean isDeleted) {
    validate(user, todo);

    this.id = id;
    this.user = user;
    this.todo = todo;
    this.isDeleted = !isNull(isDeleted) && isDeleted;
  }

  private void validate(User user, Todo todo) {
    if (isNull(user)) {
      throw new InvalidParameterException(LikeErrorCode.NULL_USER);
    }

    if (isNull(todo)) {
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

  public void delete() {
    if (!isDeleted) {
      isDeleted = true;
    }
  }

  public void undelete() {
    if (isDeleted) {
      isDeleted = false;
    }
  }

}
