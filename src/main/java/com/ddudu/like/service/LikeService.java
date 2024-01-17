package com.ddudu.like.service;

import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.common.exception.ForbiddenException;
import com.ddudu.like.domain.Like;
import com.ddudu.like.dto.request.LikeRequest;
import com.ddudu.like.dto.response.LikeResponse;
import com.ddudu.like.exception.LikeErrorCode;
import com.ddudu.like.repository.LikeRepository;
import com.ddudu.todo.domain.Todo;
import com.ddudu.todo.repository.TodoRepository;
import com.ddudu.user.domain.User;
import com.ddudu.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

  private final LikeRepository likeRepository;
  private final UserRepository userRepository;
  private final TodoRepository todoRepository;

  @Transactional
  public LikeResponse toggle(Long loginId, LikeRequest request) {
    checkPermission(loginId, request.userId());

    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new DataNotFoundException(LikeErrorCode.USER_NOT_EXISTING));
    Todo todo = todoRepository.findById(request.todoId())
        .orElseThrow(() -> new DataNotFoundException(LikeErrorCode.TODO_NOT_EXISTING));

    Like existingLike = likeRepository.findByUserAndTodo(user, todo);

    if (existingLike != null) {
      return LikeResponse.from(switchLikeStatus(existingLike));
    }

    Like like = Like.builder()
        .user(user)
        .todo(todo)
        .build();

    return LikeResponse.from(likeRepository.save(like));
  }

  private void checkPermission(Long loginId, Long userId) {
    if (!loginId.equals(userId)) {
      throw new ForbiddenException(LikeErrorCode.INVALID_AUTHORITY);
    }
  }

  private Like switchLikeStatus(Like like) {
    if (!like.isDeleted()) {
      like.delete();
      return like;
    }

    like.undelete();
    return like;
  }

}
