package com.ddudu.application.like.service;

import com.ddudu.application.common.exception.DataNotFoundException;
import com.ddudu.application.common.exception.ForbiddenException;
import com.ddudu.application.like.domain.Like;
import com.ddudu.application.like.domain.LikeRepository;
import com.ddudu.application.like.dto.request.LikeRequest;
import com.ddudu.application.like.dto.response.LikeResponse;
import com.ddudu.application.like.exception.LikeErrorCode;
import com.ddudu.application.todo.domain.Todo;
import com.ddudu.application.todo.domain.TodoRepository;
import com.ddudu.application.user.domain.User;
import com.ddudu.application.user.domain.UserRepository;
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
      likeRepository.delete(existingLike);
      return LikeResponse.from(existingLike);
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

}
