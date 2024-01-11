package com.ddudu.like.service;

import com.ddudu.common.exception.DataNotFoundException;
import com.ddudu.common.exception.DuplicateResourceException;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

  private final LikeRepository likeRepository;
  private final UserRepository userRepository;
  private final TodoRepository todoRepository;

  @Transactional
  @Validated
  public LikeResponse create(Long loginId, @Valid LikeRequest request) {
    checkPermission(loginId, request.userId());

    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new DataNotFoundException(LikeErrorCode.USER_NOT_EXISTING));
    Todo todo = todoRepository.findById(request.todoId())
        .orElseThrow(() -> new DataNotFoundException(LikeErrorCode.TODO_NOT_EXISTING));

    if (likeRepository.existsByUserAndTodo(user, todo)) {
      throw new DuplicateResourceException(LikeErrorCode.ALREADY_LIKED);
    }

    Like like = Like.builder()
        .user(user)
        .todo(todo)
        .build();

    return LikeResponse.from(likeRepository.save(like));
  }

  @Transactional
  public void delete(Long loginId, Long id) {
    likeRepository.findById(id)
        .ifPresent(like -> {
          checkPermission(loginId, like.getUser()
              .getId());
          like.delete();
        });
  }

  private void checkPermission(Long loginId, Long userId) {
    if (!loginId.equals(userId)) {
      throw new ForbiddenException(LikeErrorCode.INVALID_AUTHORITY);
    }
  }

}
