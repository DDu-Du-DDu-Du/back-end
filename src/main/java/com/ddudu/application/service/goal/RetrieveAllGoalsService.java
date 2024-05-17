package com.ddudu.application.service.goal;

import com.ddudu.application.annotation.UseCase;
import com.ddudu.application.domain.goal.dto.response.GoalSummaryResponse;
import com.ddudu.application.domain.goal.exception.GoalErrorCode;
import com.ddudu.application.domain.user.domain.User;
import com.ddudu.application.port.in.goal.RetrieveAllGoalsUseCase;
import com.ddudu.application.port.out.goal.GoalLoaderPort;
import com.ddudu.application.port.out.UserLoaderPort;
import java.util.List;
import java.util.MissingResourceException;
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
            () -> new MissingResourceException(
                GoalErrorCode.USER_NOT_EXISTING.getCodeName(),
                User.class.getName(),
                userId.toString()
            ));
  }

}
