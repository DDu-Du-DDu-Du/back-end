package com.ddudu.application.service;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.goal.dto.response.GoalSummaryResponse;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.port.in.RetrieveAllGoalsUseCase;
import com.ddudu.application.port.out.GoalLoaderPort;
import com.ddudu.application.port.out.UserLoaderPort;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RetrieveAllGoalsService implements RetrieveAllGoalsUseCase {

  private final UserLoaderPort userLoaderPort;
  private final GoalLoaderPort goalLoaderPort;

  @Override
  public List<GoalSummaryResponse> findAllByUser(Long userId) {
    User user = findUser(userId);

    return goalLoaderPort.findAllByUser(user)
        .stream()
        .map(GoalSummaryResponse::from)
        .toList();
  }

  private User findUser(Long userId) {
    return userLoaderPort.findById(userId)
        .orElseThrow(
            () -> new EntityNotFoundException(GoalErrorCode.USER_NOT_EXISTING.getCodeName()));
  }

}
