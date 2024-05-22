package com.ddudu.old.like.service;

import com.ddudu.application.domain.ddudu.domain.Ddudu;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.old.like.domain.Like;
import com.ddudu.old.like.domain.LikeRepository;
import com.ddudu.old.like.dto.request.LikeRequest;
import com.ddudu.old.like.dto.response.LikeResponse;
import com.ddudu.old.like.exception.LikeErrorCode;
import com.ddudu.old.todo.domain.OldTodoRepository;
import com.ddudu.old.user.domain.UserRepository;
import com.ddudu.presentation.api.exception.DataNotFoundException;
import com.ddudu.presentation.api.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

  private final LikeRepository likeRepository;
  private final UserRepository userRepository;
  private final OldTodoRepository oldTodoRepository;

  @Transactional
  public LikeResponse toggle(Long loginId, LikeRequest request) {
    checkPermission(loginId, request.userId());

    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new DataNotFoundException(LikeErrorCode.USER_NOT_EXISTING));
    Ddudu ddudu = oldTodoRepository.findById(request.todoId())
        .orElseThrow(() -> new DataNotFoundException(LikeErrorCode.TODO_NOT_EXISTING));

    Like existingLike = likeRepository.findByUserAndTodo(user, ddudu);

    if (existingLike != null) {
      likeRepository.delete(existingLike);
      return LikeResponse.from(existingLike);
    }

    Like like = Like.builder()
        .user(user)
        .ddudu(ddudu)
        .build();

    return LikeResponse.from(likeRepository.save(like));
  }

  private void checkPermission(Long loginId, Long userId) {
    if (!loginId.equals(userId)) {
      throw new ForbiddenException(LikeErrorCode.INVALID_AUTHORITY);
    }
  }

}
